package main.java.de.avankziar.afkrecord.spigot.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.interfaces.PlayerInfo;
import main.java.de.avankziar.afkrecord.spigot.interfaces.TopList;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHandler
{
	private AfkRecord plugin;

	public CommandHandler(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	public void info(Player player, String language)
	{
		TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg02")));
		msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord time"));
		TextComponent msg2 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg03")));
		msg2.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top "));
		TextComponent msg3 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg04")));
		msg3.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord gettime"));
		TextComponent msg4 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg05")));
		msg4.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord convertolddata "));
		TextComponent msg5 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg06")));
		msg5.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afk"));
		TextComponent msg6 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg07")));
		msg6.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord counttime "));
		TextComponent msg7 = plugin.getUtility().tc(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg08")));
		msg7.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord counttime "));
		player.spigot().sendMessage(plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.info.msg01")));
		player.spigot().sendMessage(msg1);
		player.spigot().sendMessage(msg2);
		player.spigot().sendMessage(msg3);
		player.spigot().sendMessage(msg4);
		player.spigot().sendMessage(msg6);
		player.spigot().sendMessage(msg7);
		player.spigot().sendMessage(msg5);
	}
	
	public void time(Player player, OfflinePlayer target, User u, String language)
	{
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
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "afktime", "player_uuid")));
			ontime = plugin.getUtility().timetl(
					u.getAfktime()+
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "activitytime", "player_uuid")));
			alltime = plugin.getUtility().timetl(
					u.getAfktime()+
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "alltime", "player_uuid")));
			lastactivity = plugin.getUtility().getDateExact(u.getLastactivity());
		} else
		{
			afktime = plugin.getUtility().timetl(
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "afktime", "player_uuid")));
			ontime = plugin.getUtility().timetl(
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "activitytime", "player_uuid")));
			alltime = plugin.getUtility().timetl(
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "alltime", "player_uuid")));
			lastactivity = plugin.getUtility().getDateExact(
					Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							target.getUniqueId().toString(), "lastactivity", "player_uuid")));
		}
		player.spigot().sendMessage(plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(
						language+".CMDAfkRecord.time.msg01").replaceAll("%player%", target.getName())));
		TextComponent msg1 = plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(
						language+".CMDAfkRecord.time.msg02")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg02").contains("%ontime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg06"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg02").contains("%afktime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg07"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg02").contains("%alltime%"))
		{
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg1.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg08"))).create()));
		}
		TextComponent msg2 = plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(
						language+".CMDAfkRecord.time.msg03")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg03").contains("%ontime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg06"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg03").contains("%afktime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg07"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg03").contains("%alltime%"))
		{
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg2.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg08"))).create()));
		}
		TextComponent msg3 = plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(
						language+".CMDAfkRecord.time.msg04")
				.replaceAll("%ontime%", ontime)
				.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
				.replaceAll("%afktime%", afktime)
				.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
				.replaceAll("%alltime%", alltime)
				.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace)));
		if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg04").contains("%ontime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top onlinetime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg06"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg04").contains("%afktime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top afktime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg07"))).create()));
		} else if(plugin.getYamlHandler().getL().getString(
				language+".CMDAfkRecord.time.msg04").contains("%alltime%"))
		{
			msg3.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr top alltime"));
			msg3.setHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					new ComponentBuilder(plugin.getUtility().tl
					(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.time.msg08"))).create()));
		}
		player.spigot().sendMessage(msg1);
		player.spigot().sendMessage(msg2);
		player.spigot().sendMessage(msg3);
		if(player.hasPermission("afkrecord.cmd.afkrecord.time.lastactivity"))
		{
			player.spigot().sendMessage(plugin.getUtility().tcl(
					plugin.getYamlHandler().getL().getString(
							language+".CMDAfkRecord.time.msg05").replaceAll("%time%", lastactivity)));
		}
	}
	
	public void top(Player player, String language, ArrayList<TopList> ar, String[] args, int page,
			String headpath, String subcmd)
	{
		int size = ar.size()-1;
		int start = 0;
		int stop = 0;
		int end = 0;
		start = page*10;
		stop = page*10+10;
		end = (int) Math.ceil(size/10);
		if(stop>size)
		{
			stop = size;
			start = size-10;
			if(start<0)
			{
				start = 0;
			}
		}
		player.spigot().sendMessage(plugin.getUtility().tcl(
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
				player.spigot().sendMessage(plugin.getUtility().tcl(
						plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg04")
						.replaceAll("%place%", plugin.getUtility().getPlaceColor(i.getPlace()))
						.replaceAll("%player%", i.getName())
						.replaceAll("%time%", plugin.getUtility().timetl(i.getTime()))));
			}
			start++;
		}
		int i = page+1;
		TextComponent MSG = plugin.getUtility().tcl("");
		List<BaseComponent> list = new ArrayList<BaseComponent>();
		if(end>=2 && page==0)
		{
			TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05").substring(1)));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord top "+subcmd+" "+i));
			list.add(msg1);
			MSG.setExtra(list);	
			player.spigot().sendMessage(MSG);
		} else if(end>=2 && page!=0)
		{
			TextComponent msg2 = plugin.getUtility().tc(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg06")+"|"));
			msg2.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord top "+subcmd+" "+end));
			list.add(msg2);
			TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
			msg1.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord top "+subcmd+" "+i));
			list.add(msg1);
			MSG.setExtra(list);	
			player.spigot().sendMessage(MSG);
		}
	}
	
	public void gettime(Player player, OfflinePlayer target, String language, int page)
	{
		int start = page*10;
		int stop = page*10+10;
		ArrayList<PlayerInfo> a = plugin.getMysqlInterface().getListII(target.getUniqueId().toString());
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
		player.spigot().sendMessage(plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.gettime.msg01")
				.replaceAll("%player%", target.getName())));
		while(start<=stop)
		{
			player.spigot().sendMessage(plugin.getUtility().tcl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.gettime.msg02")
					.replaceAll("%date%", a.get(start).getDate())
					.replaceAll("%alltime%", plugin.getUtility().timetl(a.get(start).getAlltime()))
					.replaceAll("%ontime%", plugin.getUtility().timetl(a.get(start).getActivitytime()))
					.replaceAll("%afktime%", plugin.getUtility().timetl(a.get(start).getAfktime()))));
			start++;
		}
	}
	
	public void counttime(Player player, OfflinePlayer target, String language, int days)
	{
		PlayerInfo pi = plugin.getMysqlInterface().getCountTime(target.getUniqueId().toString(), days);
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg01")
				.replaceAll("%player%", target.getName())
				.replaceAll("%days%", String.valueOf(days))));
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg02")
				.replaceAll("%ontime%", plugin.getUtility().timetl(pi.getActivitytime()))
				.replaceAll("%afktime%", plugin.getUtility().timetl(pi.getAfktime()))
				.replaceAll("%alltime%", plugin.getUtility().timetl(pi.getAlltime()))));
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg03")
				.replaceAll("%ontime%", plugin.getUtility().timetl(pi.getActivitytime()))
				.replaceAll("%afktime%", plugin.getUtility().timetl(pi.getAfktime()))
				.replaceAll("%alltime%", plugin.getUtility().timetl(pi.getAlltime()))));
		player.sendMessage(plugin.getUtility().tl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg04")
				.replaceAll("%ontime%", plugin.getUtility().timetl(pi.getActivitytime()))
				.replaceAll("%afktime%", plugin.getUtility().timetl(pi.getAfktime()))
				.replaceAll("%alltime%", plugin.getUtility().timetl(pi.getAlltime()))));
		if(player.hasPermission("afkrecord.cmd.afkrecord.counttime.lastactivity"))
		{
			player.sendMessage(plugin.getUtility().tl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg05")
					.replaceAll("%time%", plugin.getUtility().getDateExact(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
							player.getUniqueId().toString(), "lastactivity", "player_uuid"))))));
		}
	}
	
	public void getafk(Player player, String language)
	{
		ArrayList<User> user = plugin.getUtility().sortAfkList(User.getUsers());
		boolean check = false;
		TextComponent playerlist = plugin.getUtility().tc("");
		long now = System.currentTimeMillis();
		TextComponent MSG = plugin.getUtility().tcl(
				plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.getafk.msg02"));
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
							.getString(language+".CMDAfkRecord.getafk.playercolor.under15min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
				} else if(time>=15 && time<30)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CMDAfkRecord.getafk.playercolor.15min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
				} else if(time>=30 && time<45)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CMDAfkRecord.getafk.playercolor.30min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
				} else if(time>=45 && time<60)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CMDAfkRecord.getafk.playercolor.45min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
				} else if(time>=60 && time<90)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CMDAfkRecord.getafk.playercolor.60min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
				} else if(time>=90 && time<120)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CMDAfkRecord.getafk.playercolor.90min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
				} else if(time>=120)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CMDAfkRecord.getafk.playercolor.120min");
					playerlist = plugin.getUtility().tcl(pc+u.getPlayer().getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getPlayer().getName()));
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
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.getafk.msg01")));
			return;
		}
		MSG.setExtra(list);
		player.spigot().sendMessage(MSG);
	}
}
