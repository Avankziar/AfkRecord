package main.java.de.avankziar.afkrecord.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import main.java.de.avankziar.afkrecord.bungee.database.MysqlInterface;
import main.java.de.avankziar.afkrecord.bungee.database.MysqlSetup;
import main.java.de.avankziar.afkrecord.bungee.database.YamlHandler;
import main.java.de.avankziar.afkrecord.bungee.listener.EVENTAfkCheck;
import main.java.de.avankziar.afkrecord.bungee.listener.ServerListener;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class AfkRecord extends Plugin
{
	public static Logger log;
	public static String pluginName = "AfkRecord";
	private static YamlHandler yamlHandler;
	private static MysqlSetup databaseHandler;
	private static MysqlInterface mysqlinterface;
	private static Utility utility;
	private static AfkRecord plugin;
	
	public void onEnable() 
	{
		plugin = this;
		log = getLogger();
		yamlHandler = new YamlHandler(this);
		utility = new Utility();
		if(yamlHandler.get().getString("mysql.status").equalsIgnoreCase("true"))
		{
			mysqlinterface = new MysqlInterface(this);
			databaseHandler = new MysqlSetup(this);
		} else
		{
			log.severe("MySQL is not enabled! "+pluginName+" wont work correctly!");
		}
		getProxy().registerChannel("afkrecord:afkrecordout");
		getProxy().getPluginManager().registerListener(this, new EVENTAfkCheck());
		getProxy().getPluginManager().registerListener(this, new ServerListener(this));
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(this);
		//HandlerList.unregisterAll();
		if(yamlHandler.get().getString("mysql.status").equalsIgnoreCase("true"))
		{
			if (databaseHandler.getConnection() != null) 
			{
				//backgroundtask.onShutDownDataSave();
				databaseHandler.closeConnection();
			}
		}
		
		log.info(pluginName + " is disabled!");
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public MysqlSetup getDatabaseHandler() 
	{
		return databaseHandler;
	}
	
	public MysqlInterface getMysqlInterface()
	{
		return mysqlinterface;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public static AfkRecord getPlugin()
	{
		return plugin;
	}
	
	public boolean isAfk(ProxiedPlayer player)
	{
		if(!mysqlinterface.hasAccount(player))
		{
			return false;
		}
		return (boolean) mysqlinterface.getDataI(player, "isafk", "player_uuid");
	}
	
	public void softSave(ProxiedPlayer player)
	{
		ServerInfo server = player.getServer().getInfo();
		String µ = "µ";
		String message = "softsave"+µ+player.getUniqueId().toString();
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        String msg = message;
        try {
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    server.sendData("afkrecord:afkrecordout", streamout.toByteArray());
	    return;
	}
}
