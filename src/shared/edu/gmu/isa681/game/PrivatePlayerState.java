/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

import java.util.ArrayList;

public final class PrivatePlayerState {
  
  private String username; 
  private ArrayList<Card> hand = new ArrayList<Card>();
  
  public PrivatePlayerState(String username) {
    this.username = username;
  }
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }

  public ArrayList<Card> getHand() {
    return hand;
  }
}
