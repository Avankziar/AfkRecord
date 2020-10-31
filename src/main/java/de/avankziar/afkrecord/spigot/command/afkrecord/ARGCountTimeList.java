package main.java.de.avankziar.afkrecord.spigot.command.afkrecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;
import main.java.de.avankziar.afkrecord.spigot.object.PlayerInfoEx;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGCountTimeList extends CommandModule
{
	private AfkRecord plugin;
	
	public ARGCountTimeList(AfkRecord plugin)
	{
		super("counttimelist","afkrecord.cmd.afkrecord.counttimelist",AfkRecord.afkrarguments,2,7);
		this.plugin = plugin;
	}
	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String language = plugin.getUtility().getLanguage();
		if(AfkRecord.getPerms() == null)
		{
			player.sendMessage(
					ChatApi.tl(plugin.getYamlHandler().getL().getString(language+".NoVault")));
			return;
		}
		int days = 1;
		String permission = "null";
		String mini = "1";
		long min = 1; //minimum
		String time = "m";
		String tValue = plugin.getYamlHandler().getL().getString(language+".Time.Minutes");
		String column = "alltime";
		int page = 0;
		if(!args[1].matches("[0-9]+"))
		{
			player.spigot().sendMessage(
					ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
			return;
		}
		days = Integer.parseInt(args[1]);
		if(args.length >= 3)
		{
			permission = args[2];
		}
		if(args.length >= 5 && args[3].matches("[0-9]+"))
		{
			mini = args[3];
			if(args[4].equals("s"))
			{
				min = Integer.parseInt(args[3])*1000;
				tValue = plugin.getYamlHandler().getL().getString(language+".Time.Seconds");
			} else if(args[4].equals("m"))
			{
				min = Integer.parseInt(args[3])*1000*60;
				tValue = plugin.getYamlHandler().getL().getString(language+".Time.Minutes");
			} else if(args[4].equals("h"))
			{
				min = Integer.parseInt(args[3])*1000*60*60;
				tValue = plugin.getYamlHandler().getL().getString(language+".Time.Hours");
			} else if(args[4].equals("d"))
			{
				min = Integer.parseInt(args[3])*1000*60*60*24;
				tValue = plugin.getYamlHandler().getL().getString(language+".Time.Days");
			}
			min = Integer.parseInt(args[3]);
		}
		if(args.length >= 6)
		{
			if(args[5].equalsIgnoreCase("all"))
			{
				column = "alltime";
			} else if(args[5].equalsIgnoreCase("afk"))
			{
				column = "afktime";
			} else if(args[5].equalsIgnoreCase("on"))
			{
				column = "activitytime";
			}
		}
		if(args.length >= 7)
		{
			if(!args[6].matches("[0-9]+"))
			{
				player.spigot().sendMessage(
						ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
				return;
			}
			page = Integer.parseInt(args[6]);
		}
		
		ArrayList<PlayerInfoEx> playerlists = plugin.getMysqlHandler().getCountTimeList(days, min, column);
		ArrayList<PlayerInfoEx> playerlist = new ArrayList<>();
		if(!permission.equals("null"))
		{
			for(PlayerInfoEx piex : playerlists)
			{
				OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(piex.getUuid()));
				if(AfkRecord.getPerms().playerHas(null, target, permission))
				{
					playerlist.add(piex);
				}
			}
		} else
		{
			playerlist = playerlists;
		}
		int start = page*25;
		int quantity = 25;
		int end = start+quantity;
		int last = playerlist.size()-1;
		boolean lastpage = false;
		if(end > last)
		{
			lastpage = true;
			end = last;
		}
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTimeList.Headline")
				.replace("%perm%", permission)
				.replace("%days%", String.valueOf(days))
				.replace("%time%", mini+tValue)));
		int c = 1;
		List<BaseComponent> bclist = new ArrayList<>();
		while(start <= last)
		{
			PlayerInfoEx piex = playerlist.get(start);
			bclist.add(ChatApi.hoverEvent(
					piex.getPlayername(),
					HoverEvent.Action.SHOW_TEXT,
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTimeList.Hover")
					.replace("%alltime%", plugin.getUtility().timetl(piex.getAlltime()))
					.replace("%ontime%", plugin.getUtility().timetl(piex.getActivitytime()))
					.replace("%afktime%", plugin.getUtility().timetl(piex.getAfktime()))
					.replace("%time%", piex.getDate())));
			c++;
			if(c == quantity)
			{
				break;
			}
			start++;
		}
		
		double pa = Math.floor(start/25);
		page = (int) pa;
		int i = page+1;
		int j = page-1;
		TextComponent MSG = ChatApi.tctl("");
		List<BaseComponent> list = new ArrayList<BaseComponent>();
		if(page!=0)
		{
			TextComponent msg2 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Top.Past")));
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/afkr counttimelist "+days+" "+permission+" "+min+" "+time+" "+column+" "+j));
			list.add(msg2);
		}
		if(!lastpage)
		{
			TextComponent msg1 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Top.Next")));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/afkr counttimelist "+days+" "+permission+" "+min+" "+time+" "+column+" "+i));
			if(list.size()==1)
			{
				list.add(ChatApi.tc(" | "));
			}
			list.add(msg1);
		}
		MSG.setExtra(list);	
		player.spigot().sendMessage(MSG);
	}
}