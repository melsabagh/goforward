package edu.gmu.isa681.server.core.db;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public final class GameHistory {

  @DatabaseField(id=true)
  private String gameId;
  
  @DatabaseField(dataType=DataType.DATE_LONG, canBeNull=false)
  private Date date; // mutable.. be careful
  
  @DatabaseField
  private String history; // TOOD: use proper types
  
  public GameHistory() {
    // ORMLite needs a no-arg constructor
  }
  
  public GameHistory(String gameId, Date date, String history) {
    setGameId(gameId);
    setDate(date);
    setHistory(history);
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

  public String getHistory() {
    return history;
  }

  public void setHistory(String line) {
    this.history = line;
  }
}
