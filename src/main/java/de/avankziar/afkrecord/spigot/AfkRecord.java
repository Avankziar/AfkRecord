package main.java.de.avankziar.afkrecord.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.de.avankziar.afkrecord.spigot.assistance.BackgroundTask;
import main.java.de.avankziar.afkrecord.spigot.assistance.Utility;
import main.java.de.avankziar.afkrecord.spigot.cmd.AfkCommandExecutor;
import main.java.de.avankziar.afkrecord.spigot.cmd.AfkRCommandExecutor;
import main.java.de.avankziar.afkrecord.spigot.cmd.CommandHelper;
import main.java.de.avankziar.afkrecord.spigot.cmd.TABCompletion;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGBypass;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGConvert;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGCountTime;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGCountTimePermission;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGGetAfk;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGGetTime;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGTime;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGTop;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGTop_AfkTime;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGTop_AllTime;
import main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord.ARGTop_OnlineTime;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.BaseConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.CommandConstructor;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlSetup;
import main.java.de.avankziar.afkrecord.spigot.database.YamlHandler;
import main.java.de.avankziar.afkrecord.spigot.database.YamlManager;
import main.java.de.avankziar.afkrecord.spigot.interfacehub.PlayerTimesAPI;
import main.java.de.avankziar.afkrecord.spigot.listener.JoinQuitListener;
import main.java.de.avankziar.afkrecord.spigot.listener.ServerListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerAsyncChatListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerCommandPreprocessListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerFishListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerInteractEntityListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerItemConsumeListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerLevelChangeListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerMoveListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerToggleSneakListener;
import main.java.de.avankziar.afkrecord.spigot.listener.afkcheck.PlayerToggleSprintListener;
import main.java.de.avankziar.afkrecord.spigot.object.PluginSettings;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.papi.Expansion;
import main.java.de.avankziar.afkrecord.spigot.permission.BypassPermission;
import main.java.de.avankziar.afkrecord.spigot.permission.KeyHandler;
import net.milkbowl.vault.permission.Permission;

public class AfkRecord extends JavaPlugin
{
	public static Logger log;
	public static String pluginName = "AfkRecord";
	public static boolean isPapiRegistered = false;
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private BackgroundTask backgroundtask;
	private Utility utility;
	private CommandHelper commandHelper;
	private static AfkRecord plugin;
	private static Permission perms = null;
	private static PlayerTimesAPI ptapi;
	
	private ArrayList<BaseConstructor> helpList = new ArrayList<>();
	private ArrayList<CommandConstructor> commandTree = new ArrayList<>();
	private LinkedHashMap<String, ArgumentModule> argumentMap = new LinkedHashMap<>();
	private ArrayList<String> players = new ArrayList<>();
	public static String baseCommandI = "afkr"; //Pfad angabe + ürspungliches Commandname
	public static String baseCommandII = "afk";
	public static String baseCommandIName = ""; //CustomCommand name
	public static String baseCommandIIName = "";
	
