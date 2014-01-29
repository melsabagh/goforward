/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.request;


public final class RegisterRequest extends Request {

  private String username;
  private String email;
  private String password;
  
  public RegisterRequest(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
  
  public String getUsername() {
    return username;
  }
  
  public String getPassword() {
    return password;
  }
  
  public String getEmail() {
    return email;
  }

}
