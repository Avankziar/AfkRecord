package main.java.de.avankziar.afkrecord.spigot.command;

import java.util.HashMap;

import org.bukkit.command.CommandSender;

public abstract class CommandModule
{
	public String lable;
	public String permission;
    public int minArgs;
    public int maxArgs;
    public String[] aliases;

    public CommandModule(String lable, String permission, HashMap<String, CommandModule> map, 
    		int minArgs, int maxArgs, String... aliases)
    {
        this.lable = lable;
        this.permission = permission;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.aliases = aliases;

		map.put(lable, this);
		for(String alias : aliases)
		{
			map.put(alias, this);
		}
    }
    
    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args);
}