	public static String infoCommandPath = "CmdAfkRecord";
	public static String infoCommand = "/";
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=AFKR
		log.info("  █████╗ ███████╗██╗  ██╗██████╗  | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info(" ██╔══██╗██╔════╝██║ ██╔╝██╔══██╗ | Author: "+plugin.getDescription().getAuthors().toString());
		log.info(" ███████║█████╗  █████╔╝ ██████╔╝ | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info(" ██╔══██║██╔══╝  ██╔═██╗ ██╔══██╗ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info(" ██║  ██║██║     ██║  ██╗██║  ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info(" ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝ | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		yamlHandler = new YamlHandler(plugin);
		
		utility = new Utility(plugin);
		commandHelper = new CommandHelper(plugin);
		backgroundtask = new BackgroundTask(plugin);
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(plugin);
		} else
		{
			log.severe("MySQL is not set in the Plugin "+pluginName+"! Plugin is disabled");
			Bukkit.getPluginManager().getPlugin("AfkRecord").getPluginLoader().disablePlugin(plugin);
			return;
		}
		PluginSettings.initSettings(plugin);
		setupCommandTree();
		ListenerSetup();
		setupPermissions();
		setupPlayerTimes();
		isPapiRegistered = setupPlaceholderAPI();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(plugin);
		HandlerList.unregisterAll(plugin);
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
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
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}
	
	public void setYamlManager(YamlManager yamlManager)
	{
		plugin.yamlManager = yamlManager;
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
	
	private void ListenerSetup()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JoinQuitListener(plugin), plugin);
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "afkrecord:afkrecordin");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "afkrecord:afkrecordout", new ServerListener(plugin));
		if(yamlHandler.getConfig().getBoolean("EventListener.AsyncChat", false))
		{
			pm.registerEvents(new PlayerAsyncChatListener(plugin), plugin);
			log.info("AsyncChatListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.CommandPreprocess", false))
		{
			pm.registerEvents(new PlayerCommandPreprocessListener(plugin), plugin);
			log.info("CommandPreprocessListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.Fish", false))
		{
			pm.registerEvents(new PlayerFishListener(plugin), plugin);
			log.info("FishListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.InteractEntity", false))
		{
			pm.registerEvents(new PlayerInteractEntityListener(plugin), plugin);
			log.info("InteractEntityListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.ItemConsume", false))
		{
			pm.registerEvents(new PlayerItemConsumeListener(plugin), plugin);
			log.info("ItemConsumeListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.LevelChange", false))
		{
			pm.registerEvents(new PlayerLevelChangeListener(plugin), plugin);
			log.info("LevelChangeListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.Move", false))
		{
			pm.registerEvents(new PlayerMoveListener(plugin), plugin);
			log.info("MoveListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.ToggleSneak", false))
		{
			pm.registerEvents(new PlayerToggleSneakListener(plugin), plugin);
			log.info("ToggleSneakListener is active");
		}
		if(yamlHandler.getConfig().getBoolean("EventListener.ToggleSprint", false))
		{
			pm.registerEvents(new PlayerToggleSprintListener(plugin), plugin);
			log.info("ToggleSprintListener is active");
		}
	}
	
	private void setupCommandTree()
	{
		baseCommandIName = plugin.getYamlHandler().getCom().getString(baseCommandI+".Name");
		baseCommandIIName = plugin.getYamlHandler().getCom().getString(baseCommandII+".Name");
		infoCommand += plugin.getYamlHandler().getCom().getString("afkr.Name");
		LinkedHashMap<Integer, ArrayList<String>> playerMapI = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapII = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapIII = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapIV = new LinkedHashMap<>();
		LinkedHashMap<Integer, ArrayList<String>> playerMapV = new LinkedHashMap<>();
		
		//setupPlayers();
		ArrayList<String> playerarray = getPlayers();
		
		Collections.sort(playerarray);
		playerMapI.put(1, playerarray);
		playerMapII.put(2, playerarray);
		playerMapIII.put(3, playerarray);
		playerMapIV.put(4, playerarray);
		playerMapV.put(5, playerarray);
		
		ArgumentConstructor bypass = new ArgumentConstructor(baseCommandI+"_bypass", 0, 0, 0, false, null);
		ArgumentConstructor convert = new ArgumentConstructor(baseCommandI+"_convert", 0, 0, 1, false, null);
		ArgumentConstructor counttime = new ArgumentConstructor(baseCommandI+"_counttime", 0, 1, 2, false, null);
		PluginSettings.settings.addCommands(KeyHandler.COUNTTIME, counttime.getCommandString());
		ArgumentConstructor counttimeperm = new ArgumentConstructor(baseCommandI+"_counttimeperm", 0, 2, 2, false, null);
		ArgumentConstructor getafk = new ArgumentConstructor(baseCommandI+"_getafk", 0, 0, 0, false, null);
		ArgumentConstructor gettime = new ArgumentConstructor(baseCommandI+"_gettime", 0, 0, 2, false, null);
		ArgumentConstructor time = new ArgumentConstructor(baseCommandI+"_time", 0, 0, 1, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TIME, time.getCommandString());
		
		ArgumentConstructor top_onlinetime = new ArgumentConstructor(baseCommandI+"_top_onlinetime", 1, 1, 2, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TOP_ACTIVITYTIME, top_onlinetime.getCommandString());
		ArgumentConstructor top_alltime = new ArgumentConstructor(baseCommandI+"_top_alltime", 1, 1, 2, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TOP_ALLTIME, top_alltime.getCommandString());
		ArgumentConstructor top_afktime = new ArgumentConstructor(baseCommandI+"_top_afktime", 1, 1, 2, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TOP_AFKTIME, top_afktime.getCommandString());
		ArgumentConstructor top = new ArgumentConstructor(baseCommandI+"_top", 0, 2, 3, false, null,
				top_onlinetime, top_alltime, top_afktime);
		
		CommandConstructor afkr = new CommandConstructor(baseCommandIName, false,
				bypass, convert, counttime, counttimeperm, getafk, gettime, time, top);
		
		registerCommand(afkr.getPath(), afkr.getName());
		getCommand(afkr.getName()).setExecutor(new AfkRCommandExecutor(plugin, afkr));
		getCommand(afkr.getName()).setTabCompleter(new TABCompletion(plugin));
		
		CommandConstructor afk = new CommandConstructor(baseCommandIIName, false);
		
		registerCommand(afk.getPath(), afk.getName());
		getCommand(afk.getName()).setExecutor(new AfkCommandExecutor(plugin, afk));
		getCommand(afk.getName()).setTabCompleter(new TABCompletion(plugin));
		
		addingHelps(afkr,
						convert, bypass, counttime, counttimeperm, getafk, gettime, time, top,
							top_onlinetime, top_alltime, top_afktime,
					afk);
		
		new ARGConvert(plugin, convert);
		new ARGBypass(plugin, bypass);
		new ARGCountTime(plugin, counttime);
		new ARGCountTimePermission(plugin, counttimeperm);
		//new ARGCountTimeList(plugin);
		new ARGGetAfk(plugin, getafk);
		new ARGGetTime(plugin, gettime);
		//new ARGReload(plugin);
		new ARGTime(plugin, time);
		new ARGTop(plugin, top);
		new ARGTop_AllTime(plugin, top_alltime);
		new ARGTop_OnlineTime(plugin, top_onlinetime);
		new ARGTop_AfkTime(plugin, top_afktime);
	}
	
	public void setupBypassPerm()
	{
		String path = "Bypass.";
		BypassPermission.COUNTTIMEOTHER = yamlHandler.getCom().getString(path+"CountTimeOther");
		BypassPermission.GETTIMEOTHER = yamlHandler.getCom().getString(path+"GetTimeOther");
		BypassPermission.TIMEOTHER = yamlHandler.getCom().getString(path+"TimeOther");
	}
	
	public ArrayList<BaseConstructor> getHelpList()
	{
		return helpList;
	}
	
	public void addingHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			helpList.add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public CommandConstructor getCommandFromPath(String commandpath)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getPath().equalsIgnoreCase(commandpath))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public CommandConstructor getCommandFromCommandString(String command)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getName().equalsIgnoreCase(command))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, AfkRecord plugin) 
	{
		PluginCommand command = null;
	 
		try 
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) 
		{
			e.printStackTrace();
		} catch (InvocationTargetException e) 
		
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
	 
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return argumentMap;
	}
	
	public ArrayList<String> getMysqlPlayers()
	{
		return players;
	}

	public void setMysqlPlayers(ArrayList<String> players)
	{
		plugin.players = players;
	}
	
	public static AfkRecord getPlugin()
	{
		return plugin;
	}
	
	public ArrayList<String> getPlayers()
	{
		return players;
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
	
	private void setupPlayerTimes()
	{      
        if (plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
		{
			ptapi = new PlayerTimesAPI(this);
            plugin.getServer().getServicesManager().register(
            		main.java.me.avankziar.interfacehub.spigot.interfaces.PlayerTimes.class,
            		ptapi,
            		this,
                    ServicePriority.Normal);
            log.info(pluginName + " detected InterfaceHub. Hooking!");
            return;
        }
	}
	
	public Permission getPerms()
	{
		return perms;
	}
	
	private boolean setupPlaceholderAPI()
	{
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
		{
            new Expansion(plugin).register();
            return true;
		}
		return false;
	}
	
	public boolean isAfk(Player player)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		if(user != null)
		{
			return user.isAFK();
		}
		return false;
	}
	
	public long lastActivity(Player player)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		if(user != null)
		{
			return user.getLastActivity();
		}
		return 0;
	}
	
	public void softSave(Player player)
	{
		if(!player.isOnline())
		{
			return;
		}
		plugin.getUtility().debug(player, "AfkR Main Class");
		plugin.getUtility().save(player, false, false, false, false);
	}
	
	public boolean existOfflinePlayer(OfflinePlayer player)
	{
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLUGINUSER,
				"`player_uuid` = ? AND `isonline` = ?", player.getUniqueId().toString(), false))
		{
			return true;
		}
		return false;
	}
	
	public boolean existOnlinePlayer(Player player)
	{
		if(plugin.getMysqlHandler().exist(MysqlHandler.Type.PLUGINUSER,
				"`player_uuid` = ? AND `isonline` = ?", player.getUniqueId().toString(), true))
		{
			return true;
		}
		return false;
	}
	
	public PluginUser getOfflineUser(OfflinePlayer player)
	{
		if(existOfflinePlayer(player))
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
					"`player_uuid` = ? AND `isonline` = ?", player.getUniqueId().toString(), false);
			return user;
		}
		return null;
	}
	
	public PluginUser getOnlineUser(Player player)
	{
		if(!player.isOnline())
		{
			return null;
		}
		if(existOnlinePlayer(player))
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
					"`player_uuid` = ? AND `online` = ?", player.getUniqueId().toString(), true);
			return user;
		}
		return null;
	}
	
	public long getTimes(Type type, OfflinePlayer player)
	{
		PluginUser user = null;
		if(!existOfflinePlayer(player))
		{
			return 0;
		}
		user = (PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
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
