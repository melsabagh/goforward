/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

import java.util.Date;
import java.util.List;

public final class PublicHistory {

  private Date date; // mutable
  private List<String> opponents;
  private String moves;
  
  public PublicHistory(Date date, List<String> opponents, String moves) {
    this.date = date != null ? new Date(date.getTime()) : null;
    this.opponents = opponents;
    this.moves = moves;
  }

  public Date getDate() {
    return date != null ? new Date(date.getTime()) : null;
  }

  public List<String> getOpponent() {
    return opponents;
  }
  
  public String getMoves() {
    return moves;
  }
}
