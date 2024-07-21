package main.java.me.avankziar.afkr.spigot.object;

import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.database.YamlHandler;
import main.java.me.avankziar.afkr.spigot.AfkR;

public class PluginSettings
{
	private boolean mysql;
	private boolean debug;
	private LinkedHashMap<String, String> commands = new LinkedHashMap<>(); //To save commandstrings
	
	public static PluginSettings settings;
	
	public PluginSettings(){}
	
	public PluginSettings(boolean mysql, boolean debug)
	{
		setMysql(mysql);
		setDebug(debug);
	}
	
	public static void initSettings(AfkR plugin)
	{
		YamlHandler yh = plugin.getYamlHandler();
		boolean mysql = false;
		try
		{
			if(plugin.getMysqlSetup().getConnection() != null)
			{
				mysql = true;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		boolean debug = yh.getConfig().getBoolean("Use.DebuggingMode", false);
		settings = new PluginSettings(mysql, debug);
		plugin.getLogger().info("Plugin Settings init...");
	}
	
	public static void debug(Player player, String s)
	{
		if(PluginSettings.settings != null && PluginSettings.settings.isDebug())
		{
			if(player != null)
			{
				player.sendMessage(s);
			}
			if(AfkR.getPlugin() != null)
			{
				AfkR.getPlugin().getLogger().info(s);
			}
		}
	}

	public boolean isMysql()
	{
		return mysql;
	}

	public void setMysql(boolean mysql)
	{
		this.mysql = mysql;
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public String getCommands(String key)
	{
		return commands.get(key);
	}

	public void setCommands(LinkedHashMap<String, String> commands)
	{
		this.commands = commands;
	}
	
	public void addCommands(String key, String commandString)
	{
		if(commands.containsKey(key))
		{
			commands.replace(key, commandString);
		} else
		{
			commands.put(key, commandString);
		}
	}
}
