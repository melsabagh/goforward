/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.request;

import edu.gmu.isa681.ctn.Encodeable;
import edu.gmu.isa681.util.Utils;

public abstract class Request implements Encodeable {
  
  private final String type;
  
  public Request() {
    this.type = getClass().getName();
  }
  
  public final String getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return Utils.toJsonString(this);
  }
}
