package edu.gmu.isa681.server.core.db;

import com.j256.ormlite.field.DatabaseField;

public final class UserStats {

  @DatabaseField(id=true)
  private String username;
  
  @DatabaseField
  private int wins;
  
  @DatabaseField
  private int losses;
  
  @DatabaseField
  private float score;
  
  public UserStats() {
    // ORMLite needs a no-arg constructor 
  }
  
  public UserStats(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getWins() {
    return wins;
  }

  public void setWins(int wins) {
    this.wins = wins;
  }

  public int getLosses() {
    return losses;
  }

  public void setLosses(int losses) {
    this.losses = losses;
  }
  
  public float getScore() {
    return score;
  }
  
  public void setScore(float score) {
    this.score = score;
  }
}
