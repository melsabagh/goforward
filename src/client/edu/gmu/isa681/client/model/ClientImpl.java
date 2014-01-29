/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.client.model;

import java.io.IOException;
import java.util.Vector;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.ctn.Encodeable;
import edu.gmu.isa681.ctn.EncodedChannel;
import edu.gmu.isa681.ctn.UnexpectedTypeException;
import edu.gmu.isa681.ctn.request.Request;
import edu.gmu.isa681.ctn.response.Response;
import edu.gmu.isa681.util.Utils;

final class ClientImpl implements Runnable {
  Log log = LogFactory.getLog(ClientImpl.class);
  
  private String host;
  private int port;
  
  private SSLSocket sslSocket = null;
  private EncodedChannel chnl = null;
  
  private Object shutdownLock = new Object();
  private boolean shuttingDown = false;
  private Vector<ClientEventListener> listeners = new Vector<ClientEventListener>();
  
  public ClientImpl(String host, int port) {
    this.host = host;
    this.port = port;
  }
  
  public void connect() throws IOException {
    if (isConnected()) {
      return;
    }
    
    SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();;
    sslSocket = (SSLSocket) ssf.createSocket(host, port);
    sslSocket.startHandshake();
    
    chnl = new EncodedChannel(sslSocket.getInetAddress().toString(), 
                              sslSocket.getInputStream(), 20, 
                              sslSocket.getOutputStream(), 20);

    shuttingDown = false;
  }
  
  @Override
  public void run() {
    try {
      while (true) {
        synchronized (shutdownLock) {
          if (shuttingDown) break;
        }
        Encodeable packet = chnl.read();
        
        if (packet instanceof Response) {
          notifyResponseListeners((Response)packet);
          
        } else {
          notifyExceptionListeners(new UnexpectedTypeException(packet.toString()));
        }
      }
      
    } catch (InterruptedException ex) {
      synchronized (shutdownLock) {
        if (!shuttingDown) {
          log.warn(ex.getMessage(), ex);
        }
      }
      
    } finally {
      disconnect();
    }
  }
  
  private void notifyResponseListeners(Response response) {
    for (ClientEventListener listener : listeners) {
      listener.responseReceived(response);
    }
  }
  
  private void notifyExceptionListeners(Throwable ex) {
    for (ClientEventListener listener : listeners) {
      listener.exceptionOccured(ex);
    }
  }
  
  public void addEventListener(ClientEventListener listener) {
    listeners.add(listener);
  }
  
  public void disconnect() {
    synchronized (shutdownLock) {
      shuttingDown = true;
    }
    
    Utils.closeQuitely(chnl);
    Utils.closeQuitely(sslSocket);
    sslSocket = null;
    chnl = null;
  }
  
  public boolean isConnected() {
    return sslSocket != null && !sslSocket.isClosed();
  }
  
  public void sendRequest(Request request) {
    chnl.write(request);
  }
}