/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

import edu.gmu.isa681.game.PrivatePlayerState;
import edu.gmu.isa681.game.PublicPlayerState;

final class Player {
  
  private String username;
  private PublicPlayerState publicPlayerState;
  private PrivatePlayerState privatePlayerState;
  
  public Player(String username) {
    if (username == null) throw new IllegalArgumentException("username cannot be null");
    this.username = username;
    this.publicPlayerState = new PublicPlayerState(username);
    this.privatePlayerState = new PrivatePlayerState(username);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    
    Player other = (Player) obj;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }

  public String getUsername() {
    return username;
  }

  public PublicPlayerState getPublicPlayerState() {
    return publicPlayerState;
  }

  public PrivatePlayerState getPrivatePlayerState() {
    return privatePlayerState;
  }
}
