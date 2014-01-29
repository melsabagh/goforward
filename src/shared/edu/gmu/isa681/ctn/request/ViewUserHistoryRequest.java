/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.request;

public final class ViewUserHistoryRequest extends Request {

  private boolean onlyMyGames;
  private int offset;
  
  public ViewUserHistoryRequest(boolean onlyMyGames, int offset) {
    this.onlyMyGames = onlyMyGames;
    this.offset = offset;
  }
  
  public boolean isOnlyMyGames() {
    return onlyMyGames;
  }
  
  public int getOffset() {
    return offset;
  }
  
}
