package main.java.me.avankziar.afkr.general.commands.tree;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import main.java.me.avankziar.afkr.general.database.YamlHandling;

public class BaseConstructor
{
	private static YamlHandling yamlHandling;
	private static ArrayList<BaseConstructor> helpList = new ArrayList<>();
	private static ArrayList<CommandConstructor> commandTree = new ArrayList<>();
	private static LinkedHashMap<String, main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule> argumentMapSpigot = new LinkedHashMap<>();
	
	private String name;
	private String path;
	private String permission;
	private String suggestion;
	private String commandString;
	private String helpInfo;
	private boolean canConsoleAccess;
	
	public static void init(YamlHandling yamlHandling)
	{
		BaseConstructor.yamlHandling = yamlHandling;
	}
	
	public static YamlHandling getYamlHandling()
	{
		return yamlHandling;
	}
	
	public static ArrayList<BaseConstructor> getHelpList()
	{
		return helpList;
	}
	
	public static ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public static LinkedHashMap<String, main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule> getArgumentMapSpigot()
	{
		return argumentMapSpigot;
	}
	
	public BaseConstructor(String name, String path, String permission, String suggestion, String commandString,
			String helpInfo, boolean canConsoleAccess)
	{
		setName(name);
		setPath(path);
		setPermission(permission);
		setSuggestion(suggestion);
		setCommandString(commandString);
		setHelpInfo(helpInfo);
		setCanConsoleAccess(canConsoleAccess);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getPermission()
	{
		return permission;
	}

	public void setPermission(String permission)
	{
		this.permission = permission;
	}

	public String getSuggestion()
	{
		return suggestion;
	}

	public void setSuggestion(String suggestion)
	{
		this.suggestion = suggestion;
	}

	public String getCommandString()
	{
		return commandString;
	}

	public void setCommandString(String commandString)
	{
		this.commandString = commandString;
	}

	public boolean canConsoleAccess()
	{
		return canConsoleAccess;
	}

	public void setCanConsoleAccess(boolean canConsoleAccess)
	{
		this.canConsoleAccess = canConsoleAccess;
	}

	public String getHelpInfo()
	{
		return helpInfo;
	}

	public void setHelpInfo(String helpInfo)
	{
		this.helpInfo = helpInfo;
	}
}