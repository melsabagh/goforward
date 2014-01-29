/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

public final class PublicStats {
  private String username;
  private int losses;
  private int wins;

  public PublicStats(String username, int losses, int wins) {
    this.username = username;
    this.losses = losses;
    this.wins = wins;
  }

  public String getUsername() {
    return username;
  }

  public int getLosses() {
    return losses;
  }

  public int getWins() {
    return wins;
  }
}