/*
** Copyright 2013 Mohamed Elsabagh <melsabag@gmu.edu>
**
** This file is part of GoForward. See LICENSE for more details.
*/

package edu.gmu.isa681.server.core;

import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

import edu.gmu.isa681.server.core.db.DBHelper;
import edu.gmu.isa681.server.core.db.UserAccount;

final class Authenticator {

  public static boolean checkCredentials(String username, String password) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = DBHelper.createConnection();
      Dao<UserAccount, String> dao = DaoManager.createDao(conn, UserAccount.class);
      
      UserAccount account = dao.queryForId(username);
      if (account != null) {
        /*
         * Uses JBCrypt, by the original authors of BCrypt.
         */
        return BCrypt.checkpw(password, account.getPwdSaltedHash());
      }
      
      return false;
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static String createSaltedHash(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
