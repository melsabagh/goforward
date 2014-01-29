/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server.core;

import java.util.HashSet;
import java.util.Set;

enum GlobalUserStatus {
  INSTANCE;
  
  private final Set<String> loggedInUsers = new HashSet<String>();
  
  public synchronized boolean isLoggedIn(String username) {
    return loggedInUsers.contains(username);
  }
  
  public synchronized void setLoggedIn(String username) {
    loggedInUsers.add(username);
  }
  
  public synchronized void setLoggedOut(String username) {
    loggedInUsers.remove(username);
  }
}
