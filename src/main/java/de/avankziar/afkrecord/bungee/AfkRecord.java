package main.java.de.avankziar.afkrecord.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.de.avankziar.afkrecord.bungee.database.MysqlHandler;
import main.java.de.avankziar.afkrecord.bungee.database.MysqlSetup;
import main.java.de.avankziar.afkrecord.bungee.database.YamlHandler;
import main.java.de.avankziar.afkrecord.bungee.listener.EventAfkCheck;
import main.java.de.avankziar.afkrecord.bungee.listener.ServerListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class AfkRecord extends Plugin
{
	public static Logger log;
	public static String pluginName = "AfkRecord";
	private static YamlHandler yamlHandler;
	private static MysqlSetup mysqlSetup;
	private static MysqlHandler mysqlHandler;
	private static Utility utility;
	private static AfkRecord plugin;
	
	public void onEnable() 
	{
		plugin = this;
		log = getLogger();
		yamlHandler = new YamlHandler(this);
		utility = new Utility(this);
		if(yamlHandler.get().getBoolean("Mysql.Status", false))
		{
			mysqlHandler = new MysqlHandler(this);
			mysqlSetup = new MysqlSetup(this);
		} else
		{
			disablePlugin();
			log.severe("MySQL is not enabled! "+pluginName+" is disabled!");
		}
		getProxy().registerChannel("afkrecord:afkrecordout");
		getProxy().registerChannel("afkrecord:afkrecordin");
		getProxy().getPluginManager().registerListener(this, new EventAfkCheck(this));
		getProxy().getPluginManager().registerListener(this, new ServerListener(this));
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(this);
		//HandlerList.unregisterAll();
		if(yamlHandler.get().getBoolean("Mysql.Status", false))
		{
			if (mysqlSetup.getConnection() != null) 
			{
				//backgroundtask.onShutDownDataSave();
				mysqlSetup.closeConnection();
			}
		}
		
		log.info(pluginName + " is disabled!");
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public static AfkRecord getPlugin()
	{
		return plugin;
	}
	
	public boolean reload()
	{
		if(!yamlHandler.loadYamlHandler())
		{
			return false;
		}
		if(!utility.loadUtility())
		{
			return false;
		}
		if(yamlHandler.get().getBoolean("Mysql.Status", false))
		{
			mysqlSetup.closeConnection();
			if(!mysqlHandler.loadMysqlHandler())
			{
				return false;
			}
			if(!mysqlSetup.loadMysqlSetup())
			{
				return false;
			}
		} else 
		{
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void disablePlugin()
	{
		Plugin plugin = (Plugin) ProxyServer.getInstance().getPluginManager().getPlugin(pluginName);
	       
		try
		{
			plugin.onDisable();
			for (Handler handler : plugin.getLogger().getHandlers())
			{
				handler.close();
			}
		}
		catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Exception disabling plugin " + plugin.getDescription().getName(), t);
		}
		ProxyServer.getInstance().getPluginManager().unregisterCommands(plugin);
		ProxyServer.getInstance().getPluginManager().unregisterListeners(plugin);
		ProxyServer.getInstance().getScheduler().cancel(plugin);
		plugin.getExecutorService().shutdownNow();
	}
	
	public boolean isAfk(ProxiedPlayer player)
	{
		if(!mysqlHandler.hasAccount(player))
		{
			return false;
		}
		return (boolean) mysqlHandler.getDataI(player, "isafk", "player_uuid");
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
