/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import edu.gmu.isa681.game.Card.SpecialType;
import edu.gmu.isa681.game.Card.Type;
import edu.gmu.isa681.game.PrivatePlayerState;
import edu.gmu.isa681.game.PublicGameState;
import edu.gmu.isa681.game.PublicPlayerState;
import edu.gmu.isa681.game.PublicPlayerState.Status;

public final class GoForward {

  private String uuid = UUID.randomUUID().toString();

  public String getId() {
    return uuid;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    GoForward other = (GoForward) obj;
    return uuid.equals(other.uuid);
  }

  public static interface GameStateListener {
    public void stateChanged(boolean justStarted);
  }

  private Vector<GameStateListener> listeners = new Vector<GameStateListener>();

  public synchronized void addListener(GoForward.GameStateListener callback) {
    if (callback != null) {
      listeners.add(callback);
    }
  }

  // Game variables
  // -----------------------------------------------------------------------------------------------------------------
  
  private PublicGameState publicGameState = new PublicGameState();
  private Hashtable<String, Player> players = new Hashtable<String, Player>();
  private String[] pList;
  private boolean firstTurn;
  private int turn;
  private String lastWhoDidNotPass;
  private final static int DEFAULT_TIMEOUT_IN_SECONDS = 90;
  
  // -----------------------------------------------------------------------------------------------------------------
  
  private SecureRandom rand = new SecureRandom();
  
  public GoForward(List<String> users, GoForward.GameStateListener callback) {
    if (users.size() < 2 || users.size() > 4) {
      throw new IllegalArgumentException("invalid number of users");
    }
    
    pList = new String[users.size()];
    for (int i = 0; i < 4 && i < users.size(); ++i) {
      String username = users.get(i);
      players.put(username, new Player(username));
      pList[i] = username;
    }

    addListener(callback);
  }

  public List<String> getPlayers() {
    return Collections.list(players.keys());
  }
  
  // Game logic
  // -----------------------------------------------------------------------------------------------------------------

  private boolean started = false;

  public synchronized void start() {
    if (started) {
      return;
    }
    // Initiate a new deck
    ArrayList<Card> deck = new ArrayList<Card>();
    
    for (Card.Value v : Card.Value.values()) {
      for (Card.Suit s : Card.Suit.values()) {
        deck.add(Card.createPlayCard(v, s));
      }
    }
    
    Collections.shuffle(deck, rand);

    // Deal cards to players
    int dealTo = 0;
    for (Card c : deck) {
      if(dealTo < pList.length) {
        String pName = pList[dealTo];
        Player p = players.get(pName);
        p.getPrivatePlayerState().getHand().add(c);
        players.put(pName, p);
      }
      dealTo = (dealTo + 1) % pList.length;
    }
    
    for (String name : players.keySet()) {
      Player p = players.get(name);
      Collections.sort(p.getPrivatePlayerState().getHand());
      p.getPublicPlayerState().setCardsCount(p.getPrivatePlayerState().getHand().size());
      players.put(name, p);
    }

    started = true;
    firstTurn = true;
    turn = rand.nextInt(pList.length);
    publicGameState.setNextPlayer(pList[turn]);
    publicGameState.setTimeout(DEFAULT_TIMEOUT_IN_SECONDS);
    restartTimeout();
    
    signalStateChanged(true);
  }

  /**
   * Re-signals the current game state. Useful when players crashes and rejoins.
   */
  public synchronized void resignal() {
    publicGameState.setTimeout(getRemainingTime());
    signalStateChanged(firstTurn);
  }
  
  private enum MoveType { 
    SEQUENCE, THREE_PAIR_SEQUENCE, FOUR_PAIR_SEQUENCE, SINGLE, PAIR, TRIPLE, QUADRUPLE, 
    PASS, FORFEIT, 
    INVALID 
  }
  
