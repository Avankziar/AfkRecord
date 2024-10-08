package main.java.me.avankziar.afkr.spigot.cmd;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.general.objects.TimeRecord;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.assistance.TimeHandler;
import main.java.me.avankziar.afkr.spigot.assistance.Utility;
import main.java.me.avankziar.afkr.spigot.handler.PlayerTimesHandler;
import main.java.me.avankziar.afkr.spigot.object.ConvertHandler;
import main.java.me.avankziar.afkr.spigot.object.PluginSettings;
import main.java.me.avankziar.afkr.spigot.permission.KeyHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHelper
{
	private AfkR plugin;

	public CommandHelper(AfkR plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void time(Player player, OfflinePlayer target)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER, "`player_uuid` = ?", target.getUniqueId().toString());
		long act = (PlayerTimesHandler.activeTime.containsKey(target.getUniqueId()) ? PlayerTimesHandler.activeTime.get(target.getUniqueId()) : 0);
		long afk = (PlayerTimesHandler.afkTime.containsKey(target.getUniqueId()) ? PlayerTimesHandler.afkTime.get(target.getUniqueId()) : 0);
		int acplace = plugin.getMysqlHandler().getTopListPlaceI("`activitytime`", "`activitytime` > ?", user.getActiveTime()+act);
		int afkplace = plugin.getMysqlHandler().getTopListPlaceI("`afktime`", "`afktime` > ?", user.getAfkTime()+afk);
		int allplace = plugin.getMysqlHandler().getTopListPlaceI("`alltime`", "`alltime` > ?", user.getTotalTime()+act+afk);
		boolean b = true;
		String afktime = (user != null) ? plugin.getPlayerTimes().formatTimePeriod(user.getAfkTime()+afk, false, b, b, b, b) : "0";
				//TimeHandler.getRepeatingTime(user.getAfkTime(), format) : 0);
		String ontime = (user != null) ? plugin.getPlayerTimes().formatTimePeriod(user.getActiveTime()+act, false, b, b, b, b) : "0";
				//TimeHandler.getRepeatingTime(user.getActiveTime(), format) : "0";
		String alltime = (user != null) ? plugin.getPlayerTimes().formatTimePeriod(user.getTotalTime()+act+afk, false, b, b, b, b) : "0";
				//TimeHandler.getRepeatingTime(user.getTotalTime(), format) : 0);
		String lastactivity = String.valueOf((user != null) ? plugin.getPlayerTimes().formatDate(user.getLastActivity()) : 0);
		
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
			String headpath, String subcmd)
	{
		int start = 0;
		int quantity = 9;
		start = page*9;
		int lastEntry = plugin.getMysqlHandler().lastID(MysqlType.PLUGINUSER);
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
				plugin.getMysqlHandler().getTop("`"+orderByColumn+"`", true, start, quantity));
		if(arr == null || arr.isEmpty())
		{
			player.sendMessage(ChatApi.tl("Arr ist null oder Empty"));
		}
		player.spigot().sendMessage(ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString(headpath)));
		int a = 0;
		while(a < arr.size())
		{
			PluginUser user = arr.get(a);
			int place = start+1;
			long time = 0;
			switch(orderByColumn)
			{
			case "afktime":
				time = user.getAfkTime();
				break;
			case "activitytime":
				time = user.getActiveTime();
				break;
			case "alltime":
				time = user.getTotalTime();
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
						.replace("%time%", plugin.getPlayerTimes().formatTimePeriod(time, true, true))));
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
	
	public void topLastXDays(Player player, String orderByColumn, int page,
			String headpath, String subcmd, int days)
	{
		long now = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long beforeXDays = now - (1000L*60*60*24*days);
		ArrayList<TimeRecord> timerec = ConvertHandler.convertListII(plugin.getMysqlHandler().getFullList(MysqlType.TIMERECORD,
				"`id` ASC", "`timestamp_unix` > ?", beforeXDays));
		if(timerec == null || timerec.isEmpty())
		{
			player.sendMessage(ChatApi.tl("Arr ist null oder Empty"));
		}
		LinkedHashMap<UUID, Long> map = new LinkedHashMap<>();
		for(TimeRecord tr : timerec)
		{
			long l = 0;
			if(map.containsKey(tr.getUUID()))
			{
				l = map.get(tr.getUUID());
			}
			switch(orderByColumn)
			{
			case "alltime": l += tr.getTotalTime(); break;
			case "activitytime": l += tr.getActiveTime(); break;
			case "afktime": l += tr.getActiveTime(); break;
			}
			map.put(tr.getUUID(), l);
		}
		LinkedHashMap<UUID, Long> sorted = map.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Long::compareTo))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(a, b) -> a, LinkedHashMap::new));
		int start = page*10;
		int quantity = 9;
		int lastEntry = sorted.size();
		int lastpage = lastEntry/quantity;
		ArrayList<String> msg = new ArrayList<>();
		msg.add(plugin.getYamlHandler().getLang().getString(headpath)
				.replace("%days%", String.valueOf(days)));
		int a = 0;
		for(Entry<UUID, Long> e : sorted.entrySet())
		{
			if(a < start || a >= start+quantity)
			{
				continue;
			}
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER, "`player_uuid` = ?", e.getKey().toString());
			int place = start+1;
			long time = e.getValue();
			if(user != null)
			{
				msg.add(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Top.PlaceAndTime")
						.replace("%place%", plugin.getUtility().getPlaceColor(place))
						.replace("%player%", user.getPlayerName())
						.replace("%time%", plugin.getPlayerTimes().formatTimePeriod(time, true, true)));
			}
			a++;
		}
		int i = page+1;
		int j = page == 0 ? lastpage : page-1;
		TextComponent MSG = ChatApi.tctl("");
		List<BaseComponent> list = new ArrayList<BaseComponent>();
		if(page != 0)
		{
			TextComponent msg2 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.BaseInfo.Past")));
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, subcmd+" "+j+" "+days));
			list.add(msg2);
		}
		if(page != 0 && lastpage > 0)
		{
			TextComponent msg1 = ChatApi.tc(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.BaseInfo.Next")));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, subcmd+" "+i+" "+days));
			if(list.size()==1)
			{
				list.add(ChatApi.tc(" | "));
			}
			list.add(msg1);
		}
		msg.stream().forEach(x -> player.spigot().sendMessage(ChatApi.tctl(x)));
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
		int lastid = plugin.getMysqlHandler().lastID(MysqlType.TIMERECORD);
		if((start+quantity) > lastid)
		{
			start = lastid-quantity;
			lastpage = true;
		}
		
		ArrayList<TimeRecord> a = TimeRecord.convert(plugin.getMysqlHandler().getList(MysqlType.TIMERECORD, "`timestamp_unix` DESC", start, quantity,
				"`player_uuid` = ?", target.getUniqueId().toString()));
		player.spigot().sendMessage(ChatApi.tctl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.GetTime.Headline")
				.replaceAll("%player%", target.getName())));
		for(TimeRecord tr : a)
		{
			player.spigot().sendMessage(ChatApi.tctl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.GetTime.Line")
					.replaceAll("%date%", TimeHandler.getDate(tr.getTimeStamp()))
					.replaceAll("%alltime%", plugin.getPlayerTimes().formatTimePeriod(tr.getTotalTime(), true, true))
					.replaceAll("%ontime%", plugin.getPlayerTimes().formatTimePeriod(tr.getActiveTime(), true, true))
					.replaceAll("%afktime%", plugin.getPlayerTimes().formatTimePeriod(tr.getAfkTime(), true, true))));
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
		long act = plugin.getMysqlHandler().getSumII("`player_uuid`", "`activitytime`",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", target.getUniqueId().toString(), before);
		long afkt = plugin.getMysqlHandler().getSumII("`player_uuid`", "`afktime`",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", target.getUniqueId().toString(), before);
		long allt = plugin.getMysqlHandler().getSumII("`player_uuid`", "`alltime`",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", target.getUniqueId().toString(), before);
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.Headline")
				.replaceAll("%player%", target.getName())
				.replaceAll("%days%", String.valueOf(days))));
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.ActiveTime")
				.replaceAll("%ontime%", plugin.getPlayerTimes().formatTimePeriod(act, true, true))
				.replaceAll("%afktime%", plugin.getPlayerTimes().formatTimePeriod(afkt, true, true))
				.replaceAll("%alltime%", plugin.getPlayerTimes().formatTimePeriod(allt, true, true))));
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.AfkTime")
				.replaceAll("%ontime%", plugin.getPlayerTimes().formatTimePeriod(act, true, true))
				.replaceAll("%afktime%", plugin.getPlayerTimes().formatTimePeriod(afkt, true, true))
				.replaceAll("%alltime%", plugin.getPlayerTimes().formatTimePeriod(allt, true, true))));
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.Alltime")
				.replaceAll("%ontime%", plugin.getPlayerTimes().formatTimePeriod(act, true, true))
				.replaceAll("%afktime%", plugin.getPlayerTimes().formatTimePeriod(afkt, true, true))
				.replaceAll("%alltime%", plugin.getPlayerTimes().formatTimePeriod(allt, true, true))));
		if(player.hasPermission(Utility.PERMCOUNTTIMELASTACTIVITY))
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER,
					"`player_uuid` = ?", target.getUniqueId().toString());
			
			player.sendMessage(ChatApi.tl(
					plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTime.LastActive")
					.replaceAll("%time%", plugin.getPlayerTimes().formatDate((user != null) ? user.getLastActivity() : 0))));
		}
	}
	
	public void getafk(Player player) throws IOException
	{
		ArrayList<PluginUser> users = PluginUser.convert(plugin.getMysqlHandler(
				).getFullList(MysqlType.PLUGINUSER, "`id` ASC", "`isafk` = ? AND `isonline` = ?", true, true));
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
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=15 && time<30)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.15Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=30 && time<45)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.30Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=45 && time<60)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.45Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND, 
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=60 && time<90)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.60Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else if(time>=90 && time<120)
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.90Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
				playerlist.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND,
						PluginSettings.settings.getCommands(KeyHandler.TIME)+user.getPlayerName()));
			} else
			{
				String pc = plugin.getYamlHandler().getLang()
						.getString("CmdAfkRecord.GetAfk.PlayerColor.120Min");
				playerlist = ChatApi.tctl(pc+user.getPlayerName()
						+"&f|"+plugin.getPlayerTimes().formatTimePeriod(t, false, false)+" ");
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
