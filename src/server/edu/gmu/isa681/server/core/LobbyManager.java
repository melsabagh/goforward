/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.game.Card;
import edu.gmu.isa681.game.GoForward;
import edu.gmu.isa681.game.PublicGameState;
import edu.gmu.isa681.server.core.db.DBHelper;
import edu.gmu.isa681.server.core.db.GameHistory;
import edu.gmu.isa681.server.core.db.UserHistory;
import edu.gmu.isa681.server.core.db.UserStats;

enum LobbyManager {
  INSTANCE;
  
  private Log log = LogFactory.getLog(LobbyManager.class);

  public static interface GameReadyListener {
    public void gameReady(GoForward game);
  }
  
  private Hashtable<String, GoForward> currentGames = new Hashtable<String, GoForward>();
  private Hashtable<String, String> currentPlayers = new Hashtable<String, String>();
  
  private Hashtable<String, Integer> waitingUsers = new Hashtable<String, Integer>();
  private LinkedList<String> waitingQueue = new LinkedList<String>();
  private LinkedList<GameReadyListener> listeners = new LinkedList<GameReadyListener>();
  
  /**
   * Determines if the given game Id belongs to a running game.
   * @param gameId
   * @return <code>true</code> if the game associated with the given id is running, <code>false</code> otherwise.
   */
  public synchronized boolean isGameRunning(String gameId) {
    GoForward game = currentGames.get(gameId);
    return game != null && !game.getPublicGameState().isGameOver(); 
  }
  
  /**
   * Joins a player in. If the player was crashed while being in a running game, he rejoins that specific game. 
   * Otherwise, the player is queued up. 
   * 
   * @see {@link #queuePlayer(String, GameReadyListener)} for servicing details of queued up players.
   * @param username
   * @param listener listener to be notified when a game is ready for the given player
   */
  public synchronized void join(String username, LobbyManager.GameReadyListener listener) {
    String gameId = currentPlayers.get(username);
    
    if (gameId != null) {
      GoForward game = currentGames.get(gameId);
      listener.gameReady(game);
      game.resignal();
      // TODO-FIXME: include crash in the audit? may reveal useful information to attackers
      
    } else {
      queuePlayer(username, listener);
    }
  }
  
  /**
   * Removes a player from the lobby. If the player is crashed, his lobby remains active. 
   * Note: it's the responsibility of the caller to maintain the game and the players status. 
   * 
   * @param username
   * @param isCrashed
   */
  public synchronized void leave(String username, boolean isCrashed) {
    Integer index = waitingUsers.get(username);
    if (index != null) { // was waiting
      waitingQueue.remove((int)index);
      listeners.remove((int)index);
      
    } else {
      String gameId = currentPlayers.get(username);
      
      if (gameId != null) { // was in-game
        if (!isCrashed) { // and didn't crash
          currentPlayers.remove(username);
          
        } else { // was in-game but crashed
          // pass
        }
      } else { // never saw him before!
        // pass
      }
    }
  }
  
  /**
   * Adds a player to the waiting queue. The player is serviced once enough players are available to start a game.
   * @param username
   * @param listener GameReadyListener to be notified when the game is ready.
   */
  private void queuePlayer(String username, LobbyManager.GameReadyListener listener) {
    waitingQueue.add(username);
    listeners.add(listener);
    waitingUsers.put(username, waitingQueue.size() -1);
    
    /*
     * TODO-FIXME: how to deal cards to players who join late?!
     * TODO-FIXME: players who join late will always be at a disadvantage.. unless we take cards from existing players!
     * TODO-FIXME: forfeiting, in the case of more than 2 players, can cause the game to be impossible to finish! 
     */
    
    int enoughPlayers = 2; // + rand.nextInt(3); (don't enable. client GUI can only handle 2).
    createGameLobbyIf(enoughPlayers);
  }
  
