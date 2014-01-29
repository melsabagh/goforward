/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;


import java.util.List;

import edu.gmu.isa681.game.PrivatePlayerState;
import edu.gmu.isa681.game.PublicGameState;
import edu.gmu.isa681.game.PublicPlayerState;

public final class GameStateResponse extends Response {

  private PublicGameState gameState; 
  private List<PublicPlayerState> playersState;
  private PrivatePlayerState myState; 
  
  public GameStateResponse(StatusCode status, PublicGameState gameState, List<PublicPlayerState> playersState, PrivatePlayerState myState) {
    super(status);
    
    this.gameState = gameState;
    this.playersState = playersState;
    this.myState = myState;
    
  }

  public PublicGameState getGameState() {
    return gameState;
  }
  
  public List<PublicPlayerState> getPlayersState() {
    return playersState;
  }
  
  public PrivatePlayerState getMyState() {
    return myState;
  }
}
