/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.util.Constants;

public final class Main {
  private static Log log = LogFactory.getLog(Main.class);
  
  public static void main(String[] args) {
    log.info("Starting server...");
    final Server server = new Server(Constants.SERVER_PORT);
    
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        server.shutdown();
      }
    });
    
    Thread thread = new Thread(server, "ServerThread");
    thread.start();
    
    try {
      thread.join();
      
    } catch (InterruptedException ex) {
      log.error(ex);
    }
    
    //TODO: implement some CLI to admin the server
  }
}
