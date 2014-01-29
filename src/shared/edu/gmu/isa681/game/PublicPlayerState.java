/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

public final class PublicPlayerState {
  public enum Status { IDLE, MOVED, PASSED, FORFEITED }
  
  private String username;
  private int score;
  private int cardsCount;
  private Status status = Status.IDLE;
  
  public PublicPlayerState(String username) {
    this.username = username;
  }
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getCardsCount() {
    return cardsCount;
  }

  public void setCardsCount(int cardsCount) {
    this.cardsCount = cardsCount;
  }
  
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
