/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.model;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.ctn.request.Request;

public final class Client {
  Log log = LogFactory.getLog(Client.class);
  
  private ClientImpl impl;
  private Thread clientThread; 
  
  public Client(String host, int port) {
    impl = new ClientImpl(host, port);
  }
  
  public synchronized void connect() throws IOException {
    if (!impl.isConnected()) {
      impl.connect();
      
      clientThread = new Thread(impl, "Client");
      clientThread.setDaemon(true);
      clientThread.start();
    }
  }
  
  public synchronized void addEventListener(ClientEventListener listener) {
    impl.addEventListener(listener);
  }
  
  public synchronized void disconnect() {
    impl.disconnect();
    clientThread = null;
  }
  
  
  public synchronized void sendRequest(Request request) {
    impl.sendRequest(request);
  }
  
  public synchronized boolean isConnected() {
    return impl.isConnected();
  }
}
