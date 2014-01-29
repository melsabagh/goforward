/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.controller;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.client.model.Client;
import edu.gmu.isa681.client.model.ClientEventListener;
import edu.gmu.isa681.client.view.AccountCreatedPane;
import edu.gmu.isa681.client.view.CaptchaPane;
import edu.gmu.isa681.client.view.GameBoardPane;
import edu.gmu.isa681.client.view.GameHistoryPane;
import edu.gmu.isa681.client.view.GameOverPane;
import edu.gmu.isa681.client.view.ServerErrorPane;
import edu.gmu.isa681.client.view.HistoryPane;
import edu.gmu.isa681.client.view.LeaderboardPane;
import edu.gmu.isa681.client.view.LoginPane;
import edu.gmu.isa681.client.view.RegisterPane;
import edu.gmu.isa681.client.view.WelcomePane;
import edu.gmu.isa681.ctn.Encodeable;
import edu.gmu.isa681.ctn.UnexpectedTypeException;
import edu.gmu.isa681.ctn.request.CaptchaResponse;
import edu.gmu.isa681.ctn.request.JoinGameRequest;
import edu.gmu.isa681.ctn.request.LoginRequest;
import edu.gmu.isa681.ctn.request.LogoutRequest;
import edu.gmu.isa681.ctn.request.MakeMoveRequest;
import edu.gmu.isa681.ctn.request.RegisterRequest;
import edu.gmu.isa681.ctn.request.Request;
import edu.gmu.isa681.ctn.request.ViewUserHistoryRequest;
import edu.gmu.isa681.ctn.request.ViewStatsRequest;
import edu.gmu.isa681.ctn.response.CaptchaChallenge;
import edu.gmu.isa681.ctn.response.EmptyResponse;
import edu.gmu.isa681.ctn.response.GameStateResponse;
import edu.gmu.isa681.ctn.response.LoginResponse;
import edu.gmu.isa681.ctn.response.LogoutResponse;
import edu.gmu.isa681.ctn.response.RegisterResponse;
import edu.gmu.isa681.ctn.response.Response;
import edu.gmu.isa681.ctn.response.StatusCode;
import edu.gmu.isa681.ctn.response.ViewHistory;
import edu.gmu.isa681.ctn.response.ViewStatsResponse;
import edu.gmu.isa681.game.Card;
import edu.gmu.isa681.game.Card.SpecialType;
import edu.gmu.isa681.game.PrivatePlayerState;
import edu.gmu.isa681.game.PublicPlayerState;
import edu.gmu.isa681.game.PublicPlayerState.Status;
import edu.gmu.isa681.game.PublicStats;
import edu.gmu.isa681.game.PublicHistory;
import edu.gmu.isa681.util.Pair;

public final class Controller {

  private Log log = LogFactory.getLog(Controller.class);
  
  private Client client;
  private JFrame container;
  
  private String player1;
  private String player2;
  
  private final static int SUBMITMOVE_TIMEOUT = 5; // seconds
  
  public int getSubmitMoveTimeout() {
    return SUBMITMOVE_TIMEOUT;
  }
  
  private Hashtable<Class<?>, Container> cache = new Hashtable<Class<?>, Container>();
  private Stack<Container> viewStack = new Stack<Container>();
  
