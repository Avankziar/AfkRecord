package main.java.de.avankziar.afkrecord.spigot.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.CommandConstructor;
import main.java.de.avankziar.afkrecord.spigot.object.PluginSettings;

public class TABCompletion implements TabCompleter
{	
	private AfkRecord plugin;
	
	public TABCompletion(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	private void debug(Player player, String s)
	{
		PluginSettings.debug(player, s);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			 String lable, String[] args)
	{
		if(!(sender instanceof Player))
		{
			return null;
		}
		Player player = (Player) sender;
		CommandConstructor cc = plugin.getCommandFromPath(cmd.getName());
		if(cc == null)
		{
			cc = plugin.getCommandFromCommandString(cmd.getName());
		}
		if(cc == null)
		{
			return null;
		}
		int length = args.length-1;
		ArrayList<ArgumentConstructor> aclist = cc.subcommands;
		ArrayList<String> OneArgumentBeforeList = new ArrayList<>();
		ArgumentConstructor lastAc = null;
		for(ArgumentConstructor ac : aclist)
		{
			OneArgumentBeforeList.add(ac.getName());
		}
		boolean isBreak = false;
		for(int i = 0; i <= length; i++)
		{
			isBreak = false;
			for(int j = 0; j <= aclist.size()-1; j++)
			{
				ArgumentConstructor ac = aclist.get(j);				
				/*
				 * Wenn das aktuelle Argument leer ist, so loop durch die aclist.
				 */
				if(args[i].isEmpty())
				{
					debug(player, "args[i].isEmpty | i: "+i+" | j: "+j);
					return listIfArgumentIsEmpty(aclist, player);
				} else
				/*
				 * Wenn das aktuelle Argument NICHT leer ist, so loop durch die aclist und checke ob das Argument mit "xx" anfängt.
				 */
				{
					int c = countHowMuchAreStartsWithIgnoreCase(aclist, args[i]);
					debug(player, "!args[i].isEmpty | c: "+c+" | i: "+i+" | j: "+j);
					if(c > 1)
					{
						/*
						 * Wenn mehr als 1 Argument mit dem Chateintrag startet, so liefere eine Liste mit allen diesen zurück.
						 */
						return listIfArgumentIsnotEmpty(aclist, args[i], player);
					}
					if(ac.getName().toLowerCase().startsWith(args[i].toLowerCase()))
					{
						if(ac.getName().length() > args[i].length())
						{
							/*
							 * Wenn das Argument noch nicht vollständig ausgeschrieben ist, so return das.
							 */
							ArrayList<String> list = new ArrayList<>();
							list.add(ac.getName());
							return list;
						}
						/*
						 * Das Argument startet mit dem Argumentenname. aclist mit den Subargumenten vom Argument setzten.
						 * Sowie den innern Loop brechen.
						 */
						aclist = ac.subargument;
						isBreak = true;
						lastAc = ac;
						break;
					}
					if(j == aclist.size()-1)
					{
						/*
						 * Wenn keins der Argumente an der spezifischen Position gepasst hat, abbrechen. Und leere aclist setzten.
						 */
						aclist = new ArrayList<>();
					}
				}
			}
			if(!isBreak)
			{
				debug(player, "isBreak");
				if(lastAc != null)
				{
					return getReturnTabList(lastAc.tabList.get(length), args[length]);
					//Return null, wenn die Tabliste nicht existiert! Aka ein halbes break;
				}
				if(i == length || aclist.isEmpty()) //Wenn das ende erreicht ist oder die aclist vorher leer gesetzt worden ist
				{
					break;
				}
			}
		}
		return null;
	}
	
	private List<String> getReturnTabList(ArrayList<String> tabList, String argsi)
	{
		ArrayList<String> list = new ArrayList<>();
		if(tabList != null && argsi != null)
		{
			for(String s : tabList)
			{
				if(s.startsWith(argsi))
				{
					list.add(s);
				}
			}
		}
		Collections.sort(list);
		return list;
	}
	
	private List<String> listIfArgumentIsEmpty(ArrayList<ArgumentConstructor> subarg, Player player)
	{
		List<String> returnlist = new ArrayList<String>();
		for(ArgumentConstructor ac : subarg)
		{
			if(ac != null)
			{
				if(player.hasPermission(ac.getPermission()))
				{
					returnlist.add(ac.getName());
				}
			}
		}
		return returnlist;
	}
	
	private List<String> listIfArgumentIsnotEmpty(ArrayList<ArgumentConstructor> subarg, String arg, Player player)
	{
		List<String> returnlist = new ArrayList<String>();
		for(ArgumentConstructor ac : subarg)
		{
			if(ac != null)
			{
				if(ac.getName().toLowerCase().startsWith(arg.toLowerCase()))
				{
					if(player.hasPermission(ac.getPermission()))
					{
						returnlist.add(ac.getName());
					}
				}
			}
		}
		return returnlist;
	}
	
	private int countHowMuchAreStartsWithIgnoreCase(ArrayList<ArgumentConstructor> subarg, String arg)
	{
		int i = 0;
		for(ArgumentConstructor ac : subarg)
		{
			if(ac.getName().toLowerCase().startsWith(arg.toLowerCase()))
			{
				i++;
			}
		}
		return i;
	}
	
	public String[] AddToStringArray(String[] oldArray, String newString)
	{
	    String[] newArray = Arrays.copyOf(oldArray, oldArray.length+1);
	    newArray[oldArray.length] = newString;
	    return newArray;
	}
}