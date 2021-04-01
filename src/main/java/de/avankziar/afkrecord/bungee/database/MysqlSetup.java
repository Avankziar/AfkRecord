package main.java.de.avankziar.afkrecord.bungee.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;

public class MysqlSetup 
{
	private Connection conn = null;
	private AfkRecord plugin;
	
	public MysqlSetup(AfkRecord plugin) 
	{
		this.plugin = plugin;
		loadMysqlSetup();
	}
	
	public boolean loadMysqlSetup()
	{
		if(!connectToDatabase())
		{
			return false;
		}
		if(!setupDatabaseI())
		{
			return false;
		}
		return true;
	}
	
	public boolean connectToDatabase() 
	{
		AfkRecord.log.info("Connecting to the database...");
		try 
		{
       	 	//Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", plugin.getYamlHandler().getConfig().getString("Mysql.User"));
            properties.setProperty("password", plugin.getYamlHandler().getConfig().getString("Mysql.Password"));
            properties.setProperty("autoReconnect", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true) + "");
            properties.setProperty("verifyServerCertificate", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false) + "");
            properties.setProperty("useSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            properties.setProperty("requireSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getYamlHandler().getConfig().getString("Mysql.Host") 
            		+ ":" + plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306) + "/" 
            		+ plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName"), properties);
           
          } catch (ClassNotFoundException e) 
		{
        	  AfkRecord.log.severe("Could not locate drivers for mysql! Error: " + e.getMessage());
            return false;
          } catch (SQLException e) 
		{
        	  AfkRecord.log.severe("Could not connect to mysql database! Error: " + e.getMessage());
            return false;
          }
		AfkRecord.log.info("Database connection successful!");
		return true;
	}
	
	public boolean setupDatabaseI() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		    	  String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameI
			        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
			        		+ " player_uuid char(36) NOT NULL UNIQUE,"
			        		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
			        		+ " alltime BIGINT NULL DEFAULT '0',"
			        		+ " activitytime BIGINT NULL DEFAULT '0',"
			        		+ " afktime BIGINT NULL DEFAULT '0',"
			        		+ " lastactivity BIGINT NULL DEFAULT '0',"
			        		+ " lasttimecheck BIGINT NULL DEFAULT '0',"
			        		+ " isafk boolean,"
			        		+ " isonline boolean);";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) {
		        e.printStackTrace();
		        AfkRecord.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally {
		    	  try {
		    		  if (query != null) {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public boolean setupDatabaseII() 
	{
		if (conn != null) 
		{
			PreparedStatement query = null;
		      try 
		      {	        
		    	  String data = "CREATE TABLE IF NOT EXISTS `" + plugin.getMysqlHandler().tableNameII
			        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
			        		+ " player_uuid char(36) NOT NULL,"
			        		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
			        		+ " timestamp_unix BIGINT NULL DEFAULT '0',"
			        		+ " alltime BIGINT NULL DEFAULT '0',"
			        		+ " activitytime BIGINT NULL DEFAULT '0',"
			        		+ " afktime BIGINT NULL DEFAULT '0');";
		        query = conn.prepareStatement(data);
		        query.execute();
		      } catch (SQLException e) {
		        e.printStackTrace();
		        AfkRecord.log.severe("Error creating tables! Error: " + e.getMessage());
		        return false;
		      } finally {
		    	  try {
		    		  if (query != null) {
		    			  query.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    		  return false;
		    	  }
		      }
		}
		return true;
	}
	
	public Connection getConnection() {
		checkConnection();
		return conn;
	}
	
	public void checkConnection() {
		try {
			if (conn == null) {
				AfkRecord.log.warning("Connection failed. Reconnecting...");
				reConnect();
			}
			if (!conn.isValid(3)) {
				AfkRecord.log.warning("Connection is idle or terminated. Reconnecting...");
				reConnect();
			}
			if (conn.isClosed() == true) {
				AfkRecord.log.warning("Connection is closed. Reconnecting...");
				reConnect();
			}
		} catch (Exception e) {
			AfkRecord.log.severe("Could not reconnect to Database! Error: " + e.getMessage());
		}
	}
	
	public boolean reConnect() 
	{
		try 
		{            
            long start = 0;
			long end = 0;
			
		    start = System.currentTimeMillis();
		    AfkRecord.log.info("Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", plugin.getYamlHandler().getConfig().getString("Mysql.User"));
            properties.setProperty("password", plugin.getYamlHandler().getConfig().getString("Mysql.Password"));
            properties.setProperty("autoReconnect", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true) + "");
            properties.setProperty("verifyServerCertificate", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false) + "");
            properties.setProperty("useSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            properties.setProperty("requireSSL", 
            		plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false) + "");
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + plugin.getYamlHandler().getConfig().getString("Mysql.Host") 
            		+ ":" + plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306) + "/" 
            		+ plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName"), properties);
		    end = System.currentTimeMillis();
		    AfkRecord.log.info("Connection to MySQL server established!");
		    AfkRecord.log.info("Connection took " + ((end - start)) + "ms!");
            return true;
		} catch (Exception e) 
		{
			AfkRecord.log.severe("Error re-connecting to the database! Error: " + e.getMessage());
			return false;
		}
	}
	
	public void closeConnection() {
		try {
			AfkRecord.log.info("Closing database connection...");
			conn.close();
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
