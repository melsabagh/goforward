/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.gmu.isa681.server.core.SessionHandler;
import edu.gmu.isa681.util.Utils;

final class Server implements Runnable {
  private Log log = LogFactory.getLog(Server.class);
  
  private int port;
  private SSLServerSocket sslServerSocket = null;
  
  private Object shutdownLock = new Object();
  private boolean shuttingDown = false;
  
  /**
   * Constructs and initializes a server instance. 
   * Note: the server will not run until the {@link #run()} method is called.
   * @param port Port number where the server should listen on for incoming connections.
   */
  public Server(int port) {
    this.port = port;
  }
  
  /**
   * Establishes the SSL server socket and listens for incoming connections. A stand-alone <code>SessionHandler</code> 
   * is spawned for each connection.
   */
  public void run() {
    try {
      SSLServerSocketFactory ssf = (SSLServerSocketFactory) getSSLServerSocketFactory();
      
      try {
        sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
        log.info("Server started.");
        
        while (true) {
          synchronized (shutdownLock) {
            if (shuttingDown) break;
          }
          
          try {
            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            handleRequest(sslSocket);
            
          } catch (IOException ex) {
            log.error("Exception while handling request.", ex);
          }
        }
      } catch (IOException ex) {
        log.error(ex.getMessage(), ex);
      }
      
    } catch (GeneralSecurityException | IOException ex) {
      log.fatal(ex.getMessage(), ex);
    }
    
    shutdown();
  }
  
  /**
   * Creates a TLS server socket factory using the key store and key store password provided to the JVM at runtime.
   * @return
   * @throws GeneralSecurityException If an error occurs while creating the TLS factory.
   * @throws IOException If an error occurs while reading the key store.
   * 
   * Adapted from Oracle JSSE docs.
   */
  private static SSLServerSocketFactory getSSLServerSocketFactory() throws GeneralSecurityException, IOException {
    FileInputStream fis = null;
    try {
      SSLServerSocketFactory ssf = null;
      // set up key manager to do server authentication
      SSLContext ctx = SSLContext.getInstance("TLS");
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      KeyStore ks = KeyStore.getInstance("JKS");
      
      String keyStore = System.getProperty("javax.net.ssl.keyStore");
      String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
  
      fis = new FileInputStream(keyStore); 
      ks.load(fis, keyStorePassword.toCharArray());
      
      kmf.init(ks, keyStorePassword.toCharArray());
      ctx.init(kmf.getKeyManagers(), null, null);
  
      ssf = ctx.getServerSocketFactory();
      return ssf;
      
    } finally {
      Utils.closeQuitely(fis);
    }
  }
  
  
  /**
   * Shuts the server down and closes the server SSL socket. Calling <code>shutdown()</code> more than 
   * once has no effect.
   */
  public void shutdown() {
    synchronized (shutdownLock) {
      if (shuttingDown) return;
      
      log.info("Shutting down...");
      shuttingDown = true;
      Utils.closeQuitely(sslServerSocket);
    }
  }
  
  /**
   * Spawns a <code>SessionHandler</code> for the given SSL connection.
   * @param sslSocket
   */
  private void handleRequest(SSLSocket sslSocket) {
    SessionHandler handler = new SessionHandler(sslSocket);
    Thread thread = new Thread(handler, "RequestHandler["+ sslSocket.getInetAddress().toString() +"]");
    thread.setDaemon(true);
    thread.start();
  }
}
