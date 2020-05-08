package main.java.de.avankziar.afkrecord.spigot.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class TABCompleter implements TabCompleter
{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		Player player = (Player)sender;
		HashMap<String, CommandModule> commandList = AfkRecord.afkrarguments;
		List<String> list = new ArrayList<String>();
		if(cmd.getName().equalsIgnoreCase("afkr") && args.length == 0)
		{
			for (String commandString : commandList.keySet()) 
			{
				CommandModule mod = commandList.get(commandString);
				if (player.hasPermission(mod.permission)) 
				{
					list.add(commandString);
				}
			}
			Collections.sort(list);
			return list;			
		} else if (cmd.getName().equalsIgnoreCase("afkr") && args.length == 1) 
		{
			if (!args[0].equals("")) 
			{
				for (String commandString : commandList.keySet()) 
				{
					CommandModule mod = commandList.get(commandString);
					if (player.hasPermission(mod.permission))
					{
						if (commandString.startsWith(args[0])) 
						{
							list.add(commandString);
						}
					}
				}
				Collections.sort(list);
				return list;
			} else
			{
				for (String commandString : commandList.keySet()) 
				{
					CommandModule mod = commandList.get(commandString);
					if (player.hasPermission(mod.permission)) 
					{
						list.add(commandString);
					}
				}
				Collections.sort(list);
				return list;
			}
		}
		return null;
	}
}
