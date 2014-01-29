/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.ctn;

public final class UnexpectedTypeException extends Exception {
  private static final long serialVersionUID = -2091837620422260766L;

  public UnexpectedTypeException() {
    super();
  }

  public UnexpectedTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public UnexpectedTypeException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnexpectedTypeException(String message) {
    super(message);
  }

  public UnexpectedTypeException(Throwable cause) {
    super(cause);
  }
}