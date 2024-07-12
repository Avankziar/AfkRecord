package main.java.me.avankziar.afkr.general.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

public class MysqlBaseSetup 
{
	@Nullable
	protected static Logger logger;
	protected String host;
	protected int port;
	protected  String database;
	protected String user;
	protected String password;
	protected boolean isAutoConnect;
	protected boolean isVerifyServerCertificate;
	protected boolean isSSLEnabled;
	
	public MysqlBaseSetup(Logger logger)
	{
		MysqlBaseSetup.logger = logger;
	}
	
	public void init( String host, int port, String database, String user, String password,
			boolean isAutoConnect, boolean isVerifyServerCertificate, boolean isSSLEnabled)
	{
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.isAutoConnect = isAutoConnect;
		this.isVerifyServerCertificate = isVerifyServerCertificate;
		this.isSSLEnabled = isSSLEnabled;
	}
	
	public boolean loadMysqlSetup(ServerType serverType)
	{
		if(!connectToDatabase())
		{
			return false;
		}
		for(MysqlType mt : MysqlType.values())
		{
			//Decide, on which server, the mysql Table should be used.
			ServerType st = ServerType.valueOf(mt.getUsedOnServer());
			switch(st)
			{
			case ALL: break;
			case PROXY:
				if(serverType == ServerType.BUNGEE || serverType == ServerType.VELOCITY)
				{
					break;
				} else
				{
					continue;
				}
			case BUNGEE:
			case SPIGOT:
			case VELOCITY:
				if(st == serverType)
				{
					break;
				} else
				{
					continue;
				}
			}
			if(!baseSetup(mt.getSetupQuery()))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean connectToDatabase() 
	{
		logger.info("Connecting to the database...");
		try
		{
			getConnection();
			logger.info("Database connection successful!");
			return true;
		} catch(Exception e) 
		{
			logger.log(Level.WARNING, "Could not connect to Database!", e);
			return false;
		}		
	}
	
	public Connection getConnection() throws SQLException
	{
		return reConnect();
	}
	
	private Connection reConnect() throws SQLException
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
	    if (bool == false)
    	{
    		// Load old Drivers for spigot
    		try
    		{
    			Class.forName("com.mysql.jdbc.Driver");
    		}  catch (Exception e) {}
    	}
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("autoReconnect", String.valueOf(isAutoConnect));
        properties.setProperty("verifyServerCertificate", String.valueOf(isVerifyServerCertificate));
        properties.setProperty("useSSL", String.valueOf(isSSLEnabled));
        properties.setProperty("requireSSL", String.valueOf(isSSLEnabled));
        //Connect to database
        Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
        return conn;
	}
	
	public boolean baseSetup(String data) 
	{
		try (Connection conn = getConnection(); PreparedStatement query = conn.prepareStatement(data))
		{
			query.execute();
		} catch (SQLException e) 
		{
			logger.log(Level.WARNING, "Could not build data source. Or connection is null", e);
		}
		return true;
	}
}