  /**
   * Validates the type of the given move. 
   * For a list of all possible move types, see: http://www.pagat.com/climbing/thirteen.html
   * @param move
   * @return {@linkplain MoveType}
   */
  private MoveType validateMoveType(List<Card> move) {
    // check if not null
    if (move == null) {
      return MoveType.INVALID;
    }
    
    int size = move.size();
    
    boolean hasPass = false;
    boolean hasForfeit = false;
    for (Card c : move) {
      if (c == null) {
        return MoveType.INVALID;
      } else {
        hasPass |= c.getType() == Type.PASS;
        hasForfeit |= c.getType() == Type.FORFEIT;
      }
    }
    
    if (hasPass || hasForfeit) {
      return size == 1 ? (hasPass ? MoveType.PASS : MoveType.FORFEIT) : MoveType.INVALID;
    }
    
    if (size >= 3) {
      Collections.sort(move);
      boolean isSequence = true;
      for (int i = 0; i < move.size() - 1; i++) {
        
        if (move.get(i + 1).getValue().ordinal() != move.get(i).getValue().ordinal() + 1) {
          isSequence = false;
          break;
        }
      }
      if (isSequence) {
        return MoveType.SEQUENCE;
      }
    }

    if (size == 6 || size == 8) {
      Collections.sort(move);
      List<Card[]> groupsOfTwo = new ArrayList<Card[]>();
      
      for (int i = 0; i < size; i += 2) {
        Card[] pair = new Card[2];
        pair[0] = move.get(i);
        pair[1] = move.get(i + 1);
        groupsOfTwo.add(pair);
      }
      
      for (Card[] c : groupsOfTwo) {
        if (c[0].getValue() != c[1].getValue()) {
          return MoveType.INVALID;
        }
      }
      
      Card.Value firstVal, secondVal, thirdVal, fourthVal;
      firstVal = groupsOfTwo.get(0)[0].getValue();
      secondVal = groupsOfTwo.get(1)[0].getValue();
      thirdVal = groupsOfTwo.get(2)[0].getValue();
      if (groupsOfTwo.size() == 4) {
        fourthVal = groupsOfTwo.get(3)[0].getValue();
        
        if (firstVal.ordinal() == secondVal.ordinal() - 1 && 
            secondVal.ordinal() == thirdVal.ordinal() - 1 && 
            thirdVal.ordinal() == fourthVal.ordinal() - 1) {
          return MoveType.FOUR_PAIR_SEQUENCE;
        }
      }
      
      if (firstVal.ordinal() == secondVal.ordinal() - 1 && 
          secondVal.ordinal() == thirdVal.ordinal() - 1) {
        return MoveType.THREE_PAIR_SEQUENCE;
      }
      
      return MoveType.INVALID;
    }

    MoveType ret = MoveType.INVALID;
    switch (size) {      
    case 1:
      ret = MoveType.SINGLE;
      break;
      
    case 2:
      if (move.get(0).getValue() == move.get(1).getValue()) {
        ret = MoveType.PAIR;
      }
      break;
      
    case 3:
      if (move.get(0).getValue() == move.get(1).getValue()
          && move.get(0).getValue() == move.get(2).getValue()) {
        ret = MoveType.TRIPLE;
      }
      break;
      
    case 4:
      if (move.get(0).getValue() == move.get(1).getValue()
          && move.get(0).getValue() == move.get(2).getValue()
          && move.get(0).getValue() == move.get(3).getValue()) {
        ret = MoveType.QUADRUPLE;
      }
      break;
      
    default:
      ret = MoveType.INVALID;
      break;
    }
    return ret;
  }
  
  /**
   * Validates the given move based on the current turn and the possible sets of valid move types.
   * @param username
   * @param newMoveType
   * @param newMove
   * @return <code>true</code> if move is valid, <code>false</code> otherwise.
   */
  private boolean validateMove(String username, MoveType newMoveType, List<Card> newMove) {
    boolean valid = false;
    
    if (newMoveType == MoveType.FORFEIT) { // player forfeits!
      valid = true;
        
    } else { // not a forfeit
    
      boolean allPassed = true;
      for (String name : players.keySet()) {
        Player p = players.get(name);
        if (!p.getUsername().equals(username) && p.getPublicPlayerState().getStatus() == Status.MOVED) {
          allPassed = false;
          break;
        }
      }
      
      if (newMoveType != MoveType.INVALID) { // valid move type
        
        if (newMoveType != MoveType.PASS) { // not a pass
          if (firstTurn) { // first turn
            firstTurn = false;
            valid = true;
            
          } else if (username.equals(lastWhoDidNotPass)) { // no one could beat his last play
            valid = true;
            
          } else { // otherwise
            
            List<Card> currentMove = publicGameState.getCurrentMove();
            MoveType moveType = validateMoveType(currentMove);
            
            Collections.sort(newMove);
            Collections.sort(currentMove);
            
            // Same move size, same move type, check highest value
            if (currentMove.size() == newMove.size() && newMoveType == moveType) {
              if (newMove.get(newMove.size() - 1).compareTo(currentMove.get(currentMove.size() - 1)) > 0) {
                valid = true;
              }
              
            } else { // Special cases
              
              if (moveType == MoveType.SINGLE && currentMove.get(0).getValue() == Card.Value.TWO) {
                if (newMoveType == MoveType.THREE_PAIR_SEQUENCE || newMoveType == MoveType.QUADRUPLE) {
                  valid = true;
                }
                
              } else if (moveType == MoveType.PAIR && currentMove.get(0).getValue() == Card.Value.TWO) {
                if (newMoveType == MoveType.FOUR_PAIR_SEQUENCE || newMoveType == MoveType.QUADRUPLE) {
                  valid = true;
                }
                
              } else if ((moveType == MoveType.THREE_PAIR_SEQUENCE || moveType == MoveType.FOUR_PAIR_SEQUENCE) && 
                  newMoveType == MoveType.QUADRUPLE) {
                valid = true;
              }
            }
          }
          
        } else { // player passes
          
          if (!firstTurn && !allPassed) {
            valid = true;
          }
        }
      }
    }
    
    if (valid && newMoveType != MoveType.PASS) {
      lastWhoDidNotPass = username;
    }
    
    return valid;
  }

  /**
   * Determines if the given user has the correct turn
   * @param username
   * @return
   */
  private boolean validateTurn(String username) {
    return pList[turn].equals(username);
  }
  
