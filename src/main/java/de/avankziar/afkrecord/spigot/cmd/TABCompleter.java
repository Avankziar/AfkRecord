package main.java.de.avankziar.afkrecord.spigot.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TABCompleter implements TabCompleter
{
	private ArrayList<String> firstargs;
	private ArrayList<String> secondtop;
	
	public TABCompleter() 
	{
		init();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(args.length==1)
		{
			return this.firstargs;
		} else if(args.length==2 && args[0].equalsIgnoreCase("top"))
		{
			return this.secondtop;
		}
		return null;
	}
	
	private void init()
	{
		firstargs = new ArrayList<String>();
		firstargs.add("time");
		firstargs.add("top");
		firstargs.add("gettime");
		firstargs.add("counttime");
		firstargs.add("getafk");
		Collections.sort(firstargs, String.CASE_INSENSITIVE_ORDER);
		
		secondtop = new ArrayList<String>();
		secondtop.add("onlinetime");
		secondtop.add("alltime");
		secondtop.add("afktime");
		Collections.sort(secondtop, String.CASE_INSENSITIVE_ORDER);
	}
}
