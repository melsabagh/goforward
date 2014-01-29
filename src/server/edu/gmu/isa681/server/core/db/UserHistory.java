package edu.gmu.isa681.server.core.db;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public final class UserHistory {

  @DatabaseField(index=true, canBeNull=false)
  private String username;
  
  @DatabaseField(canBeNull=false)
  private String gameId;
  
  @DatabaseField(dataType=DataType.DATE_LONG, canBeNull=false)
  private Date date;
  
  public UserHistory() {
    // ORMLite needs a no-arg constructor
  }
  
  public UserHistory(String username, String gameId, Date date) {
    setUsername(username);
    setGameId(gameId);
    setDate(date);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }
  
  public Date getDate() {
    return date != null ? new Date(date.getTime()) : null;
  }
  
  public void setDate(Date date) {
    this.date = date != null ? new Date(date.getTime()) : null;
  }
}
