package main.java.de.avankziar.afkrecord.bungee.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler;

public class MysqlSetup 
{
	private Connection conn = null;
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	private boolean isAutoConnect;
	private boolean isVerifyServerCertificate;
	private boolean isSSLEnabled;
	
	public MysqlSetup(AfkRecord plugin) 
	{
		host = plugin.getYamlHandler().getConfig().getString("Mysql.Host");
		port = plugin.getYamlHandler().getConfig().getInt("Mysql.Port", 3306);
		database = plugin.getYamlHandler().getConfig().getString("Mysql.DatabaseName");
		user = plugin.getYamlHandler().getConfig().getString("Mysql.User");
		password = plugin.getYamlHandler().getConfig().getString("Mysql.Password");
		isAutoConnect = plugin.getYamlHandler().getConfig().getBoolean("Mysql.AutoReconnect", true);
		isVerifyServerCertificate = plugin.getYamlHandler().getConfig().getBoolean("Mysql.VerifyServerCertificate", false);
		isSSLEnabled = plugin.getYamlHandler().getConfig().getBoolean("Mysql.SSLEnabled", false);
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
		if(!setupDatabaseII())
		{
			return false;
		}
		return true;
	}
	
	public boolean connectToDatabase() 
	{
		AfkRecord.log.info("Connecting to the database...");
		boolean bool = false;
	    try
	    {
	    	// Load new Drivers for papermc
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	bool = true;
	    } catch (Exception e)
	    {
	    	bool = false;
	    } 
	    try
	    {
	    	if (bool == false)
	    	{
	    		// Load old Drivers for spigot
	    		Class.forName("com.mysql.jdbc.Driver");
	    	}
	        Properties properties = new Properties();
	        properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("autoReconnect", String.valueOf(isAutoConnect));
            properties.setProperty("verifyServerCertificate", String.valueOf(isVerifyServerCertificate));
            properties.setProperty("useSSL", String.valueOf(isSSLEnabled));
            properties.setProperty("requireSSL", String.valueOf(isSSLEnabled));
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
            AfkRecord.log.info("Database connection successful!");
        } catch (ClassNotFoundException e) 
		{
        	AfkRecord.log.severe("Could not locate drivers for mysql! Error: " + e.getMessage());
            return false;
        } catch (SQLException e) 
		{
        	AfkRecord.log.severe("Could not connect to mysql database! Error: " + e.getMessage());
            return false;
        }
		return true;
	}
	
	public boolean setupDatabaseI() 
	{
		String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.PLUGINUSER.getValue()
        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
        		+ " player_uuid char(36) NOT NULL UNIQUE,"
        		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
        		+ " alltime BIGINT NULL DEFAULT '0',"
        		+ " activitytime BIGINT NULL DEFAULT '0',"
        		+ " afktime BIGINT NULL DEFAULT '0',"
        		+ " lastactivity BIGINT NULL DEFAULT '0',"
        		+ " lasttimecheck BIGINT NULL DEFAULT '0',"
        		+ " isafk boolean,"
        		+ " isonline boolean,"
        		+ " vacationtime BIGINT NULL DEFAULT '0');";
		baseSetup(data);
		return true;
	}
	
	public boolean setupDatabaseII() 
	{
		String data = "CREATE TABLE IF NOT EXISTS `" + MysqlHandler.Type.TIMERECORD.getValue()
        		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
        		+ " player_uuid char(36) NOT NULL,"
        		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
        		+ " timestamp_unix BIGINT NULL DEFAULT '0',"
        		+ " alltime BIGINT NULL DEFAULT '0',"
        		+ " activitytime BIGINT NULL DEFAULT '0',"
        		+ " afktime BIGINT NULL DEFAULT '0');";
		baseSetup(data);
		return true;
	}
	
	private boolean baseSetup(String data) 
	{
		try (Connection conn = getConnection(); PreparedStatement query = conn.prepareStatement(data))
		{
			query.execute();
		} catch (SQLException e) 
		{
			AfkRecord.log.log(Level.WARNING, "Could not build data source. Or connection is null", e);
		}
		return true;
	}
	
	public Connection getConnection() 
	{
		checkConnection();
		return conn;
	}
	
	public void checkConnection() 
	{
		try {
			if (conn == null) 
			{
				//AfkRecord.log.warning("Connection failed. Reconnecting...");
				reConnect();
			}
			if (!conn.isValid(3)) 
			{
				//AfkRecord.log.warning("Connection is idle or terminated. Reconnecting...");
				reConnect();
			}
			if (conn.isClosed() == true) 
			{
				//AfkRecord.log.warning("Connection is closed. Reconnecting...");
				reConnect();
			}
		} catch (Exception e) 
		{
			AfkRecord.log.severe("Could not reconnect to Database! Error: " + e.getMessage());
		}
	}
	
	public boolean reConnect() 
	{
		boolean bool = false;
	    try
	    {
	    	// Load new Drivers for papermc
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	bool = true;
	    } catch (Exception e)
	    {
	    	bool = false;
	    } 
	    try
	    {
	    	if (bool == false)
	    	{
	    		// Load old Drivers for spigot
	    		Class.forName("com.mysql.jdbc.Driver");
	    	}
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);
            properties.setProperty("autoReconnect", String.valueOf(isAutoConnect));
            properties.setProperty("verifyServerCertificate", String.valueOf(isVerifyServerCertificate));
            properties.setProperty("useSSL", String.valueOf(isSSLEnabled));
            properties.setProperty("requireSSL", String.valueOf(isSSLEnabled));
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
            return true;
		} catch (Exception e) 
		{
			AfkRecord.log.severe("Error re-connecting to the database! Error: " + e.getMessage());
			return false;
		}
	}
}
