/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.response;

public enum StatusCode {
  OK ("OK"),
  
  INVALID_CREDENTIALS ("Invalid username or password."),
  
  USERNAME_EXISTS     ("Username already exists."),
  EMAIL_EXISTS        ("Email address already exists."),
  
  USERNAME_REQUIRED   ("Username required."),
  PASSWORD_REQUIRED   ("Password required."),
  
  REGISTRATION_ERROR  ("Registration error."),
  
  CONNECTION_TIMEOUT  ("Connection timeout."),
  INVALID_REQUEST     ("Invalid request."),  
  
  SERVER_ERROR        ("Server error."),
  INVALID_STATE       ("Invalid request."),
  
  INVALID_MOVE        ("Invalid move.")
  ;
  
  
  private final String description;
  private StatusCode(String description) {
    this.description = description;
  }
  
  @Override
  public String toString() {
    return description;
  }
}