  /**
   * Creates a game and joins waiting players as long as enough player are present in the system. Follows a 
   * GI,GI,*:*,*,FCFS queuing model.
   * 
   * @param enoughPlayers
   */
  private void createGameLobbyIf(int enoughPlayers) {
    if (waitingUsers.size() >= enoughPlayers) {
      final ArrayList<String> servicedUsers = new ArrayList<String>();
      final ArrayList<GameReadyListener> servicedListeners = new ArrayList<GameReadyListener>();
      
      for (int i = 0; i < enoughPlayers; ++i) {
        String player = waitingQueue.remove();
        GameReadyListener listener = listeners.remove();
        waitingUsers.remove(player);
        
        servicedUsers.add(player);
        servicedListeners.add(listener);
      }
      
      // create game instance
      final GoForward game = new GoForward(servicedUsers, null);
      
      // save game and players
      for (String player : servicedUsers) {
        currentGames.put(game.getId(), game);
        currentPlayers.put(player, game.getId());
      }
      
      try {
        Date date = new Date();
        // create users history
        for (String user : servicedUsers) {
          UserHistory userHistory = new UserHistory(user, game.getId(), date);
          DBHelper.createUserHistory(userHistory);
        }
        
        // create game history
        GameHistory gameHistory = new GameHistory(game.getId(), date, "");
        gameHistory.setHistory("GAME STARTED.");
        DBHelper.updateGameHistory(gameHistory);
      
        // add game state listener to update the audit and game history
        game.addListener(new GoForward.GameStateListener() {
          private Hashtable<String, List<Card>> hands = new Hashtable<String, List<Card>>();
          
          public void stateChanged(boolean justStarted) {
            
            try {
              if (justStarted) {
                // save hands
                // to be recorded only when the game is over. Remember: this game players 
                // have access to its history
                
                for (String p : game.getPlayers()) {
                  hands.put(p, Collections.unmodifiableList(game.getPrivatePlayerState(p).getHand()));
                }
                
              } else { // otherwise, record moves and victor (if game over)
              
                PublicGameState publicGameState = game.getPublicGameState();
                
                // record moves
                String str = publicGameState.getCurrentPlayer() +":"+ publicGameState.getCurrentMove().toString();
                
                // record victor if game over
                if (publicGameState.isGameOver()) {
                  str += "\n" + "GAME OVER.";
                  str += "\n" + "VICTOR: "+ publicGameState.getVictor();
                }
                DBHelper.appendToGameHistory(game.getId(), str);
              
                // final updates if game over
                if (publicGameState.isGameOver()) {
                  for (String username : game.getPlayers()) {
                    
                    // update stats
                    
                    UserStats oldStats = DBHelper.getStats(username);
                    if (oldStats == null) {
                      oldStats = new UserStats(username);
                    }
                    
                    UserStats newStats = new UserStats(username);
                    
                    if (publicGameState.getVictor().equals(username)) {
                      // check for overflow
                      if (oldStats.getWins() < Integer.MAX_VALUE) {
                        newStats.setWins(oldStats.getWins() +1);
                      } else {
                        newStats.setWins(oldStats.getWins());
                      }
                    } else {
                      // check for overflow
                      if (oldStats.getLosses() < Integer.MAX_VALUE) {
                        newStats.setLosses(oldStats.getLosses() +1);
                      } else {
                        newStats.setLosses(oldStats.getLosses());
                      }
                    }
                    
                    float score = calculateScore(newStats.getWins(), newStats.getLosses());
                    newStats.setScore(score);
                    
                    DBHelper.updateStats(newStats);
                    
                    
                    // add hands to history
                    
                    str = username +" started with: " + hands.get(username).toString();
                    DBHelper.appendToGameHistory(game.getId(), str);
                    
                  }
                }
              }
              
              if (game.getPublicGameState().isGameOver()) {
                for (String player : game.getPlayers()) {
                  currentPlayers.remove(player);
                  currentGames.remove(game.getId());
                }
              }
              
            } catch (SQLException ex) {
              log.fatal(ex.getMessage(), ex);
            }
          };
        });
        
        // inform listeners that the game is ready
        for (GameReadyListener listener : servicedListeners) {
          listener.gameReady(game);
        }
        
        // and start the game
        game.start();
      
      } catch (SQLException ex) {
        log.fatal(ex.getMessage(), ex);
      }
    }
  }

  
  /**
   * Calculates score given the no. of wins and losses as: wins / (wins + losses), i.e., the "Winning Percentage."
   * 
   * @param wins
   * @param losses
   * @return The winning percentage.
   */
  private float calculateScore(int wins, int losses) {
    if (wins > 0) {
      //return (float)wins / (wins + losses); // incorrect! wins + losses may overflow 
      return 1.0f / (float)(1.0f + (double)losses/(double)wins);
    } else {
      return 0;
    }
  }
}
