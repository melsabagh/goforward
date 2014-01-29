/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.request;


public final class LoginRequest extends Request {

  private String username;
  private String password;
  
  public LoginRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }
  
  public String getUsername() {
    return username;
  }
  
  public String getPassword() {
    return password;
  }
  
  @Override
  public String toString() {
    LoginRequest request = new LoginRequest(username, "-");
    return request.toString();
  }
}
