/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;

public final class LoginResponse extends Response {

  private String username;
  
  public LoginResponse(StatusCode status, String username) {
    super(status);
    this.username = username;
  }
  
  public String getUsername() {
    return username;
  }
}
