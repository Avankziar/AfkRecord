package main.java.me.avankziar.afkr.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.general.commands.tree.BaseConstructor;
import main.java.me.avankziar.afkr.general.commands.tree.CommandConstructor;
import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.database.YamlManager;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.spigot.assistance.BackgroundTask;
import main.java.me.avankziar.afkr.spigot.assistance.Utility;
import main.java.me.avankziar.afkr.spigot.cmd.AfkCommandExecutor;
import main.java.me.avankziar.afkr.spigot.cmd.AfkRCommandExecutor;
import main.java.me.avankziar.afkr.spigot.cmd.CommandHelper;
import main.java.me.avankziar.afkr.spigot.cmd.TABCompletion;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGBypass;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGCountTime;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGCountTimePermission;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGGetAfk;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGGetTime;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGTime;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGTop;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGTop_AfkTime;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGTop_AllTime;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGTop_OnlineTime;
import main.java.me.avankziar.afkr.spigot.cmd.afkrecord.ARGVacation;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;
import main.java.me.avankziar.afkr.spigot.database.MysqlHandler;
import main.java.me.avankziar.afkr.spigot.database.MysqlSetup;
import main.java.me.avankziar.afkr.spigot.database.YamlHandler;
import main.java.me.avankziar.afkr.spigot.handler.PlayerTimesHandler;
import main.java.me.avankziar.afkr.spigot.hook.PAPIHook;
import main.java.me.avankziar.afkr.spigot.ifh.PlayerTimesAPI;
import main.java.me.avankziar.afkr.spigot.listener.JoinQuitListener;
import main.java.me.avankziar.afkr.spigot.listener.ServerListener;
import main.java.me.avankziar.afkr.spigot.listener.afkcheck.BaseListener;
import main.java.me.avankziar.afkr.spigot.listener.afkcheck.PlayerListener;
import main.java.me.avankziar.afkr.spigot.metrics.Metrics;
import main.java.me.avankziar.afkr.spigot.object.PluginSettings;
import main.java.me.avankziar.afkr.spigot.permission.BypassPermission;
import main.java.me.avankziar.afkr.spigot.permission.KeyHandler;
import me.avankziar.ifh.spigot.administration.Administration;
import net.milkbowl.vault.permission.Permission;

public class AfkR extends JavaPlugin
{
	public static Logger logger;
	public static String pluginName = "AfkRecord";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private BackgroundTask backgroundtask;
	private Utility utility;
	private CommandHelper commandHelper;
	private static AfkR plugin;
	
	private PlayerTimesHandler pth;
	
	private static Permission perms = null;
	private static PlayerTimesAPI ptapi;
	
	private static Administration administrationConsumer;
	
	private ArrayList<String> players = new ArrayList<>();
	public static String baseCommandI = "afkr"; //Pfad angabe + ürspungliches Commandname
	public static String baseCommandII = "afk";
	public static String baseCommandIName = ""; //CustomCommand name
	public static String baseCommandIIName = "";
	
	public static String infoCommandPath = "CmdAfkRecord";
	public static String infoCommand = "/";
	
	public static String afkcmd = "";
	
	public void onEnable()
	{
		plugin = this;
		logger = getLogger();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=AFKR
		logger.info("  █████╗ ███████╗██╗  ██╗██████╗  | API-Version: "+plugin.getDescription().getAPIVersion());
		logger.info(" ██╔══██╗██╔════╝██║ ██╔╝██╔══██╗ | Author: "+plugin.getDescription().getAuthors().toString());
		logger.info(" ███████║█████╗  █████╔╝ ██████╔╝ | Plugin Website: "+plugin.getDescription().getWebsite());
		logger.info(" ██╔══██║██╔══╝  ██╔═██╗ ██╔══██╗ | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		logger.info(" ██║  ██║██║     ██║  ██╗██║  ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		logger.info(" ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝ | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(YamlManager.Type.VELO, pluginName, logger, plugin.getDataFolder().toPath(),
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
		
		utility = new Utility(plugin);
		commandHelper = new CommandHelper(plugin);
		backgroundtask = new BackgroundTask(plugin);
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm ||  yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		} else
		{
			logger.severe("MySQL is not set in the Plugin "+pluginName+"! Plugin is disabled");
			Bukkit.getPluginManager().getPlugin("AfkRecord").getPluginLoader().disablePlugin(plugin);
			return;
		}
		PluginSettings.initSettings(plugin);
		setPlayers();
		setupCommandTree();
		ListenerSetup();
		pth = new PlayerTimesHandler(plugin);
		setupPermissions();
		setupPlayerTimes();
		setupPlaceholderAPI();
		setupBstats();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(plugin);
		HandlerList.unregisterAll(plugin);
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			try
			{
				if (mysqlSetup.getConnection() != null) 
				{
					backgroundtask.onShutDownDataSave();
				}
			} catch (SQLException e)
			{}
		}
		logger.info(pluginName + " is disabled!");
	}
	
	public PlayerTimesHandler getPlayerTimes()
	{
		return pth;
	}
	
