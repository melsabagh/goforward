/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn.request;

public final class CaptchaResponse extends Request {

  private String solution;
  
  public CaptchaResponse(String solution) {
    this.solution = solution;
  }
  
  public String getSolution() {
    return solution;
  }

}
