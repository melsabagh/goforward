/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.util;

import java.io.Closeable;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Utils {
  
  public static void closeQuitely(Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (IOException ignore) {
        
      }
    }
  }
  
  public static String toJsonString(Object obj) {
    Gson gson = new GsonBuilder()
      .serializeNulls()
      .disableInnerClassSerialization()
      .generateNonExecutableJson()
      //.setPrettyPrinting()
      .create();
    
    return gson.toJson(obj);
  }

}
