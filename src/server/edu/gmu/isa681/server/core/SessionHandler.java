/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.ctn.ChannelExceptionListener;
import edu.gmu.isa681.ctn.Encodeable;
import edu.gmu.isa681.ctn.EncodedChannel;
import edu.gmu.isa681.ctn.UnexpectedTypeException;
import edu.gmu.isa681.ctn.request.CaptchaResponse;
import edu.gmu.isa681.ctn.request.JoinGameRequest;
import edu.gmu.isa681.ctn.request.LoginRequest;
import edu.gmu.isa681.ctn.request.LogoutRequest;
import edu.gmu.isa681.ctn.request.MakeMoveRequest;
import edu.gmu.isa681.ctn.request.RegisterRequest;
import edu.gmu.isa681.ctn.request.ViewUserHistoryRequest;
import edu.gmu.isa681.ctn.request.ViewStatsRequest;
import edu.gmu.isa681.ctn.response.CaptchaChallenge;
import edu.gmu.isa681.ctn.response.EmptyResponse;
import edu.gmu.isa681.ctn.response.GameStateResponse;
import edu.gmu.isa681.ctn.response.LoginResponse;
import edu.gmu.isa681.ctn.response.LogoutResponse;
import edu.gmu.isa681.ctn.response.RegisterResponse;
import edu.gmu.isa681.ctn.response.StatusCode;
import edu.gmu.isa681.ctn.response.ViewHistory;
import edu.gmu.isa681.ctn.response.ViewStatsResponse;
import edu.gmu.isa681.game.GoForward;
import edu.gmu.isa681.game.PublicStats;
import edu.gmu.isa681.game.PublicHistory;
import edu.gmu.isa681.server.core.db.DBHelper;
import edu.gmu.isa681.server.core.db.GameHistory;
import edu.gmu.isa681.server.core.db.UserStats;
import edu.gmu.isa681.util.Utils;

public final class SessionHandler implements Runnable {
  private Log log = LogFactory.getLog(SessionHandler.class);
  
  private final SSLSocket sslSocket;
  private EncodedChannel chnl;
  
  
  public SessionHandler(SSLSocket socket) {
    this.sslSocket = socket;
  }
  
  // session variables
  // -----------------------------------------------------------------------------------------------------------------

  private final String sessionId = UUID.randomUUID().toString();
  
  private String _username;
  
  public String getUsername() {
    return _username;
  }
  
  private void setUsername(String username) {
    if (this._username == null) {
      this._username = username;
    } else {
      throw new RuntimeException("BUG: username already set.");  
    }
  }
  
  // States for the session state machine
  private enum State { NOT_LOGGED_IN, IDLE, WAITING, INGAME };
  private State currentState = State.NOT_LOGGED_IN;
    
  // game instance
  private GoForward game = null;
  
  // -----------------------------------------------------------------------------------------------------------------

  
  // global handlers
  // -----------------------------------------------------------------------------------------------------------------
  
  private volatile boolean _shuttingDown = false;
  
  private synchronized boolean isShuttingDown() {
    return _shuttingDown;
  }
  
  private synchronized void shutdown(boolean isCrashed) {
    if (_shuttingDown) return;
    
    _shuttingDown = true;
    
    if (getUsername() != null) {
      log.info("CRASHED? "+ isCrashed + ", user: "+ getUsername());
      
      if (!isCrashed) {
        forceForfeit();
      }
      
      LobbyManager.INSTANCE.leave(getUsername(), isCrashed);
      GlobalUserStatus.INSTANCE.setLoggedOut(getUsername());
      currentState = State.NOT_LOGGED_IN;
    }
    
    log.info("Closing request handler...");
    Utils.closeQuitely(chnl);
    Utils.closeQuitely(sslSocket);
    log.info("Request handler closed.");
  }
  
