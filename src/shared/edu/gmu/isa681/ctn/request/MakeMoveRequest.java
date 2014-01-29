/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.request;

import java.util.List;

import edu.gmu.isa681.game.Card;

public final class MakeMoveRequest extends Request {

  List<Card> move;
  
  public MakeMoveRequest(List<Card> move) {
    this.move = move;
  }
  
  public List<Card> getMove() {
    return move;
  }

}
