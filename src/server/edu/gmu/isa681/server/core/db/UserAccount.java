package edu.gmu.isa681.server.core.db;

import com.j256.ormlite.field.DatabaseField;

public final class UserAccount {
  
  @DatabaseField(id=true)
  private String username;
  
  @DatabaseField(canBeNull=false, unique=true)
  private String email;
  
  @DatabaseField(canBeNull=false)
  private String pwdSaltedHash;
  
  public UserAccount() {
    // ORMLite needs a no-arg constructor 
  }
  
  public UserAccount(String username, String email, String pwdSaltedHash) {
    this.username = username;
    this.email = email;
    this.pwdSaltedHash = pwdSaltedHash;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }

  public String getPwdSaltedHash() {
    return pwdSaltedHash;
  }

  public void setPwdSaltedHash(String pwdHash) {
    this.pwdSaltedHash = pwdHash;
  }
  
  public String toString() {
    return username +"|"+ email +"|"+ pwdSaltedHash;
  }
}