  private void handleException(Throwable t) {
    try {
      throw t;
      
    } catch (SQLException ex) {
      log.error(ex.getMessage(), ex);
      shutdown(false);
      
    } catch (UnexpectedTypeException ex) {
      log.warn(ex.getMessage(), ex);
      
    } catch (InterruptedException | IOException ex) {
      if (!isShuttingDown()) {
        log.error(ex.getMessage(), ex);
        shutdown(true);
      }
      
    } catch (Throwable ex) {
      log.fatal("Unhandled exception:");
      log.fatal(ex.getMessage(), ex);
      shutdown(true); 
    }
  }
  
  private void handleRequest(Encodeable request) throws SQLException, UnexpectedTypeException {
    if (request instanceof RegisterRequest) {
      handleRegister((RegisterRequest)request);
      
    } else if (request instanceof LoginRequest) {
      handleLogin((LoginRequest)request);
      
    } else if (request instanceof JoinGameRequest) {
      handleJoinGame((JoinGameRequest)request);
      
    } else if (request instanceof MakeMoveRequest) {
      handleMakeMove((MakeMoveRequest)request);
      
    } else if (request instanceof ViewStatsRequest) {
      handleViewStats((ViewStatsRequest)request);
      
    } else if (request instanceof ViewUserHistoryRequest) {
      handleViewUserHistory((ViewUserHistoryRequest)request);
      
    } else if (request instanceof LogoutRequest) {
      handleLogout((LogoutRequest)request);
      
    } else if (request == null) { // timeout
      chnl.write(new EmptyResponse(StatusCode.CONNECTION_TIMEOUT));
      shutdown(false);
      
    } else {
      // invalid request.
      chnl.write(new EmptyResponse(StatusCode.INVALID_REQUEST));
      throw new UnexpectedTypeException(request.toString());
    }
  }
  
  // -----------------------------------------------------------------------------------------------------------------
  
  // Session handler loop
  // -----------------------------------------------------------------------------------------------------------------
  
  public final static int CONNECTION_TIMEOUT1 = 1; // 1 minute.
  public final static int CONNECTION_TIMEOUT2 = 5; // 5 minutes. 
  
  public final static int DEFAULT_INBOUND_CAPACITY = 20;
  public final static int DEFAULT_OUTBOUND_CAPACITY = 20;
  
  @Override
  public void run() {
    try {
      log.info("Handling request from "+ sslSocket.getInetAddress().toString());
      
      currentState = State.NOT_LOGGED_IN;
      
      // setup the encoder
      chnl = new EncodedChannel(sslSocket.getInetAddress().toString(), 
                                sslSocket.getInputStream(), DEFAULT_INBOUND_CAPACITY, 
                                sslSocket.getOutputStream(), DEFAULT_OUTBOUND_CAPACITY);

      // setup the exception handler
      chnl.addExceptionListener(new ChannelExceptionListener() {
        @Override
        public void exceptionOccurred(Throwable ex) {
          handleException(ex);
        }
      });
      
      int timeout = CONNECTION_TIMEOUT1; // should login within 1 minute of opening the connection
      // read and handle requests (with timeout)
      while (!isShuttingDown()) {
        Encodeable request = chnl.read(timeout, TimeUnit.MINUTES);
        timeout = CONNECTION_TIMEOUT2; // timeout for requests after successful login
        
        try {
          handleRequest(request);
        } catch (UnexpectedTypeException ex) {
          handleException(ex);
        }
      }
      
    } catch (Exception ex) {
      handleException(ex);
    }
  }
  
  //-----------------------------------------------------------------------------------------------------------------
  
  // helpers
  //-----------------------------------------------------------------------------------------------------------------
  
  public final static int MAX_CAPTCHA_ATTEMPTS = 3;
  
