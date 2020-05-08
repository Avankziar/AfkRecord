package main.java.de.avankziar.afkrecord.spigot.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.Utility;
import main.java.de.avankziar.afkrecord.spigot.object.PlayerInfo;
import main.java.de.avankziar.afkrecord.spigot.object.TopList;
import main.java.de.avankziar.afkrecord.spigot.object.User;
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
	
	public void afkr(Player player)
	{
		String language = plugin.getUtility().getLanguage();
		TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg02")));
		msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord time"));
		TextComponent msg2 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg03")));
		msg2.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top "));
		TextComponent msg3 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg04")));
		msg3.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord gettime"));
		TextComponent msg5 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg06")));
		msg5.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afk"));
		TextComponent msg6 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg07")));
		msg6.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord counttime "));
		TextComponent msg7 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg08")));
		msg7.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord counttime "));
		player.spigot().sendMessage(plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Info.msg01")));
		player.spigot().sendMessage(msg1);
		player.spigot().sendMessage(msg2);
		player.spigot().sendMessage(msg3);
		player.spigot().sendMessage(msg6);
		player.spigot().sendMessage(msg7);
		player.spigot().sendMessage(msg5);
	}
	
	public void time(Player player, OfflinePlayer target, User u)
	{
		String language = plugin.getUtility().getLanguage();
		int acplace = plugin.getUtility().getPlace(target, plugin.getBackgroundTask().ac);
		int afkplace = plugin.getUtility().getPlace(target, plugin.getBackgroundTask().afk);
		int allplace = plugin.getUtility().getPlace(target, plugin.getBackgroundTask().all);
		String afktime = "";
		String ontime = "";
		String alltime = "";
		String lastactivity = "";
		if(u!=null)
		{
			afktime = plugin.getUtility().timetl(
					u.getAfktime()+
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "afktime", "player_uuid"));
			ontime = plugin.getUtility().timetl(
					u.getAfktime()+
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "activitytime", "player_uuid"));
			alltime = plugin.getUtility().timetl(
					u.getAfktime()+
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "alltime", "player_uuid"));
			lastactivity = plugin.getUtility().getDateExact(u.getLastactivity());
		} else
		{
			afktime = plugin.getUtility().timetl(
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "afktime", "player_uuid"));
			ontime = plugin.getUtility().timetl(
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "activitytime", "player_uuid"));
			alltime = plugin.getUtility().timetl(
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "alltime", "player_uuid"));
			lastactivity = plugin.getUtility().getDateExact(
					(Long) plugin.getMysqlHandler().getDataI(
							target.getUniqueId().toString(), "lastactivity", "player_uuid"));
		}
		player.spigot().sendMessage(plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(
						language+".CmdAfkRecord.Time.Headline").replaceAll("%player%", target.getName())));
		TextComponent msg1 = plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(
						language+".CmdAfkRecord.Time.ActiveTime")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.ActiveTime").contains("%ontime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAcT"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.ActiveTime").contains("%afktime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAfT"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.ActiveTime").contains("%alltime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAlT"))).create()));
		}
		TextComponent msg2 = plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(
						language+".CmdAfkRecord.Time.AfkTime")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.AfkTime").contains("%ontime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAcT"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.AfkTime").contains("%afktime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAfT"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.AfkTime").contains("%alltime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAlT"))).create()));
		}
		TextComponent msg3 = plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(
						language+".CmdAfkRecord.Time.Alltime")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.Alltime").contains("%ontime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAcT"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.Alltime").contains("%afktime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAfT"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CmdAfkRecord.Time.Alltime").contains("%alltime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Time.HoverAlT"))).create()));
		}
		player.spigot().sendMessage(msg1);
		player.spigot().sendMessage(msg2);
		player.spigot().sendMessage(msg3);
		if(player.hasPermission(Utility.PERMTIMELASTACTIVITY))
		{
			player.spigot().sendMessage(plugin.getUtility().tctl(
					plugin.getYamlHandler().getL().getString(
							language+".CmdAfkRecord.Time.LastActive").replaceAll("%time%", lastactivity)));
		}
	}
	
	public void top(Player player, ArrayList<TopList> ar, String[] args, int page,
			String headpath, String subcmd)
	{
		String language = plugin.getUtility().getLanguage();
		int size = ar.size()-1;
		int start = 0;
		int stop = 0;
		start = page*10;
		stop = page*10+9;
		boolean lastpage = false;
		//end = (int) Math.ceil(size/10);
		if(stop>size)
		{
			stop = size;
			start = size-9;
			lastpage = true;
			if(start<0)
			{
				start = 0;
			}
		}
		player.spigot().sendMessage(plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(language+headpath)));
		while(start<=stop)
		{
			TopList i;
			try
			{
				i = ar.get(start);
			} catch(IndexOutOfBoundsException e)
			{
				i = null;
			}
			if(i!=null)
			{
				player.spigot().sendMessage(plugin.getUtility().tctl(
						plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Top.PlaceAndTime")
						.replaceAll("%place%", plugin.getUtility().getPlaceColor(i.getPlace()))
						.replaceAll("%player%", i.getName())
						.replaceAll("%time%", plugin.getUtility().timetl(i.getTime()))));
			}
			start++;
		}
		int i = page+1;
		int j = page-1;
		TextComponent MSG = plugin.getUtility().tctl("");
		List<BaseComponent> list = new ArrayList<BaseComponent>();
		if(page!=0)
		{
			TextComponent msg2 = plugin.getUtility().tc(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Top.Past")));
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top "+subcmd+" "+j));
			list.add(msg2);
		}
		if(!lastpage)
		{
			TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Top.Next")));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top "+subcmd+" "+i));
			if(list.size()==1)
			{
				list.add(plugin.getUtility().tc(" | "));
			}
			list.add(msg1);
		}
		MSG.setExtra(list);	
		player.spigot().sendMessage(MSG);
	}
	
	public void gettime(Player player, OfflinePlayer target, int page)
	{
		String language = plugin.getUtility().getLanguage();
		int start = page*10;
		int stop = page*10+10;
		ArrayList<PlayerInfo> a = plugin.getMysqlHandler().getListII(target.getUniqueId().toString());
		int size = a.size()-1;
		if(stop>size)
		{
			stop = size;
			start = size-10;
			if(start<0)
			{
				start = 0;
			}
		}
		player.spigot().sendMessage(plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.GetTime.Headline")
				.replaceAll("%player%", target.getName())));
		while(start<stop)
		{
			player.spigot().sendMessage(plugin.getUtility().tctl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.GetTime.Line")
					.replaceAll("%date%", a.get(start).getDate())
					.replaceAll("%alltime%", plugin.getUtility().timetl(a.get(start).getAlltime()))
					.replaceAll("%ontime%", plugin.getUtility().timetl(a.get(start).getActivitytime()))
					.replaceAll("%afktime%", plugin.getUtility().timetl(a.get(start).getAfktime()))));
			start++;
		}
	}
	
	public void counttime(Player player, OfflinePlayer target, int days)
	{
		String language = plugin.getUtility().getLanguage();
		PlayerInfo pi = plugin.getMysqlHandler().getCountTime(target.getUniqueId().toString(), days);
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTime.Headline")
				.replaceAll("%player%", target.getName())
				.replaceAll("%days%", String.valueOf(days))));
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTime.ActiveTime")
				.replaceAll("%ontime%", plugin.getUtility().timetl(pi.getActivitytime()))
				.replaceAll("%afktime%", plugin.getUtility().timetl(pi.getAfktime()))
				.replaceAll("%alltime%", plugin.getUtility().timetl(pi.getAlltime()))));
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTime.AfkTime")
				.replaceAll("%ontime%", plugin.getUtility().timetl(pi.getActivitytime()))
				.replaceAll("%afktime%", plugin.getUtility().timetl(pi.getAfktime()))
				.replaceAll("%alltime%", plugin.getUtility().timetl(pi.getAlltime()))));
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTime.Alltime")
				.replaceAll("%ontime%", plugin.getUtility().timetl(pi.getActivitytime()))
				.replaceAll("%afktime%", plugin.getUtility().timetl(pi.getAfktime()))
				.replaceAll("%alltime%", plugin.getUtility().timetl(pi.getAlltime()))));
		if(player.hasPermission(Utility.PERMCOUNTTIMELASTACTIVITY))
		{
			player.sendMessage(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.CountTime.LastActive")
					.replaceAll("%time%", plugin.getUtility().getDateExact(
							(Long) plugin.getMysqlHandler().getDataI(
							player.getUniqueId().toString(), "lastactivity", "player_uuid")))));
		}
	}
	
	public void getafk(Player player, String bungeeplayerlist)
	{
		String language = plugin.getUtility().getLanguage();
		ArrayList<User> user = plugin.getUtility().sortAfkList(User.getUsers(), bungeeplayerlist);
		boolean check = false;
		TextComponent playerlist = plugin.getUtility().tc("");
		long now = System.currentTimeMillis();
		TextComponent MSG = plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.GetAfk.PlayerAfk"));
		List<BaseComponent> list = new ArrayList<>();
		for(User u : user)
		{
			if(u.isIsafk())
			{
				long t = now-u.getLastactivity();
				long time = t/(1000*60);
				if(time<15)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.Under15Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=15 && time<30)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.15Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=30 && time<45)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.30Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=45 && time<60)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.45Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=60 && time<90)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.60Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=90 && time<120)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.90Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=120)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.120Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				}
				list.add(playerlist);
				if(check==false)
				{
					check = true;
				}
			}
		}
		if(check==false)
		{
			player.sendMessage(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.GetAfk.NoPlayerAfk")));
			return;
		}
		MSG.setExtra(list);
		player.spigot().sendMessage(MSG);
	}
}
