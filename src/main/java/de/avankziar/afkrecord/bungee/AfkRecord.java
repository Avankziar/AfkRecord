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
import main.java.de.avankziar.afkrecord.bungee.database.YamlManager;
import main.java.de.avankziar.afkrecord.bungee.listener.EventAfkCheck;
import main.java.de.avankziar.afkrecord.bungee.object.PluginUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class AfkRecord extends Plugin
{
	private static AfkRecord plugin;
	public static Logger log;
	public static String pluginName = "AfkRecord";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	
	public void onEnable() 
	{
		plugin = this;
		log = getLogger();
		yamlHandler = new YamlHandler(plugin);
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(plugin);
		} else
		{
			disablePlugin();
			log.severe("MySQL is not enabled! "+pluginName+" is disabled!");
		}
		getProxy().registerChannel("afkr:afkrecordout");
		getProxy().registerChannel("afkrecord:afkrecordin");
		getProxy().getPluginManager().registerListener(plugin, new EventAfkCheck());
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(plugin);
		//HandlerList.unregisterAll();
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
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
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}
	
	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public static AfkRecord getPlugin()
	{
		return plugin;
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
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getDataI(plugin, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.isAFK() : false;
	}
	
	public long lastActivity(ProxiedPlayer player)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getDataI(plugin, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getLastActivity() : 0;
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
	
	public boolean existOfflinePlayer(String playername)
	{
		if(plugin.getMysqlHandler().existI(plugin,
				"`player_name` = ? AND `isonline` = ?", playername, false))
		{
			return true;
		}
		return false;
	}
	
	public boolean existOnlinePlayer(ProxiedPlayer player)
	{
		if(plugin.getMysqlHandler().existI(plugin,
				"`player_uuid` = ? AND `isonline` = ?", player.getUniqueId().toString(), true))
		{
			return true;
		}
		return false;
	}
	
	public PluginUser getOfflineUser(String playername)
	{
		if(existOfflinePlayer(playername))
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getDataI(plugin,
					"`player_uuid` = ? AND `isonline` = ?", playername, false);
			return user;
		}
		return null;
	}
	
	public PluginUser getOnlineUser(ProxiedPlayer player)
	{
		if(player == null)
		{
			return null;
		}
		if(existOnlinePlayer(player))
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getDataI(plugin,
					"`player_uuid` = ? AND `online` = ?", player.getUniqueId().toString(), true);
			return user;
		}
		return null;
	}
	
	public long getTimes(Type type, String playername)
	{
		PluginUser user = null;
		if(!existOfflinePlayer(playername))
		{
			return 0;
		}
		user = (PluginUser) plugin.getMysqlHandler().getDataI(plugin,
				"`player_uuid` = ?", playername);
		switch(type)
		{
		case ALL:
			return user.getAllTime();
		case ONLINE:
			return user.getActivityTime();
		case AFK:
			return user.getAfkTime();
		case LASTACTIVITY:
			return user.getLastActivity();
		case LASTTIMECHECK:
			return user.getLastTimeCheck();
		}
		return 0;
	}
	
	public enum Type
	{
		ONLINE, AFK, ALL, LASTACTIVITY, LASTTIMECHECK;
	}
}