  private State savedState = null;
  private void challengeAndValidate() {
    boolean correct = false;
    try {
      if (savedState == null) {
        savedState = currentState;
        currentState = State.NOT_LOGGED_IN;
      }
      
      for (int i = 0; i < MAX_CAPTCHA_ATTEMPTS; ++i) {
        BufferedImage image = CaptchaService.INSTANCE.getImageChallengeForId(sessionId);
        CaptchaChallenge challenge = new CaptchaChallenge(StatusCode.OK, image);
        chnl.write(challenge);
    
        Encodeable response = chnl.read(30, TimeUnit.SECONDS);
        if (response instanceof CaptchaResponse) {
          correct = CaptchaService.INSTANCE.validateResponseForId(sessionId, ((CaptchaResponse)response).getSolution());
          if (correct) {
            currentState = savedState;
            savedState = null;
            break;
          }
        } else {
          break;
        }
      }
      
    } catch (InterruptedException | IOException ex) {
      log.error(ex.getMessage(), ex);
      shutdown(false);
    }
    
    if (!correct) {
      shutdown(false);
    }
  }
  
  //-----------------------------------------------------------------------------------------------------------------
  
  // requests handlers
  //-----------------------------------------------------------------------------------------------------------------

  private void handleRegister(RegisterRequest request) throws SQLException {
    if (currentState != State.NOT_LOGGED_IN) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }
    
