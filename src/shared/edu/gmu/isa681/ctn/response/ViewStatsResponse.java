/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;

import java.util.List;

import edu.gmu.isa681.game.PublicStats;

public final class ViewStatsResponse extends Response {

  private List<PublicStats> stats;
  
  public ViewStatsResponse(StatusCode status, List<PublicStats> stats) {
    super(status);
    this.stats = stats;
  }
  
  public List<PublicStats> getStats() {
    return stats;
  }
}
