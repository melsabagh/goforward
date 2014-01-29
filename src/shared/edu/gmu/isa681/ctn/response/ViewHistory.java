/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;

import java.util.List;

import edu.gmu.isa681.game.PublicHistory;

public final class ViewHistory extends Response {

  private List<PublicHistory> history;
  
  public ViewHistory(StatusCode status, List<PublicHistory> history) {
    super(status);
    this.history = history;
  }
  
  public List<PublicHistory> getHistory() {
    return history;
  }
}