    try {
      String username = request.getUsername();
      String email = request.getEmail();
      String password = request.getPassword();
      
      // validate input, and give details
      
      List<String> errors = new ArrayList<String>();
      
      errors.addAll(Validator.validateUsername(username));
      errors.addAll(Validator.validateEmail(email));
      errors.addAll(Validator.validatePassword(username, password));
      
      final StatusCode status; 
      if (errors.isEmpty()) { // input is valid
        challengeAndValidate(); // captcha challenge, if incorrect would diverge to shutdown
        
        String pwdSaltedHash = Authenticator.createSaltedHash(request.getPassword());
        status = DBHelper.createUserAccount(request.getUsername(), request.getEmail(), pwdSaltedHash);
        
      } else {
        status = StatusCode.REGISTRATION_ERROR;
      }
      
      chnl.write(new RegisterResponse(status, request.getUsername(), errors));
      
      //if (status != StatusCode.OK) {
      shutdown(false);
      //}
      
    } catch (SQLException ex) {
      chnl.write(new EmptyResponse(StatusCode.SERVER_ERROR));
      throw ex;
    }
  }
  
  private void handleLogin(LoginRequest request) throws SQLException {
    if (currentState != State.NOT_LOGGED_IN) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }
    
    try {
      StatusCode status = null;
      
      String username = request.getUsername();
      String password = request.getPassword();
      
      // validate input, but don't give details
      
      if (!Validator.isPrintableAscii(username)) {
        status = StatusCode.INVALID_CREDENTIALS;
        
      } else if (!Validator.isPrintableAscii(password)) {
        status = StatusCode.INVALID_CREDENTIALS;
        
      } else if (Validator.isEmpty(password)) {
        status = StatusCode.PASSWORD_REQUIRED;
        
      } else if (Validator.isEmpty(username)) {
        status = StatusCode.USERNAME_REQUIRED;
        
      } else { // input is valid
        // multiple logins are not allowed
        if (GlobalUserStatus.INSTANCE.isLoggedIn(request.getUsername())) { 
          status = StatusCode.INVALID_CREDENTIALS;
          
        } else {
        
          if (Authenticator.checkCredentials(request.getUsername(), request.getPassword())) { 
            status = StatusCode.OK;
            setUsername(request.getUsername());
            GlobalUserStatus.INSTANCE.setLoggedIn(request.getUsername());
            currentState = State.IDLE;
            
          } else {
            status = StatusCode.INVALID_CREDENTIALS;
          }
        }
      }
      
      chnl.write(new LoginResponse(status, request.getUsername()));
      
      if (status != StatusCode.OK) {
        shutdown(false);
      }
      
    } catch (SQLException ex) {
      chnl.write(new EmptyResponse(StatusCode.SERVER_ERROR));
      throw ex;
    }
  }
  
  private void handleLogout(LogoutRequest request) {
    GlobalUserStatus.INSTANCE.setLoggedOut(getUsername());
    currentState = State.NOT_LOGGED_IN;
    
    chnl.write(new LogoutResponse(StatusCode.OK));
    shutdown(false);
  }
  
  private void handleViewStats(ViewStatsRequest request) throws SQLException {
    if (currentState == State.NOT_LOGGED_IN) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }
    
    try {
      List<UserStats> topStats = DBHelper.getTopStats(50);
      UserStats myStats = DBHelper.getStats(getUsername());
      
      boolean added = false;
      ArrayList<PublicStats> stats = new ArrayList<PublicStats>();
      
      if (topStats != null) {
        for (UserStats s : topStats) {
          stats.add(new PublicStats(s.getUsername(), s.getLosses(), s.getWins()));
          if (getUsername().equals(s.getUsername())) {
            added = true;
          }
        }
        
        if (!added && myStats != null) {
          topStats.add(myStats);
        }
      }
      
      chnl.write(new ViewStatsResponse(StatusCode.OK, stats));
      
    } catch (SQLException ex) {
      chnl.write(new EmptyResponse(StatusCode.SERVER_ERROR));
      throw ex;
    }
  }
  
  private void handleViewUserHistory(ViewUserHistoryRequest request) throws SQLException {
    if (currentState == State.NOT_LOGGED_IN) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }
    
    try {
      List<PublicHistory> toSend = new ArrayList<PublicHistory>();
      List<GameHistory> history;
      
      if (request.isOnlyMyGames()) {
        history = DBHelper.getAudit(getUsername(), request.getOffset(), 200);
        
      } else {
        history = DBHelper.getAudit(null, request.getOffset(), 200);
      }
      
      if (history != null) {
        for (GameHistory h : history) {
          if (!LobbyManager.INSTANCE.isGameRunning(h.getGameId())) {
            toSend.add(new PublicHistory(h.getDate(), DBHelper.getPlayers(h.getGameId()), h.getHistory()));
          }
        }
      }
      chnl.write(new ViewHistory(StatusCode.OK, toSend));
      
    } catch (SQLException ex) {
      chnl.write(new EmptyResponse(StatusCode.SERVER_ERROR));
      throw ex;
    }
  }
  
  private void handleJoinGame(JoinGameRequest request) {
    if (currentState != State.IDLE) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }

    currentState = State.WAITING;
    LobbyManager.INSTANCE.join(getUsername(), new LobbyManager.GameReadyListener() {
      @Override
      public void gameReady(GoForward game) {
        SessionHandler.this.gameReady(game);
      }
    });
  }
  
  private void handleMakeMove(MakeMoveRequest request) {
    if (currentState != State.INGAME) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }
    
    boolean accepted = game.offerMove(getUsername(), request.getMove());
    if (!accepted) {
      chnl.write(new GameStateResponse(StatusCode.INVALID_MOVE, null, null, null));
    }
  }
  
  //-----------------------------------------------------------------------------------------------------------------
  
  // game-related methods
  //-----------------------------------------------------------------------------------------------------------------
  
  private void gameReady(final GoForward game) {
    if (currentState != State.WAITING) {
      //chnl.write(new EmptyResponse(StatusCode.INVALID_STATE));
      //shutdown(false);
      log.info("Invalid sate. Current: "+ currentState);
      return;
    }
    
    log.info("GAME READY" + game.getPlayers().toString());
    
    this.game = game;
    currentState = State.INGAME;
    
    game.addListener(new GoForward.GameStateListener() {
      @Override
      public void stateChanged(boolean justStarted) {
        signalGameStateChanged(justStarted);
      }
    });
  }
  
  private void signalGameStateChanged(boolean justStarted) {
    GameStateResponse state = new GameStateResponse(StatusCode.OK, 
        game.getPublicGameState(), 
        game.getPublicPlayersStates(getUsername()), 
        game.getPrivatePlayerState(getUsername()));
    
    chnl.write(state);
    
    if (game.getPublicGameState().isGameOver()) {
      game = null;
      currentState = State.IDLE;
    }
  }
  
  private void forceForfeit() {
    if (game == null) return;
    game.forceForfeit(getUsername());
  }
}
