/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.controller;

import edu.gmu.isa681.ctn.response.GameStateResponse;

public interface GameStateListener {
  public void stateChanged(GameStateResponse response);
}