  /**
   * Checks if the given move cards belong to the hand of the given user.
   * @param username
   * @param newMove
   * @return <code>true</code> if hand contains move cards, <code>false</code> otherwise.
   */
  private boolean validateMoveWithHand(String username, List<Card> newMove) {
    Set<Card> hand = new HashSet<Card>(players.get(username).getPrivatePlayerState().getHand());
    
    for (Card c : newMove) {
      if (c == null || (c.getType() == Type.PLAY && !hand.remove(c))) {
          return false;
      }
    }
    
    return true;
  }
  
  /**
   * Offers the given move to the game engine, where it is accepted only if it is valid.
   * @param username
   * @param newMove
   * @return <code>true</code> if move is accepted, <code>false</code> otherwise.
   */
  public synchronized boolean offerMove(String username, List<Card> newMove) {
    
    MoveType newMoveType = validateMoveType(newMove);
    if (newMoveType == MoveType.FORFEIT) { // forfeits are always accepted
      applyMove(players.get(username), newMoveType, newMove);
      return true;
      
    } else {
      if (newMoveType != MoveType.INVALID) {  // valid move type
        if (validateTurn(username)) { // if valid turn
          if (validateMoveWithHand(username, newMove)) { // valid move compared to hand
            if (validateMove(username, newMoveType, newMove)) { // and valid move value,
              applyMove(players.get(username), newMoveType, newMove); // then apply the move!
              return true;
            }
          }
        }
      }
    }
    
    return false;
  }
  
  private void applyMove(Player player, MoveType newMoveType, List<Card> newMove) {
    if (newMoveType == MoveType.PASS) {
      player.getPublicPlayerState().setStatus(Status.PASSED);
      
    } else if (newMoveType == MoveType.FORFEIT) {
      String victor = null;
      for (String u : pList) {
        if (!u.equals(player.getUsername())) {
          victor = u;
          break;
        }
      }
      
      player.getPublicPlayerState().setStatus(Status.FORFEITED);
      publicGameState.setGameOver(true);
      publicGameState.setVictor(victor);
      
    } else {
      player.getPublicPlayerState().setStatus(Status.MOVED);
      player.getPublicPlayerState().setCardsCount(player.getPublicPlayerState().getCardsCount() - newMove.size());
      
      if (player.getPublicPlayerState().getCardsCount() == 0) {
        publicGameState.setGameOver(true);
        publicGameState.setVictor(player.getUsername());          
      }
    }
    players.put(player.getUsername(), player);
    
    publicGameState.setCurrentPlayer(player.getUsername());
    publicGameState.setCurrentMove(newMove);
    
    if (!publicGameState.isGameOver()) {
      turn = (turn + 1) % pList.length;
      publicGameState.setNextPlayer(pList[turn]);
      publicGameState.setTimeout(DEFAULT_TIMEOUT_IN_SECONDS);
      restartTimeout();
      
    } else {
      publicGameState.setNextPlayer(null);
    }
    
    signalStateChanged();
  }

  public synchronized void forceForfeit(String username) {
    if (!publicGameState.isGameOver()) {
      offerMove(username, Collections.singletonList(Card.createSpecialCard(SpecialType.FORFEIT)));
    }
  }
  
  // timeout logic
  // -----------------------------------------------------------------------------------------------------------------
  
  private int getRemainingTime() {
    return Math.max(0, (int)((System.currentTimeMillis() - timestamp) / 1000));
  }
  
  private void restartTimeout() {
    timeout = DEFAULT_TIMEOUT_IN_SECONDS * 1000;
    restartTimer(timeout);
  }
  
  private long timestamp;
  private long timeout = DEFAULT_TIMEOUT_IN_SECONDS;
  private Timer timer = new Timer(true);
  private TimerTask task = null;
  private void restartTimer(long timeout) {
    if (task != null) {
      task.cancel();
    }
    
    task = new TimerTask() {
      @Override
      public void run() {
        forceForfeit(publicGameState.getNextPlayer());
      }
    };
    
    timestamp = System.currentTimeMillis();
    timer.schedule(task, timeout + 2000); // 2000 tolerance
  }
  
  // -----------------------------------------------------------------------------------------------------------------
  
  
  // for communication with the outer world!
  // -----------------------------------------------------------------------------------------------------------------

  public synchronized PublicGameState getPublicGameState() {
    return publicGameState;
  }

  public synchronized List<PublicPlayerState> getPublicPlayersStates(String exclude) {
    Vector<PublicPlayerState> playersState = new Vector<PublicPlayerState>();
    for (Player p : players.values()) {
      if (!p.getPublicPlayerState().getUsername().equals(exclude)) {
        playersState.add(p.getPublicPlayerState());
      }
    }
    return playersState;
  }

  public synchronized PrivatePlayerState getPrivatePlayerState(String username) {
    return players.get(username).getPrivatePlayerState();
  }

  private synchronized void signalStateChanged() {
    signalStateChanged(false);
  }
  
  private synchronized void signalStateChanged(boolean justStarted) {
    for (GameStateListener callback : listeners) {
      callback.stateChanged(justStarted);
    }
  }
}