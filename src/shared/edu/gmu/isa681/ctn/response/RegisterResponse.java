/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;

import java.util.List;

public final class RegisterResponse extends Response {

  private String username;
  private List<String> details;
  
  public RegisterResponse(StatusCode status, String username, List<String> details) {
    super(status);
    this.username = username;
    this.details = details;
  }
  
  public String getUsername() {
    return username;
  }
  
  public List<String> getDetails() {
    return details;
  }

}
