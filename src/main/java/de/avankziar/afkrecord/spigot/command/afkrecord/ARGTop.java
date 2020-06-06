package main.java.de.avankziar.afkrecord.spigot.command.afkrecord;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGTop extends CommandModule
{
	private AfkRecord plugin;
	
	public ARGTop(AfkRecord plugin)
	{
		super("top","afkrecord.cmd.afkrecord.top.alltime",AfkRecord.afkrarguments,2,3);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String language = plugin.getUtility().getLanguage();
		if(args[1].equalsIgnoreCase("onlinetime") || args[1].equalsIgnoreCase("spielzeit"))
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.top.onlinetime"))
			{
				player.spigot().sendMessage(plugin.getUtility().tctl(
						plugin.getYamlHandler().getL().getString(language+".NoPermission")));
				return;
			}
			int page = 0;
			if(args.length==2)
			{
				plugin.getCommandHelper().top(player, plugin.getBackgroundTask().ac, args,
						page, ".CmdAfkRecord.Top.HeadlineAcT", "onlinetime");
			} else if(args.length==3)
			{
				if(!args[2].matches("[0-9]+"))
				{
					player.spigot().sendMessage(plugin.getUtility().tctl(
							plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
					return;
				}
				page = Integer.parseInt(args[2]);
				plugin.getCommandHelper().top(player, plugin.getBackgroundTask().ac, args,
						page, ".CmdAfkRecord.Top.HeadlineAcT", "onlinetime");
			}
			return;
		} else if(args[1].equalsIgnoreCase("alltime") || args[1].equalsIgnoreCase("gesamtzeit"))
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.top.alltime"))
			{
				player.spigot().sendMessage(plugin.getUtility().tctl(
						plugin.getYamlHandler().getL().getString(language+".NoPermission")));
				return;
			}
			int page = 0;
			if(args.length==2)
			{
				plugin.getCommandHelper().top(player, plugin.getBackgroundTask().all, args,
						page, ".CmdAfkRecord.Top.HeadlineAlT", "alltime");
			} else if(args.length==3)
			{
				if(!args[2].matches("[0-9]+"))
				{
					player.spigot().sendMessage(plugin.getUtility().tctl(
							plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
					return;
				}
				page = Integer.parseInt(args[2]);
				plugin.getCommandHelper().top(player, plugin.getBackgroundTask().all, args,
						page, ".CmdAfkRecord.Top.HeadlineAlT", "alltime");
			}
			return;
		} else if(args[1].equalsIgnoreCase("afktime") || args[1].equalsIgnoreCase("afkzeit"))
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.top.afktime"))
			{
				player.spigot().sendMessage(plugin.getUtility().tctl(
						plugin.getYamlHandler().getL().getString(language+".NoPermission")));
				return;
			}
			int page = 0;
			if(args.length==2)
			{
				plugin.getCommandHelper().top(player, plugin.getBackgroundTask().afk, args,
						page, ".CmdAfkRecord.Top.HeadlineAfT", "afktime");
			} else if(args.length==3)
			{
				if(!args[2].matches("[0-9]+"))
				{
					player.spigot().sendMessage(plugin.getUtility().tctl(
							plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
					return;
				}
				page = Integer.parseInt(args[2]);
				plugin.getCommandHelper().top(player, plugin.getBackgroundTask().afk, args,
						page, ".CmdAfkRecord.Top.HeadlineAfT", "afktime");
			}
			return;
		} else
		{
			TextComponent msg = plugin.getUtility().tc(
					plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".InputIsWrong")));
			msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr"));
			player.spigot().sendMessage(msg);
			return;
		}
	}
}
