package main.java.de.avankziar.afkrecord.spigot;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.de.avankziar.afkrecord.spigot.command.CommandHelper;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;
import main.java.de.avankziar.afkrecord.spigot.command.MultipleCommandExecutor;
import main.java.de.avankziar.afkrecord.spigot.command.TABCompleter;
import main.java.de.avankziar.afkrecord.spigot.command.afkrecord.ARGCountTime;
import main.java.de.avankziar.afkrecord.spigot.command.afkrecord.ARGGetAfk;
import main.java.de.avankziar.afkrecord.spigot.command.afkrecord.ARGGetTime;
import main.java.de.avankziar.afkrecord.spigot.command.afkrecord.ARGReload;
import main.java.de.avankziar.afkrecord.spigot.command.afkrecord.ARGTime;
import main.java.de.avankziar.afkrecord.spigot.command.afkrecord.ARGTop;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlSetup;
import main.java.de.avankziar.afkrecord.spigot.database.YamlHandler;
import main.java.de.avankziar.afkrecord.spigot.listener.EVENTAfkCheck;
import main.java.de.avankziar.afkrecord.spigot.listener.EVENTJoinLeave;
import main.java.de.avankziar.afkrecord.spigot.listener.ServerListener;
import main.java.de.avankziar.afkrecord.spigot.object.User;
import net.milkbowl.vault.permission.Permission;

public class AfkRecord extends JavaPlugin
{
	public static Logger log;
	public static String pluginName = "AfkRecord";
	private static YamlHandler yamlHandler;
	private static MysqlSetup mysqlSetup;
	private static MysqlHandler mysqlHandler;
	private static BackgroundTask backgroundtask;
	private static Utility utility;
	private static CommandHelper commandHelper;
	private static AfkRecord plugin;
	private static Permission perms = null;

	public static HashMap<String, CommandModule> afkrarguments;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		yamlHandler = new YamlHandler(this);
		utility = new Utility(this);
		afkrarguments = new HashMap<String, CommandModule>();
		commandHelper = new CommandHelper(this);
		backgroundtask = new BackgroundTask(this);
		if(yamlHandler.get().getBoolean("Mysql.Status", false))
		{
			mysqlHandler = new MysqlHandler(this);
			mysqlSetup = new MysqlSetup(this);
		} else
		{
			log.severe("MySQL is not set in the Plugin "+pluginName+"! Plugin is disabled");
			Bukkit.getPluginManager().getPlugin("AfkRecord").getPluginLoader().disablePlugin(this);
			return;
		}
		CommandSetup();
		ListenerSetup();
		setupPermissions();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
		if(yamlHandler.get().getBoolean("Mysql.Status", false))
		{
			if (mysqlSetup.getConnection() != null) 
			{
				backgroundtask.onShutDownDataSave();
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
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundtask;
	}
	
	public CommandHelper getCommandHelper()
	{
		return commandHelper;
	}
	
	public Utility getUtility()
	{
		return utility;
	}
	
	private void CommandSetup()
	{
		new ARGCountTime(this);
		//new ARGCountTimeList(this);
		new ARGGetAfk(this);
		new ARGGetTime(this);
		new ARGReload(this);
		new ARGTime(this);
		new ARGTop(this);
		getCommand("afkr").setExecutor(new MultipleCommandExecutor(this));
		getCommand("afkr").setTabCompleter(new TABCompleter());
		getCommand("afk").setExecutor(new MultipleCommandExecutor(this));
	}
	
	private void ListenerSetup()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EVENTAfkCheck(this), this);
		pm.registerEvents(new EVENTJoinLeave(this), this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "afkrecord:afkrecordin");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "afkrecord:afkrecordout", new ServerListener(this));
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
	
	public static AfkRecord getPlugin()
	{
		return plugin;
	}
	
	private boolean setupPermissions() 
	{
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) 
        {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	public static Permission getPerms()
	{
		return perms;
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
	
	public void hardSave(Player player, boolean removeUser)
	{
		utility.hardSave(player, removeUser);
	}
	
	public boolean existOfflinePlayer(OfflinePlayer player)
	{
		if((Boolean) mysqlHandler.hasAccount(player.getUniqueId().toString()))
		{
			return true;
		}
		return false;
	}
	
	public boolean existOnlinePlayer(Player player)
	{
		if((Boolean) mysqlHandler.hasAccount(player.getUniqueId().toString()))
		{
			return true;
		}
		return false;
	}
	
	public User getOfflineUser(OfflinePlayer player)
	{
		User u = null;
		if(existOfflinePlayer(player))
		{
			u = new User(player,
					player.getName(),
					System.currentTimeMillis(),
					(Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "activitytime", "player_uuid"),
					(Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "afktime", "player_uuid"),
					(Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "alltime", "player_uuid"),
					(Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "lastactivity", "player_uuid"),
					(Boolean) mysqlHandler.getDataI(player.getUniqueId().toString(), "isafk", "player_uuid"));
		}
		return u;
	}
	
	public User getOnlineUser(Player player)
	{
		User u = null;
		for(User us : User.getUsers())
		{
			if(us.getName().equals(player.getName()))
			{
				u = us;
				break;
			}
		}
		return u;
	}
	
	public long getTimes(Type type, OfflinePlayer player)
	{
		User u = null;
		if(!existOfflinePlayer(player))
		{
			return 0;
		}
		if(player.isOnline())
		{
			u = getOnlineUser(player.getPlayer());
		} else
		{
			u = getOfflineUser(player);
		}
		if(type == Type.ALL)
		{
			if(player.isOnline())
			{
				return u.getAlltime()+(Long) mysqlHandler.getDataI(player.getPlayer(), "alltime", "player_uuid");
			} else
			{
				return (Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "alltime", "player_uuid");
			}
		} else if(type == Type.ONLINE)
		{
			if(player.isOnline())
			{
				return u.getActivitytime()+(Long) mysqlHandler.getDataI(player.getPlayer(), "activitytime", "player_uuid");
			} else
			{
				return (Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "activitytime", "player_uuid");
			}
			
		} else if(type == Type.AFK)
		{
			if(player.isOnline())
			{
				return u.getAfktime()+(Long) mysqlHandler.getDataI(player.getPlayer(), "afktime", "player_uuid");
			} else
			{
				return (Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "afktime", "player_uuid");
			}
			
		} else if(type == Type.LASTACTIVITY)
		{
			if(player.isOnline())
			{
				return u.getLastactivity();
			} else
			{
				return (Long) mysqlHandler.getDataI(player.getUniqueId().toString(), "lastactivity", "player_uuid");
			}
		} else if(type == Type.LASTTIMECHECK)
		{
			if(player.isOnline())
			{
				return u.getLasttimecheck();
			} else
			{
				return 0;
			}
		}
		return 0;
	}
	
	public enum Type
	{
		ONLINE, AFK, ALL, LASTACTIVITY, LASTTIMECHECK;
	}
}
