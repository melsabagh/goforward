/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;


import edu.gmu.isa681.ctn.Encodeable;
import edu.gmu.isa681.util.Utils;


public abstract class Response implements Encodeable {
  
  private final String type;
  private final StatusCode status;

  public Response(StatusCode status) {
    this.type = getClass().getName();
    this.status = status;
  }
  
  public final String getType() {
    return type;
  }
  
  public final StatusCode getStatusCode() {
    return status;
  }
  
  @Override
  public String toString() {
    return Utils.toJsonString(this);
  }
}