	private void ListenerSetup()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JoinQuitListener(plugin), plugin);
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "afkrecord:afkrecordin");
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "afkr:afkrecordout", new ServerListener(plugin));
		int i = 0;
		int j = 0;
		PlayerListener pl = new PlayerListener();
		for(BaseListener.EventType et : new ArrayList<BaseListener.EventType>(EnumSet.allOf(BaseListener.EventType.class)))
		{
			j++;
			if(BaseListener.isEventActive(et))
			{
				i++;
				switch(et)
				{
				case AsyncPlayerChat:
					pm.registerEvents(pl. new AsyncChatListener(plugin, et), plugin); break;
				case PlayerArmorStandManipulate:
					pm.registerEvents(pl. new PlayerArmorStandManipulateListener(plugin, et), plugin); break;
				case PlayerBedEnter:
					pm.registerEvents(pl. new PlayerBedEnterListener(plugin, et), plugin); break;
				case PlayerBucketEmpty:
					pm.registerEvents(pl. new PlayerBucketEmptyListener(plugin, et), plugin); break;
				case PlayerBucketFill:
					pm.registerEvents(pl. new PlayerBucketFillListener(plugin, et), plugin); break;
				case PlayerCommandPreprocess:
					pm.registerEvents(pl. new PlayerCommandPreprocessListener(plugin, et, afkcmd), plugin); break;
				case PlayerDropItem:
					pm.registerEvents(pl. new PlayerDropItemListener(plugin, et), plugin); break;
				case PlayerEditBook:
					pm.registerEvents(pl. new PlayerEditBookListener(plugin, et), plugin); break;
				case PlayerEggThrow:
					pm.registerEvents(pl. new PlayerEggThrowListener(plugin, et), plugin); break;
				case PlayerExpChange:
					pm.registerEvents(pl. new PlayerExpChangeListener(plugin, et), plugin); break;
				case PlayerFish:
					pm.registerEvents(pl. new PlayerFishListener(plugin, et), plugin); break;
				case PlayerGameModeChange:
					pm.registerEvents(pl. new PlayerGameModeChangeListener(plugin, et), plugin); break;
				case PlayerHarvestBlock:
					pm.registerEvents(pl. new PlayerHarvestBlockListener(plugin, et), plugin); break;
				case PlayerInteract:
					pm.registerEvents(pl. new PlayerInteractListener(plugin, et), plugin); break;
				case PlayerItemBreak:
					pm.registerEvents(pl. new PlayerItemBreakListener(plugin, et), plugin); break;
				case PlayerItemConsume:
					pm.registerEvents(pl. new PlayerItemConsumeListener(plugin, et), plugin); break;
				case PlayerItemDamage:
					pm.registerEvents(pl. new PlayerItemDamageListener(plugin, et), plugin); break;
				case PlayerLevelChange:
					pm.registerEvents(pl. new PlayerLevelChangeListener(plugin, et), plugin); break;
				case PlayerMove:
					pm.registerEvents(pl. new PlayerMoveListener(plugin, et), plugin); break;
				case PlayerToggleFlight:
					pm.registerEvents(pl. new PlayerToggleFlightListener(plugin, et), plugin); break;
				case PlayerToggleSneak:
					pm.registerEvents(pl. new PlayerToggleSneakListener(plugin, et), plugin); break;
				case PlayerToggleSprint:
					pm.registerEvents(pl. new PlayerToggleSprintListener(plugin, et), plugin); break;
				case PlayerUnleashEntity:
					pm.registerEvents(pl. new PlayerUnleashEntityListener(plugin, et), plugin); break;
				default:
					break;
				}
			}
		}
		logger.info("Listen to "+i+" from "+j+" Events.");
	}
	
	private void setupCommandTree()
	{
		baseCommandIName = plugin.getYamlHandler().getCommands().getString(baseCommandI+".Name");
		baseCommandIIName = plugin.getYamlHandler().getCommands().getString(baseCommandII+".Name");
		infoCommand += plugin.getYamlHandler().getCommands().getString("afkr.Name");
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
		ArgumentConstructor counttime = new ArgumentConstructor(baseCommandI+"_counttime", 0, 1, 2, false, playerMapII);
		PluginSettings.settings.addCommands(KeyHandler.COUNTTIME, counttime.getCommandString());
		ArgumentConstructor permcounttime = new ArgumentConstructor(baseCommandI+"_permcounttime", 0, 2, 2, false, null);
		ArgumentConstructor getafk = new ArgumentConstructor(baseCommandI+"_getafk", 0, 0, 0, false, null);
		ArgumentConstructor gettime = new ArgumentConstructor(baseCommandI+"_gettime", 0, 0, 2, false, playerMapII);
		ArgumentConstructor time = new ArgumentConstructor(baseCommandI+"_time", 0, 0, 1, false, playerMapI);
		PluginSettings.settings.addCommands(KeyHandler.TIME, time.getCommandString());
		
		ArgumentConstructor top_onlinetime = new ArgumentConstructor(baseCommandI+"_top_onlinetime", 1, 1, 3, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TOP_ACTIVITYTIME, top_onlinetime.getCommandString());
		ArgumentConstructor top_alltime = new ArgumentConstructor(baseCommandI+"_top_alltime", 1, 1, 3, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TOP_ALLTIME, top_alltime.getCommandString());
		ArgumentConstructor top_afktime = new ArgumentConstructor(baseCommandI+"_top_afktime", 1, 1, 3, false, null);
		PluginSettings.settings.addCommands(KeyHandler.TOP_AFKTIME, top_afktime.getCommandString());
		ArgumentConstructor top = new ArgumentConstructor(baseCommandI+"_top", 0, 0, 0, false, null,
				top_onlinetime, top_alltime, top_afktime);
		ArgumentConstructor vacation = new ArgumentConstructor(baseCommandI+"_vacation", 0, 0, 3, false, playerMapI);	
		CommandConstructor afkr = new CommandConstructor(baseCommandIName, false,
				bypass, counttime, permcounttime, getafk, gettime, time, top, vacation);
		
		registerCommand(afkr.getPath(), afkr.getName());
		getCommand(afkr.getName()).setExecutor(new AfkRCommandExecutor(plugin, afkr));
		getCommand(afkr.getName()).setTabCompleter(new TABCompletion(plugin));
		
		CommandConstructor afk = new CommandConstructor(baseCommandIIName, false);
		afkcmd = afk.getCommandString();
		registerCommand(afk.getPath(), afk.getName());
		getCommand(afk.getName()).setExecutor(new AfkCommandExecutor(plugin, afk));
		getCommand(afk.getName()).setTabCompleter(new TABCompletion(plugin));
		
		addingHelps(afkr,
						bypass, counttime, permcounttime, getafk, gettime, time, top,
							top_onlinetime, top_alltime, top_afktime, vacation,
					afk);
		
		new ARGBypass(plugin, bypass);
		new ARGCountTime(plugin, counttime);
		new ARGCountTimePermission(plugin, permcounttime);
		//new ARGCountTimeList(plugin);
		new ARGGetAfk(plugin, getafk);
		new ARGGetTime(plugin, gettime);
		//new ARGReload(plugin);
		new ARGTime(plugin, time);
		new ARGTop(plugin, top);
		new ARGTop_AllTime(plugin, top_alltime);
		new ARGTop_OnlineTime(plugin, top_onlinetime);
		new ARGTop_AfkTime(plugin, top_afktime);
		new ARGVacation(plugin, vacation);
	}
	
	public void setupBypassPerm()
	{
		String path = "Bypass.";
		BypassPermission.COUNTTIMEOTHER = yamlHandler.getCommands().getString(path+"CountTimeOther");
		BypassPermission.GETTIMEOTHER = yamlHandler.getCommands().getString(path+"GetTimeOther");
		BypassPermission.TIMEOTHER = yamlHandler.getCommands().getString(path+"TimeOther");
		BypassPermission.VACATIONOTHER = yamlHandler.getCommands().getString(path+"VacationOther");
	}
	
	public ArrayList<BaseConstructor> getHelpList()
	{
		return BaseConstructor.getHelpList();
	}
	
	public void addingHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			getHelpList().add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return BaseConstructor.getCommandTree();
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
	 
	private static PluginCommand getCommand(String name, AfkR plugin) 
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
		return BaseConstructor.getArgumentMapSpigot();
	}
	
	public ArrayList<String> getMysqlPlayers()
	{
		return players;
	}

	public void setMysqlPlayers(ArrayList<String> players)
	{
		if(players == null)
		{
			plugin.players = new ArrayList<>();
			return;
		}
		plugin.players = players;
	}
	
	public static AfkR getPlugin()
	{
		return plugin;
	}
	
	public void setPlayers()
	{
		ArrayList<PluginUser> l = PluginUser.convert(plugin.getMysqlHandler().getFullList(MysqlType.PLUGINUSER,
				"`player_name` ASC", "1"));
		ArrayList<String> li = new ArrayList<>();
		for(PluginUser pu : l)
		{
			li.add(pu.getPlayerName());
		}
		Collections.sort(li);
		plugin.players = li;
	}
	
	public ArrayList<String> getPlayers()
	{
		return players;
	}
	
	public void setupBstats()
	{
		int pluginId = 10968;
        new Metrics(plugin, pluginId);
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
            		me.avankziar.ifh.general.interfaces.PlayerTimes.class,
            		ptapi,
            		this,
                    ServicePriority.Normal);
            logger.info(pluginName + " detected InterfaceHub. Hooking!");
            return;
        }
	}
	
	public Permission getPerms()
	{
		return perms;
	}
	
	private void setupPlaceholderAPI()
	{
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
		{
            new PAPIHook(plugin).register();
            return;
		}
		return;
	}
	
	private void setupIFHAdministration()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		try
	    {
	    	RegisteredServiceProvider<me.avankziar.ifh.spigot.administration.Administration> rsp = 
                     getServer().getServicesManager().getRegistration(Administration.class);
		    if (rsp == null) 
		    {
		        return;
		    }
		    administrationConsumer = rsp.getProvider();
		    logger.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	    } catch(NoClassDefFoundError e) 
	    {}
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
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
}