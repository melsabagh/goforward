/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.model;

import edu.gmu.isa681.ctn.response.Response;

public interface ClientEventListener {
  public void responseReceived(Response response);
  public void exceptionOccured(Throwable ex);
}