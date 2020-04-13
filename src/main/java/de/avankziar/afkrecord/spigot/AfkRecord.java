package main.java.de.avankziar.afkrecord.spigot;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.de.avankziar.afkrecord.spigot.cmd.CMDAfk;
import main.java.de.avankziar.afkrecord.spigot.cmd.CMDAfkRecord;
import main.java.de.avankziar.afkrecord.spigot.cmd.CommandHandler;
import main.java.de.avankziar.afkrecord.spigot.cmd.TABCompleter;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlInterface;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlSetup;
import main.java.de.avankziar.afkrecord.spigot.database.YamlHandler;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;
import main.java.de.avankziar.afkrecord.spigot.listener.EVENTAkfCheck;
import main.java.de.avankziar.afkrecord.spigot.listener.EVENTJoinLeave;
import main.java.de.avankziar.afkrecord.spigot.listener.ServerListener;

public class AfkRecord extends JavaPlugin
{
	public static Logger log;
	public static String pluginName = "AfkRecord";
	private static YamlHandler yamlHandler;
	private static MysqlSetup databaseHandler;
	private static MysqlInterface mysqlinterface;
	private static BackgroundTask backgroundtask;
	private static Utility utility;
	private static CommandHandler commandHandler;
	private static AfkRecord plugin;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		yamlHandler = new YamlHandler(this);
		utility = new Utility(this);
		commandHandler = new CommandHandler(this);
		backgroundtask = new BackgroundTask(this);
		if(yamlHandler.get().getString("mysql.status").equalsIgnoreCase("true"))
		{
			mysqlinterface = new MysqlInterface(this);
			databaseHandler = new MysqlSetup(this);
		} else
		{
			log.severe("MySQL is not set in the Plugin "+pluginName+"!");
			Bukkit.getPluginManager().getPlugin("AfkRecord").getPluginLoader().disablePlugin(this);
			return;
		}
		CommandSetup();
		ListenerSetup();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		if(yamlHandler.get().getString("mysql.status").equalsIgnoreCase("true"))
		{
			if (databaseHandler.getConnection() != null) 
			{
				backgroundtask.onShutDownDataSave();
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
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundtask;
	}
	
	public CommandHandler getCommandHandler()
	{
		return commandHandler;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	public void CommandSetup()
	{
		getCommand("afkrecord").setExecutor(new CMDAfkRecord(this));
		getCommand("afkrecord").setTabCompleter(new TABCompleter());
		getCommand("afk").setExecutor(new CMDAfk(this));
	}
	
	public void ListenerSetup()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EVENTAkfCheck(this), this);
		pm.registerEvents(new EVENTJoinLeave(this), this);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "afkrecord:afkrecordout", new ServerListener(this));
	}
	
	public static AfkRecord getPlugin()
	{
		return plugin;
	}
	
	public boolean isAfk(Player player)
	{
		User u = User.getUser(player);
		if(u!=null)
		{
			return u.isIsafk();
		}
		return false;
	}
	
	public void softSave(Player player)
	{
		plugin.getUtility().softSave(player, true, true, false);
	}
}
