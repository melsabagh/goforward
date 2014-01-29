package edu.gmu.isa681.server.core.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

import edu.gmu.isa681.ctn.response.StatusCode;

public final class DBHelper {
  private static Log log = LogFactory.getLog(DBHelper.class);
  
  private final static String DB_FILENAME = "goforward.db"; 
  
  static {
    try {
      Class.forName("org.sqlite.JDBC");
      createDb(DB_FILENAME);
      
    } catch (ClassNotFoundException | SQLException ex) {
      log.fatal(ex.getMessage(), ex);
    }
  }
  
  private static void createDb(String dbFilename) throws SQLException {
    JdbcPooledConnectionSource conn = createConnection();
    TableUtils.createTableIfNotExists(conn, GameHistory.class);
    TableUtils.createTableIfNotExists(conn, UserAccount.class);
    TableUtils.createTableIfNotExists(conn, UserHistory.class);
    TableUtils.createTableIfNotExists(conn, UserStats.class);
    conn.closeQuietly();
  }
  
  
  public static JdbcPooledConnectionSource createConnection() throws SQLException {
    JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:"+ DB_FILENAME);
    
    connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
    connectionSource.setCheckConnectionsEveryMillis(60 * 1000);
    connectionSource.setTestBeforeGet(true);
    
    return connectionSource;
  }

    
  public static StatusCode createUserAccount(String username, String email, String pwdSaltedHash) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<UserAccount, String> dao = DaoManager.createDao(conn, UserAccount.class);
      
      if (dao.idExists(username)) {
        return StatusCode.USERNAME_EXISTS;
        
      } else if (dao.queryBuilder().where().eq("email", email).countOf() != 0) {
        return StatusCode.EMAIL_EXISTS;
        
      } else {
        UserAccount account = new UserAccount(username, email, pwdSaltedHash);
        dao.create(account);
        
        updateStats(new UserStats(username));
        return StatusCode.OK;
      }
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static List<UserStats> getTopStats(long maxRows) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<UserStats, String> dao = DaoManager.createDao(conn, UserStats.class);
      return dao.queryBuilder()
        .orderBy("score", false)
        .limit(maxRows)
        .query();
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static void updateStats(UserStats newStats) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<UserStats, String> dao = DaoManager.createDao(conn, UserStats.class);
      dao.createOrUpdate(newStats);
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static UserStats getStats(String username) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<UserStats, String> dao = DaoManager.createDao(conn, UserStats.class);
      return dao.queryForId(username);
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static void createUserHistory(UserHistory newHistory) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<UserHistory, Void> dao = DaoManager.createDao(conn, UserHistory.class);
      dao.create(newHistory);
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static List<UserHistory> getUserHistory(String username, int startRow, int maxRows) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<UserHistory, Void> dao = DaoManager.createDao(conn, UserHistory.class);
      return dao.queryBuilder()
        .offset((long)startRow)
        .limit((long)maxRows)
        .where().eq("username", username)
        .query();
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static List<GameHistory> getAudit(String limitToUsername, int startRow, int maxRows) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      
      if (limitToUsername != null) {
        Dao<UserHistory, Void> userHistoryDao = DaoManager.createDao(conn, UserHistory.class);
        QueryBuilder<UserHistory, Void> uhQb = userHistoryDao.queryBuilder();
        uhQb.where().eq("username", limitToUsername);
        uhQb.selectColumns("gameId");
        
        Dao<GameHistory, String> dao = DaoManager.createDao(conn, GameHistory.class);
        return dao.queryBuilder()
          .offset((long)startRow)
          .limit((long)maxRows)
          .orderBy("date", false)
          .where().in("gameId", uhQb)
          .query();
        
      } else {
        
        Dao<GameHistory, String> dao = DaoManager.createDao(conn, GameHistory.class);
        return dao.queryBuilder()
          .offset((long)startRow)
          .limit((long)maxRows)
          .orderBy("date", false)
          .query();
      }
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static List<String> getPlayers(String gameId) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      
      Dao<UserHistory, Void> dao = DaoManager.createDao(conn, UserHistory.class);
      List<UserHistory> history = dao.queryBuilder().where().eq("gameId", gameId).query();
      
      List<String> players = new ArrayList<String>(history.size());
      for (UserHistory h : history) {
        players.add(h.getUsername());
      }
      
      return players;
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static void updateGameHistory(GameHistory newGameHistory) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
       conn = createConnection();
       Dao<GameHistory, String> dao = DaoManager.createDao(conn, GameHistory.class);
       dao.createOrUpdate(newGameHistory);
       
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
  public static void appendToGameHistory(String gameId, String str) throws SQLException {
    GameHistory oldGameHistory = DBHelper.getGameHistory(gameId);
    GameHistory newGameHistory = new GameHistory(gameId, oldGameHistory.getDate(), ""); 
    newGameHistory.setHistory(oldGameHistory.getHistory() + "\n" + str);
    DBHelper.updateGameHistory(newGameHistory);
  }
  
  public static GameHistory getGameHistory(String gameId) throws SQLException {
    JdbcConnectionSource conn = null;
    try {
      conn = createConnection();
      Dao<GameHistory, String> dao = DaoManager.createDao(conn, GameHistory.class);
      return dao.queryForId(gameId);
      
    } finally {
      if (conn != null) {
        conn.closeQuietly();
      }
    }
  }
  
//  public static GameHistory getUserGameHistory(String username, String gameId) throws SQLException {
//    JdbcConnectionSource conn = null;
//    try {
//      conn = createConnection();      
//      
//      Dao<UserHistory, Void> userHistoryDao = DaoManager.createDao(conn, UserHistory.class);
//      QueryBuilder<UserHistory, Void> userHistoryQb = userHistoryDao.queryBuilder();
//      List<UserHistory> found = userHistoryQb.where().eq("username", username).and().eq("gameId", gameId).query();
//      
//      if (found != null) {
//        Dao<GameHistory, String> gameHistoryDao = DaoManager.createDao(conn, GameHistory.class);
//        QueryBuilder<GameHistory, String> gameHistoryQb = gameHistoryDao.queryBuilder();
//        gameHistoryQb.where().eq("gameId", gameId);
//        
//        List<GameHistory> result = gameHistoryQb.query();
//        
//        if (result.size() == 1) {
//          return result.get(0);
//          
//        } else if (result.isEmpty()) {
//          return null;
//          
//        } else {
//          RuntimeException ex = new RuntimeException("gameId not unique! username: "+ username +", gameid: "+ gameId);
//          log.fatal(ex.getMessage(), ex);
//          throw ex;
//        }
//        
//      } else {
//        return null;
//      }
//      
//            
//    } finally {
//      if (conn != null) {
//        conn.closeQuietly();
//      }
//    }
//  }

}
