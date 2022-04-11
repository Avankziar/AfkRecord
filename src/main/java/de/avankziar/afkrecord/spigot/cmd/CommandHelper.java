package main.java.de.avankziar.afkrecord.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.assistance.Utility;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.ConvertHandler;
import main.java.de.avankziar.afkrecord.spigot.object.PluginSettings;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;
import main.java.de.avankziar.afkrecord.spigot.permission.KeyHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHelper
{
	private AfkRecord plugin;

	public CommandHelper(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void time(Player player, OfflinePlayer target)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", target.getUniqueId().toString());
		int acplace = plugin.getMysqlHandler().getTopListPlaceI(plugin, "`activitytime`", "`activitytime` > ?", user.getActivityTime());
		int afkplace = plugin.getMysqlHandler().getTopListPlaceI(plugin, "`afktime`", "`afktime` > ?", user.getAfkTime());
		int allplace = plugin.getMysqlHandler().getTopListPlaceI(plugin, "`alltime`", "`alltime` > ?", user.getAllTime());
		String format = "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") +
						"&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") +
						"&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") +
						"&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds");
		String afktime = String.valueOf((user != null) ? TimeHandler.getRepeatingTime(user.getAfkTime(), format) : 0);
		String ontime = String.valueOf((user != null) ? TimeHandler.getRepeatingTime(user.getActivityTime(), format) : 0);
		String alltime = String.valueOf((user != null) ? TimeHandler.getRepeatingTime(user.getAllTime(), format) : 0);
		String lastactivity = String.valueOf((user != null) ? TimeHandler.getDateTime(user.getLastActivity()) : 0);
		
		player.spigot().sendMessage(ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString(
						"CmdAfkRecord.Time.Headline").replaceAll("%player%", target.getName())));
		TextComponent msg1 = ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString(
						"CmdAfkRecord.Time.ActiveTime")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.ActiveTime").contains("%ontime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAcT"))).create()));
		} else if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.ActiveTime").contains("%afktime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAfT"))).create()));
		} else if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.ActiveTime").contains("%alltime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAlT"))).create()));
		}
		TextComponent msg2 = ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString(
						"CmdAfkRecord.Time.AfkTime")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.AfkTime").contains("%ontime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAcT"))).create()));
		} else if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.AfkTime").contains("%afktime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAfT"))).create()));
		} else if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.AfkTime").contains("%alltime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAlT"))).create()));
		}
		TextComponent msg3 = ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString(
						"CmdAfkRecord.Time.Alltime")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.Alltime").contains("%ontime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAcT"))).create()));
		} else if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.Alltime").contains("%afktime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAfT"))).create()));
		} else if(plugin.getYamlHandler().getLang().getString(
				"CmdAfkRecord.Time.Alltime").contains("%alltime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(ChatApi.tl
					(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Time.HoverAlT"))).create()));
		}
		player.spigot().sendMessage(msg1);
		player.spigot().sendMessage(msg2);
		player.spigot().sendMessage(msg3);
		if(player.hasPermission(Utility.PERMTIMELASTACTIVITY))
		{
			player.spigot().sendMessage(ChatApi.tctl(
					plugin.getYamlHandler().getLang().getString(
							"CmdAfkRecord.Time.LastActive").replaceAll("%time%", lastactivity)));
		}
	}
	
	public void top(Player player, String orderByColumn, int page,
			String headpath, String subcmd) throws IOException
	{
		int start = 0;
		int quantity = 9;
		start = page*9;
		int lastEntry = plugin.getMysqlHandler().lastID(Type.PLUGINUSER);
		boolean lastpage = false;
		//end = (int) Math.ceil(size/10);
		if(lastEntry <= (start+quantity))
		{
			start = lastEntry-9;
			lastpage = true;
			if(start < 0)
			{
				start = 0;
			}
		}
		ArrayList<PluginUser> arr = ConvertHandler.convertListI(
				plugin.getMysqlHandler().getTop(Type.PLUGINUSER, "`"+orderByColumn+"`", true, start, quantity));
		if(arr == null || arr.isEmpty())
		{
			player.sendMessage(ChatApi.tl("Arr ist null oder Empty"));
		}
		player.spigot().sendMessage(ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString(headpath)));
		int a = 0;
		plugin.getLogger().info("10"); //TODO
		while(a < arr.size())
		{
			plugin.getLogger().info("11 : "+a); //TODO
			PluginUser user = arr.get(a);
			int place = start+1;
			long time = 0;
			switch(orderByColumn)
			{
			case "afktime":
				time = user.getAfkTime();
				break;
			case "activitytime":
				time = user.getActivityTime();
				break;
			case "alltime":
				time = user.getAllTime();
				break;
			default:
				break;
			}
			if(user != null)
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Top.PlaceAndTime")
						.replace("%place%", plugin.getUtility().getPlaceColor(place))
						.replace("%player%", user.getPlayerName())
						.replace("%time%", plugin.getUtility().timetl(time))));
			}
			a++;
			start++;
		}
		
		int i = page+1;
		int j = page-1;
		TextComponent MSG = ChatApi.tctl("");
		List<BaseComponent> list = new ArrayList<BaseComponent>();
		if(page!=0)
		{
			TextComponent msg2 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.BaseInfo.Past")));
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, subcmd+" "+j));
			list.add(msg2);
		}
		if(!lastpage)
		{
			TextComponent msg1 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.BaseInfo.Next")));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, subcmd+" "+i));
			if(list.size()==1)
			{
				list.add(ChatApi.tc(" | "));
			}
			list.add(msg1);
		}
		if(!list.isEmpty())
		{
			MSG.setExtra(list);
			player.spigot().sendMessage(MSG);
		}
	}
	
	public void gettime(Player player, OfflinePlayer target, int page, String subcmd) throws IOException
	{
		int start = page*10;
		int quantity = 9;
		boolean lastpage = false;
		int lastid = plugin.getMysqlHandler().lastID(Type.TIMERECORD);
		if((start+quantity) > lastid)
		{
			start = lastid-quantity;
			lastpage = true;
		}
		ArrayList<TimeRecord> a = ConvertHandler.convertListII(
				plugin.getMysqlHandler().getList(Type.TIMERECORD,
						"timestamp_unix", true, start, quantity, "`player_uuid` = ?", target.getUniqueId().toString()));
		player.spigot().sendMessage(ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.GetTime.Headline")
				.replaceAll("%player%", target.getName())));
		for(TimeRecord tr : a)
		{
			player.spigot().sendMessage(ChatApi.tctl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.GetTime.Line")
					.replaceAll("%date%", TimeHandler.getDate(tr.getTimeStamp()))
					.replaceAll("%alltime%", plugin.getUtility().timetl(tr.getAllTime()))
					.replaceAll("%ontime%", plugin.getUtility().timetl(tr.getActivityTime()))
					.replaceAll("%afktime%", plugin.getUtility().timetl(tr.getAfkTime()))));
			start++;
		}
		int i = page+1;
		int j = page-1;
		TextComponent MSG = ChatApi.tctl("");
		List<BaseComponent> list = new ArrayList<BaseComponent>();
		if(page!=0)
		{
			TextComponent msg2 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.BaseInfo.Past")));
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, subcmd+" "+j+" "+target.getName()));
			list.add(msg2);
		}
		if(!lastpage)
		{
			TextComponent msg1 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.BaseInfo.Next")));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, subcmd+" "+i+" "+target.getName()));
			if(list.size()==1)
			{
				list.add(ChatApi.tc(" | "));
			}
			list.add(msg1);
		}
		if(!list.isEmpty())
		{
			MSG.setExtra(list);	
			player.spigot().sendMessage(MSG);
		}
	}
	
	public void counttime(Player player, OfflinePlayer target, int days) throws IOException
	{
		long before = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()))
				- (days-1)*1000L*60*60*24;
		long act = plugin.getMysqlHandler().getSumII(plugin, "`player_uuid`", "`activitytime`",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", target.getUniqueId().toString(), before);
		long afkt = plugin.getMysqlHandler().getSumII(plugin, "`player_uuid`", "`afktime`",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", target.getUniqueId().toString(), before);
		long allt = plugin.getMysqlHandler().getSumII(plugin, "`player_uuid`", "`alltime`",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", target.getUniqueId().toString(), before);
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.Headline")
				.replaceAll("%player%", target.getName())
				.replaceAll("%days%", String.valueOf(days))));
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.ActiveTime")
				.replaceAll("%ontime%", plugin.getUtility().timetl(act))
				.replaceAll("%afktime%", plugin.getUtility().timetl(afkt))
				.replaceAll("%alltime%", plugin.getUtility().timetl(allt))));
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.AfkTime")
				.replaceAll("%ontime%", plugin.getUtility().timetl(act))
				.replaceAll("%afktime%", plugin.getUtility().timetl(afkt))
				.replaceAll("%alltime%", plugin.getUtility().timetl(allt))));
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.Alltime")
				.replaceAll("%ontime%", plugin.getUtility().timetl(act))
				.replaceAll("%afktime%", plugin.getUtility().timetl(afkt))
				.replaceAll("%alltime%", plugin.getUtility().timetl(allt))));
		if(player.hasPermission(Utility.PERMCOUNTTIMELASTACTIVITY))
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
					"`player_uuid` = ?", target.getUniqueId().toString());
			
			player.sendMessage(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.LastActive")
					.replaceAll("%time%", TimeHandler.getDateTime((user != null) ? user.getLastActivity() : 0))));
		}
	}
	
	public void getafk(Player player, String bungeeplayerlist) throws IOException
	{
		ArrayList<PluginUser> users = ConvertHandler.convertListI(
				plugin.getMysqlHandler().getAllListAt(Type.PLUGINUSER, "`id`", false, "`isafk` = ? AND `isonline` = ?", true, true));
		boolean check = false;
		TextComponent playerlist = ChatApi.tc("");
		long now = System.currentTimeMillis();
		TextComponent MSG = ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.GetAfk.PlayerAfk"));
		List<BaseComponent> list = new ArrayList<>();
		for(PluginUser user : users)
		{
			long t = now-user.getLastActivity();
			long time = t/(1000*60);
			if(time<15)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.Under15Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=15 && time<30)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.15Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=30 && time<45)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.30Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=45 && time<60)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.45Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND, 
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=60 && time<90)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.60Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=90 && time<120)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.90Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=120)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.120Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getUtility().timetl(t)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND, 
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			}
			list.add(playerlist);
			if(check==false)
			{
				check = true;
			}
		}
		if(check==false)
		{
			player.sendMessage(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.GetAfk.NoPlayerAfk")));
			return;
		}
		MSG.setExtra(list);
		player.spigot().sendMessage(MSG);
	}
}
