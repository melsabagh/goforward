/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.game;

import java.util.List;

public class PublicGameState {
  
  private String currentPlayer;
  private List<Card> currentMove;
  
  private String nextPlayer;
  private int timeout;
  
  private String victor;
  private boolean gameOver = false;
  
  public String getCurrentPlayer() {
    return currentPlayer;
  }

  public void setCurrentPlayer(String currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public List<Card> getCurrentMove() {
    return currentMove;
  }

  public void setCurrentMove(List<Card> currentMove) {
    this.currentMove = currentMove;
  }
  
  public String getNextPlayer() {
    return nextPlayer;
  }

  public void setNextPlayer(String nextPlayer) {
    this.nextPlayer = nextPlayer;
  }
  
  public int getTimeout() {
    return timeout;
  }
  
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }
  
  public String getVictor() {
    return victor;
  }

  public void setVictor(String victor) {
    this.victor = victor;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public void setGameOver(boolean gameOver) {
    this.gameOver = gameOver;
  }
}