  public Controller(final Client client, final JFrame container) {
    this.client = client;
    this.container = container;
    
    this.client.addEventListener(new ClientEventListener() {
      @Override
      public void responseReceived(Response response) {
        handleResponse(response);
      }

      @Override
      public void exceptionOccured(Throwable ex) {
        handleException(ex);
      }
    });
    
    this.container.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        log.info("WindowClosing");
        
        if (client.isConnected()) {
          logout(WindowEvent.class);
        } else {
          exit();
        }
      }
    });
  }
  
  private synchronized void handleResponse(Encodeable response) {
    
    if (response instanceof EmptyResponse) {
      handleEmptyResponse((EmptyResponse)response);
      
    } else if (response instanceof CaptchaChallenge) {
      handleCaptchaChallenge((CaptchaChallenge)response);
      
    } else if (response instanceof RegisterResponse) {
      handleRegisterResponse((RegisterResponse)response);
      
    } else if (response instanceof LoginResponse) {
      handleLoginResponse((LoginResponse)response);
      
    } else if (response instanceof GameStateResponse) {
      handleGameStateResponse((GameStateResponse)response);
      
    } else if (response instanceof ViewHistory) {
      handleViewUserHistoryResponse((ViewHistory)response);
      
    } else if (response instanceof ViewStatsResponse) {
      handleViewStatsResponse((ViewStatsResponse)response);
      
    } else if (response instanceof LogoutResponse) {
      handleLogoutResponse((LogoutResponse)response);
      
    } else {
      panicAndLogOut(new UnexpectedTypeException(response.toString()));
    }
  }
  
  private synchronized void handleEmptyResponse(EmptyResponse response) {
    if (response.getStatusCode() == StatusCode.OK) {
      // pass
    } else {
      panicAndLogOut(new UnexpectedTypeException(response.toString()));
    }
  }
  
  private synchronized void handleException(Throwable t) {
    panicAndLogOut(t);
  }
  
  public void applicationStarted() {
    LoginPane pane = new LoginPane(this);
    
    cache.put(LoginPane.class, pane);
    viewStack.push(pane);
    
    container.setContentPane(pane);
    container.pack();
  }
  
  private synchronized void handleCaptchaChallenge(CaptchaChallenge response) {
    CaptchaPane pane = null;
    if (cache.containsKey(CaptchaPane.class)) {
      pane = (CaptchaPane) cache.get(CaptchaPane.class);
    } else {
      pane = new CaptchaPane(this);
    }
    
    pane.setCaptcha(response.getImage());
    
    container.setContentPane(pane);
    container.pack();
  }
  
  public void submitCaptcha(String solution) {
    client.sendRequest(new CaptchaResponse(solution));
  }
  
  public void login(String username, String password) {
    try {
      client.connect();
      Request request = new LoginRequest(username, password);
      client.sendRequest(request);
      
    } catch (IOException ex) {
      client.disconnect();
      ((LoginPane)cache.get(LoginPane.class)).setStatusMessage("Connection error");
    }
  }
  
  private synchronized void handleLoginResponse(LoginResponse response) {
    if (response.getStatusCode() == StatusCode.OK) {
      WelcomePane pane = new WelcomePane(this, response.getUsername());
      
      ((LoginPane)cache.get(LoginPane.class)).setPassword("");
      
      cache.put(WelcomePane.class, pane);
      viewStack.push(pane);
      
      container.setContentPane(pane);
      container.pack();
      
      player1 = response.getUsername();
      
    } else {
      client.disconnect();
      
      LoginPane pane = ((LoginPane)cache.get(LoginPane.class)); 
      pane.setStatusMessage(response.getStatusCode().toString());
      container.setContentPane(pane);
    }
  }

  public void register() {
    RegisterPane pane = new RegisterPane(this);
    
    cache.put(RegisterPane.class, pane);
    viewStack.push(pane);
    
    container.setContentPane(pane);
    container.pack();
  }
  
  public void register(String username, String email, String password) {
    try {
      client.connect();
      Request request = new RegisterRequest(username, email, password);
      client.sendRequest(request);
      
    } catch (IOException ex) {
      ((RegisterPane)cache.get(RegisterPane.class)).setStatusMessage("Connection error"); 
    }
  }
  
  private synchronized void handleRegisterResponse(RegisterResponse response) {
    client.disconnect();
    
    if (response.getStatusCode() == StatusCode.OK) {
      AccountCreatedPane pane = new AccountCreatedPane(this, response.getUsername());
      
      viewStack.clear();
      viewStack.push(pane);
      
      container.setContentPane(pane);
      container.pack();
      
      ((LoginPane)cache.get(LoginPane.class)).setUsername(response.getUsername());
      
    } else if (response.getStatusCode() == StatusCode.REGISTRATION_ERROR) {
      RegisterPane pane = (RegisterPane)cache.get(RegisterPane.class);
      StringBuffer details = new StringBuffer();
      for (String d : response.getDetails()) {
        details.append(d);
        details.append('\n');
      }
      
      pane.setStatusMessage(details.toString());
      container.setContentPane(pane);
      
    } else {
      RegisterPane pane = (RegisterPane)cache.get(RegisterPane.class);
      pane.setStatusMessage(response.getStatusCode().toString());
      container.setContentPane(pane);
    }
  }

  public void startGame() {
    Request request = new JoinGameRequest();
    client.sendRequest(request);
  }
  
  private synchronized void handleGameStateResponse(GameStateResponse response) {
    log.info("RECEIVED: "+ response);
    
    if (response.getStatusCode() == StatusCode.INVALID_MOVE) {
      notifyMoveResultListeners(response);
      
    } else if (response.getGameState().getCurrentPlayer() == null) {
      GameBoardPane pane = new GameBoardPane(this);
      
      PrivatePlayerState myState = response.getMyState();
      
      pane.setPlayer1Name(myState.getUsername());
      pane.setPlayer1Cards(myState.getHand());
      
      PublicPlayerState player2State = response.getPlayersState().get(0);
      
      player2 = player2State.getUsername();
      pane.setPlayer2Name(player2);
      pane.setPlayer2Cards(player2State.getCardsCount());
      
      notifyGameStateListeners(response);
      
      cache.put(GameBoardPane.class, pane);
      viewStack.push(pane);
      
      container.setContentPane(pane);
      container.pack();
      
    } else {
      notifyMoveResultListeners(response);
      notifyGameStateListeners(response);
      
      displayIfGameOver(response);
    }
  }
  
  // move timers and logic
  // -----------------------------------------------------------------------------------------------------------------
  
  private ScheduledThreadPoolExecutor timeoutPool; 
  {
    timeoutPool = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
      }
    });
    timeoutPool.setRemoveOnCancelPolicy(true);
  }
  
  private Vector<GameStateListener> gameStateListeners = new Vector<GameStateListener>();
  
  public void addGameStateListener(GameStateListener listener) {
    gameStateListeners.add(listener);
  }
  
  private void notifyGameStateListeners(final GameStateResponse response) {
    if (response != null) {
      for (GameStateListener listener : gameStateListeners) {
        listener.stateChanged(response);
      }
    }
  }
  
  private ScheduledFuture<?> moveBufferTimer;
  private Vector<Pair<Card, MoveResultListener>> moveBuffer = new Vector<Pair<Card, MoveResultListener>>();
  
  private synchronized void restartMoveBufferTimer() {
    if (moveBufferTimer != null) {
      moveBufferTimer.cancel(true);
    }
    
    moveBufferTimer = timeoutPool.schedule(new Runnable() {
      @Override
      public void run() {
        signalMoveBufferTimeouts();
      }
    }, SUBMITMOVE_TIMEOUT, TimeUnit.SECONDS);
  }
  
  private synchronized void signalMoveBufferTimeouts() {    
    if (!moveBuffer.isEmpty()) {
      List<Card> toSubmit = new ArrayList<Card>();
      for (Pair<Card, MoveResultListener> pair : moveBuffer) {
        Card card = pair.first;
        if (card != null) {
          toSubmit.add(card);
        }
      }
      
      submitMove(toSubmit);
    }
  }
  
  private void notifyMoveResultListeners(final GameStateResponse response) {
    if (response.getStatusCode() == StatusCode.OK) {
      for (int i = 0; i < moveBuffer.size(); ++i) {
        Pair<Card, MoveResultListener> pair = moveBuffer.get(i);
        MoveResultListener callback = pair.second;
        if (callback != null) {
          callback.accepted(pair.first);
        }
      }
      
    } else {
      for (int i = moveBuffer.size(); --i >= 0;) {
        Pair<Card, MoveResultListener> pair = moveBuffer.get(i);
        MoveResultListener callback = pair.second;
        if (callback != null) {
          callback.rejected(pair.first);
        }
      }
    }
    
    moveBuffer.clear();
  }
  
  public void submitMove(Card card, MoveResultListener callback) {
    moveBuffer.add(new Pair<Card, MoveResultListener>(card, callback));
    restartMoveBufferTimer();
  }
  
  public void passTurn(MoveResultListener callback) {
    if (!moveBuffer.isEmpty()) {
      for (int i = moveBuffer.size(); --i >= 0;) {
        Pair<Card, MoveResultListener> pair = moveBuffer.get(i);
        MoveResultListener cb = pair.second;
        if (cb != null) {
          cb.rejected(pair.first);
        }
      }
    }
    
    moveBuffer.clear();
    
    submitMove(Card.createSpecialCard(SpecialType.PASS), callback);
  }
  
  public void forfeit() {
    if (!moveBuffer.isEmpty()) {
      for (int i = moveBuffer.size(); --i >= 0;) {
        Pair<Card, MoveResultListener> pair = moveBuffer.get(i);
        MoveResultListener cb = pair.second;
        if (cb != null) {
          cb.rejected(pair.first);
        }
      }
    }
    
    moveBuffer.clear();
    submitMove(Card.createSpecialCard(SpecialType.FORFEIT), null);
    signalMoveBufferTimeouts();
  }
  
  public void signalTimeout() {
    if (!moveBuffer.isEmpty()) {
      signalMoveBufferTimeouts();
      
    } else {
      //forfeit();
    }
  }
  
  private void submitMove(List<Card> move) {
    log.info("SUBMITTED: "+ Arrays.toString(move.toArray()));
    Request request = new MakeMoveRequest(move);
    
    client.sendRequest(request);
  }
  
  // -----------------------------------------------------------------------------------------------------------------
  
  private void displayIfGameOver(final GameStateResponse response) {
    if (response != null && response.getGameState().isGameOver()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      
      String reason = "";
      for (PublicPlayerState p : response.getPlayersState()) {
        if (p.getStatus() == Status.FORFEITED) {
          reason = p.getUsername() + " forfeits!";
          break;
        }
      }
      
      viewGameOver(reason, response.getGameState().getVictor());
    }
  }
  
  public void viewLeaderboard() {
    ViewStatsRequest request = new ViewStatsRequest();
    client.sendRequest(request);
  }
  
  private synchronized void handleViewStatsResponse(ViewStatsResponse response) {
    List<PublicStats> stats = ((ViewStatsResponse)response).getStats();
    
    String[] columnNames = new String[] { "Player", "Wins", "Losses" };
    String[][] data = new String[stats.size()][columnNames.length];
    
    for (int i = 0; i < stats.size(); ++i) {
      data[i][0] = stats.get(i).getUsername();
      data[i][1] = Integer.toString(stats.get(i).getWins());
      data[i][2] = Integer.toString(stats.get(i).getLosses());
    }
    
    LeaderboardPane pane = new LeaderboardPane(this, player1, data, columnNames);
    
    cache.put(LeaderboardPane.class, pane);
    viewStack.push(pane);
    
    container.setContentPane(pane);
    container.pack();
  }
  
  public void viewUserHistory(boolean onlyMyGames) {
    ViewUserHistoryRequest request = new ViewUserHistoryRequest(onlyMyGames, 0);
    client.sendRequest(request);
  }
  
  private List<PublicHistory> userHistory;
  private synchronized void handleViewUserHistoryResponse(ViewHistory response) {
    userHistory = response.getHistory();
    
    String[] columnNames = new String[] { "Date", "Opponents" };
    String[][] data = new String[userHistory.size()][columnNames.length];
    
    for (int i = 0; i < userHistory.size(); ++i) {
      data[i][0] = userHistory.get(i).getDate().toString();
      data[i][1] = userHistory.get(i).getOpponent().toString();
    }
    HistoryPane pane = new HistoryPane(this, player1, data, columnNames);
    
    cache.put(HistoryPane.class, pane);
    viewStack.push(pane);
    
    container.setContentPane(pane);
    container.pack();
  }
  
  public synchronized void viewGameHistory(int row) {
    PublicHistory history = userHistory.get(row);
    
    String moves = history.getMoves();
    String[] movesArray = null;
    if (moves != null) {
      movesArray = moves.split("\\n");
    }
    
    GameHistoryPane pane = new GameHistoryPane(this, player1, history.getDate().toString(), movesArray);
    viewStack.push(pane);
    
    container.setContentPane(pane);
    container.pack();
  }

  private void viewGameOver(String reason, String victor) {
    GameOverPane pane = new GameOverPane(this, reason, player1, player2, victor);
    
    viewStack.clear();
    viewStack.push(cache.get(WelcomePane.class));
    viewStack.push(pane);
    
    ((WelcomePane)cache.get(WelcomePane.class)).setStatusMessage("");
    
    container.setContentPane(pane);
    container.pack();
  }
  
  public void goBack(final Class<?> cls) {
    if (!container.isShowing()) {
      exit();
      return;
    }
    
    viewStack.pop();
    
    if (viewStack.isEmpty()) {
      viewStack.push(cache.get(LoginPane.class));
    }
    
    Container previous = viewStack.peek();
    container.setContentPane(previous);
    container.pack();
  }
  
  
  private void panicAndLogOut(Throwable ex) {
    log.error(ex.getMessage(), ex);
    
    Container pane = cache.get(ServerErrorPane.class);
    if (pane == null) {
      pane = new ServerErrorPane(this);
      cache.put(ServerErrorPane.class, pane);
    }
    
    viewStack.clear();
    viewStack.push(pane);
    
    container.setContentPane(pane);
    container.pack();
    
    logout(Controller.class);
  }

  private Class<?> whoCalledLogout = null;
  public synchronized void logout(Class<?> cls) {
    client.sendRequest(new LogoutRequest());
    whoCalledLogout = cls;
  }
  
  private synchronized void handleLogoutResponse(LogoutResponse response) {
    client.disconnect();
    goBack(whoCalledLogout);
    whoCalledLogout = null;
  }
  
  private void exit() {
    client.disconnect();
    container.dispose();
  }
}
