/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import edu.gmu.isa681.util.Utils;

public final class EncodedChannel implements Closeable {
  private Log log = LogFactory.getLog(EncodedChannel.class);

  private final static Gson gson; // gson is thread safe
  static {
    gson = new GsonBuilder()
      .serializeNulls()
      .generateNonExecutableJson()
      .create();
  }
  
  private String name;
  private Writer sout;
  private Scanner sin;
  
  private Vector<ChannelExceptionListener> listeners = new Vector<ChannelExceptionListener>();
  
  private void notifyExceptionListeners(Throwable ex) {
    for (ChannelExceptionListener listener : listeners) {
      listener.exceptionOccurred(ex);
    }
  }
  
  public void addExceptionListener(ChannelExceptionListener listener) {
    if (listener == null) throw new IllegalArgumentException();
    listeners.add(listener);
  }
  
  public EncodedChannel(String name, InputStream in, int inboundCapacity, OutputStream out, int outboundCapacity) throws UnsupportedEncodingException {
    this.name = name;
    this.sin = new Scanner(new BufferedReader(new InputStreamReader(in, "UTF-8")));
    this.sout = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
    
    startHandlers(inboundCapacity, outboundCapacity);
  }
  
  
  /**
   * Our packet structure
   */
  private class JsonAdaptor {
    private final String type;
    private final String body;
    
    public JsonAdaptor(final Encodeable obj) {
      this.type = obj.getType();
      this.body = gson.toJson(obj);
    }
    
    public String getType() {
      return type;
    }
    
    public String getBody() {
      return body;
    }
  }
  
  private String encode(final Encodeable obj) {
    return gson.toJson(new JsonAdaptor(obj));
  }
  
  /**
   * Decodes an encoded packet if and only if: 1) it is marked by Encodeable, and 2) has the same type as 
   * the expected type.
   * 
   * @param encoded
   * @param expectedType
   * @return
   * @throws UnexpectedTypeException if either of the two conditions is violated.
   */
  private Encodeable decode(final String encoded, Class<? extends Encodeable> expectedType) throws UnexpectedTypeException {
    try {
      JsonAdaptor adaptor = gson.fromJson(encoded, JsonAdaptor.class);
      
      // do NOT change the "false" to true. We don't want any code to run until the type is validated
      Class<?> actual = Class.forName(adaptor.getType(), false, getClass().getClassLoader());
      
      // check if type is marked by Encodeable
      if (expectedType.isAssignableFrom(actual)) {
        Object body = gson.fromJson(adaptor.getBody(), actual);
        return expectedType.cast(body);
        
      } else {
        throw new UnexpectedTypeException(adaptor.getType());
      }
      
    } catch (JsonSyntaxException | ClassNotFoundException ex) {
      throw new UnexpectedTypeException(ex);
    }
  }
  
  private final static String DELIMITER = "\r\n\r\n";
  private void send(Encodeable obj) throws IOException {
    String encoded = encode(obj);
    sout.write(encoded);
    sout.write(DELIMITER);
    sout.flush();
  }
  
  private Encodeable recv(Class<? extends Encodeable> type) throws IOException, UnexpectedTypeException {
    try {
      sin.useDelimiter(DELIMITER);
      String encoded = sin.next();
      return decode(encoded, type);
      
    } catch (NoSuchElementException ex) {
      throw new IOException(ex.getCause());
    }
  }
  
  
  private LinkedBlockingQueue<Encodeable> inboundQueue;
  private LinkedBlockingQueue<Encodeable> outboundQueue;
  
  private volatile boolean closed;
  private Thread inboundThread = null;
  private Thread outboundThread = null;
  
  private void startHandlers(int inboundCapacity, int outboundCapacity) {
    inboundQueue = new LinkedBlockingQueue<Encodeable>(inboundCapacity);
    outboundQueue = new LinkedBlockingQueue<Encodeable>(outboundCapacity);
        
    inboundThread = new Thread(new InboundHandler(), "InboundThread[" + name + "]");
    outboundThread = new Thread(new OutboundHandler(), "OutboundThread[" + name + "]");
    
    inboundThread.setDaemon(true);
    outboundThread.setDaemon(true);
    
    inboundThread.start();
    outboundThread.start();
  }
  
  private class InboundHandler implements Runnable {
    @Override
    public void run() {
      while (!closed) {
        try {
          Encodeable packet = recv(Encodeable.class);
          if (packet != null && !inboundQueue.offer(packet)) {
            notifyExceptionListeners(new IOException("Inbound queue is full"));
          }
          
        } catch (IOException ex) {
          if (!closed) {
            notifyExceptionListeners(ex);
            close();
          }
          
        } catch (UnexpectedTypeException ex) {
          notifyExceptionListeners(ex);
          //close();
        }
      }
    }
  }
  
  private class OutboundHandler implements Runnable {
    @Override
    public void run() {
      while (!closed) {
        Encodeable packet = null;
        try {
          packet = outboundQueue.take(); 
          send(packet);
          
          //notifyActionListeners(new ChannelEvent(ChannelEvent.EventType.SENT, packet, null));
          
        } catch (InterruptedException ignore) {
          
        } catch (IOException ex) {
          if (!closed) {
            notifyExceptionListeners(ex);
            close();
          }
        }
      }
    }
  }
  
  
  public void write(Encodeable e) {
    if (!outboundQueue.offer(e)) {
      log.fatal("Outbound queue is full");
      close();
    }
  }
  
  public Encodeable read() throws InterruptedException {
    Encodeable e = inboundQueue.take();
    //log.debug(e); // careful.. will log sensitive info
    return e;
  }
  
  public Encodeable read(long timeout, TimeUnit unit) throws InterruptedException {
    Encodeable e = inboundQueue.poll(timeout, unit);
    //log.debug(e); // careful.. will log sensitive info
    return e;
  }
  
  
  public void close() {
    closed = true;
    
    outboundThread.interrupt();
    try {
      outboundThread.join();
    } catch (InterruptedException ignore) {
    }
    
    // send any pending packets before closing
    try {
      Encodeable packet = null;
      while ( (packet = outboundQueue.poll()) != null) {
        send(packet);
      }
    } catch (IOException ignore) { }
    
    Utils.closeQuitely(sout);
    Utils.closeQuitely(sin);
  }
  
}
