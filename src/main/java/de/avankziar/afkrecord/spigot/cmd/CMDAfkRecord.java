package main.java.de.avankziar.afkrecord.spigot.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.interfaces.PlayerInfo;
import main.java.de.avankziar.afkrecord.spigot.interfaces.TopList;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CMDAfkRecord implements CommandExecutor
{
	private AfkRecord plugin;
	
	public CMDAfkRecord(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			return false;
		}
		Player player = (Player) sender;
		String language = plugin.getYamlHandler().get().getString("language");
		if(args.length==0)
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.info"))
			{
				player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
				return false;
			}
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
			return true;
		} else if("time".equalsIgnoreCase(args[0]))
		{
			if(args.length==1)
			{
				if(!player.hasPermission("afkrecord.cmd.afkrecord.time.self"))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
					return false;
				}
				User u = User.getUser(player);
				ArrayList<TopList> ac = plugin.getUtility().sortTopList(plugin.getMysqlInterface().getTop("activitytime"));
				int acplace = 0;
				for(TopList tl : ac)
				{
					if(tl.getName().equals(player.getName()))
					{
						acplace = tl.getPlace();
						break;
					}
				}
				ArrayList<TopList> afk = plugin.getUtility().sortTopList(plugin.getMysqlInterface().getTop("afktime"));
				int afkplace = 0;
				for(TopList tl : afk)
				{
					if(tl.getName().equals(player.getName()))
					{
						afkplace = tl.getPlace();
						break;
					}
				}
				ArrayList<TopList> all = plugin.getUtility().sortTopList(plugin.getMysqlInterface().getTop("alltime"));
				int allplace = 0;
				for(TopList tl : all)
				{
					if(tl.getName().equals(player.getName()))
					{
						allplace = tl.getPlace();
						break;
					}
				}
				if(u!=null)
				{
					String afktime = plugin.getUtility().timetl(
							u.getAfktime()
							+Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "afktime", "player_uuid")));
					String ontime = plugin.getUtility().timetl(
							u.getActivitytime()
							+Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "activitytime", "player_uuid")));
					String alltime = plugin.getUtility().timetl(
							u.getAlltime()
							+Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "alltime", "player_uuid")));
					String lastactivity = plugin.getUtility().getDateExact(u.getLastactivity());
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg01").replaceAll("%player%", player.getName())));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg02")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg03")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg04")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					if(player.hasPermission("afkrecord.cmd.afkrecord.time.lastactivity"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(
										language+".CMDAfkRecord.time.msg05").replaceAll("%time%", lastactivity)));
					}
				} else
				{
					String afktime = plugin.getUtility().timetl(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "afktime", "player_uuid")));
					String ontime = plugin.getUtility().timetl(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "activitytime", "player_uuid")));
					String alltime = plugin.getUtility().timetl(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "alltime", "player_uuid")));
					String lastactivity = plugin.getUtility().getDateExact(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "lastactivity", "player_uuid")));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg01").replaceAll("%player%", player.getName())));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg02")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg03")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg04")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					if(player.hasPermission("afkrecord.cmd.afkrecord.time.lastactivity"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(
										language+".CMDAfkRecord.time.msg05").replaceAll("%time%", lastactivity)));
					}
				}
				return true;
			} else if(args.length==2)
			{
				if(!player.hasPermission("afkrecord.cmd.afkrecord.time.other"))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
					return false;
				}
				if(args[1].matches("[0-9]+"))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg02")));
					return false;
				}
				if(Bukkit.getOfflinePlayer(args[1])==null)
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg02")));
					return false;
				}
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
				User u = User.getUser(target.getPlayer());
				ArrayList<TopList> ac = plugin.getBackgroundTask().ac;
				int acplace = 0;
				for(TopList tl : ac)
				{
					if(tl.getName().equals(target.getName()))
					{
						acplace = tl.getPlace();
						break;
					}
				}
				ArrayList<TopList> afk = plugin.getBackgroundTask().afk;
				int afkplace = 0;
				for(TopList tl : afk)
				{
					if(tl.getName().equals(target.getName()))
					{
						afkplace = tl.getPlace();
						break;
					}
				}
				ArrayList<TopList> all = plugin.getBackgroundTask().all;
				int allplace = 0;
				for(TopList tl : all)
				{
					if(tl.getName().equals(target.getName()))
					{
						allplace = tl.getPlace();
						break;
					}
				}
				if(u!=null)
				{
					String afktime = plugin.getUtility().timetl(
							u.getAfktime()+
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "afktime", "player_uuid")));
					String ontime = plugin.getUtility().timetl(
							u.getAfktime()+
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "activitytime", "player_uuid")));
					String alltime = plugin.getUtility().timetl(
							u.getAfktime()+
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "alltime", "player_uuid")));
					String lastactivity = plugin.getUtility().getDateExact(u.getLastactivity());
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg01").replaceAll("%player%", target.getName())));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg02")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg03")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg04")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					if(player.hasPermission("afkrecord.cmd.afkrecord.time.lastactivity"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(
										language+".CMDAfkRecord.time.msg05").replaceAll("%time%", lastactivity)));
					}
				} else
				{
					String afktime = plugin.getUtility().timetl(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "afktime", "player_uuid")));
					String ontime = plugin.getUtility().timetl(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "activitytime", "player_uuid")));
					String alltime = plugin.getUtility().timetl(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "alltime", "player_uuid")));
					String lastactivity = plugin.getUtility().getDateExact(
							Long.parseLong((String) plugin.getMysqlInterface().getDataI(
									target.getUniqueId().toString(), "lastactivity", "player_uuid")));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg01").replaceAll("%player%", target.getName())));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg02")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg03")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(
									language+".CMDAfkRecord.time.msg04")
							.replaceAll("%ontime%", ontime)
							.replaceAll("%onplace%", plugin.getUtility().getPlaceColor(acplace))
							.replaceAll("%afktime%", afktime)
							.replaceAll("%afkplace%", plugin.getUtility().getPlaceColor(afkplace))
							.replaceAll("%alltime%", alltime)
							.replaceAll("%allplace%", plugin.getUtility().getPlaceColor(allplace))));
					if(player.hasPermission("afkrecord.cmd.afkrecord.time.lastactivity"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(
										language+".CMDAfkRecord.time.msg05").replaceAll("%time%", lastactivity)));
					}
				}
				return true;
			} else 
			{
				TextComponent msg = plugin.getUtility().tc(
						plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
				player.spigot().sendMessage(msg);
				return false;
			}
		} else if("top".equalsIgnoreCase(args[0])) //Top 10
		{
			if(args.length>1 && args.length<=3)
			{
				if(args[1].equalsIgnoreCase("onlinetime") || args[1].equalsIgnoreCase("spielzeit"))
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.top.onlinetime"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					ArrayList<TopList> ac = plugin.getBackgroundTask().ac;
					int size = ac.size()-1;
					int page = 0;
					int start = 0;
					int stop = 0;
					if(args.length==2)
					{
						start = page*10;
						stop = page*10+10;
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
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg01")));
						while(start<=stop)
						{
							TopList i;
							try
							{
								i = ac.get(start);
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
						TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
						msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top onlinetime "+i));
						player.spigot().sendMessage(msg1);
					} else if(args.length==3)
					{
						if(!args[2].matches("[0-9]+"))
						{
							player.spigot().sendMessage(plugin.getUtility().tcl(
									plugin.getYamlHandler().getL().getString(language+".msg03")));
							return false;
						}
						page = Integer.parseInt(args[2]);
						start = page*10;
						stop = page*10+10;
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
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg01")));
						while(start<=stop)
						{
							TopList i;
							try
							{
								i = ac.get(start);
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
						TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
						msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top onlinetime "+i));
						player.spigot().sendMessage(msg1);
					}
					return true;
				} else if(args[1].equalsIgnoreCase("alltime") || args[1].equalsIgnoreCase("gesamtzeit"))
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.top.alltime"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					ArrayList<TopList> all = plugin.getBackgroundTask().all;
					int size = all.size()-1;
					int page = 0;
					int start = 0;
					int stop = 0;
					if(args.length==2)
					{
						start = page*10;
						stop = page*10+10;
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
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg02")));
						while(start<=stop)
						{
							TopList i;
							try
							{
								i = all.get(start);
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
						TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
						msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top alltime "+i));
						player.spigot().sendMessage(msg1);
					} else if(args.length==3)
					{
						if(!args[2].matches("[0-9]+"))
						{
							player.spigot().sendMessage(plugin.getUtility().tcl(
									plugin.getYamlHandler().getL().getString(language+".msg03")));
							return false;
						}
						page = Integer.parseInt(args[2]);
						start = page*10;
						stop = page*10+10;
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
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg02")));
						while(start<=stop)
						{
							TopList i;
							try
							{
								i = all.get(start);
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
						TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
						msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top alltime "+i));
						player.spigot().sendMessage(msg1);
					}
					return true;
				} else if(args[1].equalsIgnoreCase("afktime") || args[1].equalsIgnoreCase("afkzeit"))
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.top.afktime"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					ArrayList<TopList> afk = plugin.getBackgroundTask().afk;
					int size = afk.size()-1;
					int page = 0;
					int start = 0;
					int stop = 0;
					if(args.length==2)
					{
						start = page*10;
						stop = page*10+10;
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
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg03")));
						while(start<=stop)
						{
							TopList i;
							try
							{
								i = afk.get(start);
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
						TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
						msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top afktime "+i));
						player.spigot().sendMessage(msg1);
					} else if(args.length==3)
					{
						if(!args[2].matches("[0-9]+"))
						{
							player.spigot().sendMessage(plugin.getUtility().tcl(
									plugin.getYamlHandler().getL().getString(language+".msg03")));
							return false;
						}
						page = Integer.parseInt(args[2]);
						start = page*10;
						stop = page*10+10;
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
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg03")));
						while(start<=stop)
						{
							TopList i;
							try
							{
								i = afk.get(start);
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
						TextComponent msg1 = plugin.getUtility().tc(plugin.getUtility().tl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.top.msg05")));
						msg1.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/afkrecord top afktime "+i));
						player.spigot().sendMessage(msg1);
					}
					return true;
				} else
				{
					TextComponent msg = plugin.getUtility().tc(
							plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
					msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
					player.spigot().sendMessage(msg);
					return false;
				}
			} else
			{
				TextComponent msg = plugin.getUtility().tc(
						plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
				player.spigot().sendMessage(msg);
				return false;
			}
		} else if("gettime".equalsIgnoreCase(args[0]))
		{
			if(args.length>=1 && args.length<=3)
			{
				if(args.length==1)
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.gettime.self"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					int start = 0;
					int stop = 10;
					ArrayList<PlayerInfo> a = plugin.getMysqlInterface().getListII(player.getUniqueId().toString());
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
							.replaceAll("%player%", player.getName())));
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
					return true;
				} else if(args.length==2)
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.gettime.self"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					int page = 0;
					if(!args[1].matches("[0-9]+"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg03")));
						return false;
					}
					page = Integer.parseInt(args[1]);
					int start = page*10;
					int stop = page*10+10;
					ArrayList<PlayerInfo> a = plugin.getMysqlInterface().getListII(player.getUniqueId().toString());
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
							.replaceAll("%player%", player.getName())));
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
					return true;
				} else if(args.length==3)
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.gettime.other"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					if(args[2].matches("[0-9]+"))
					{
						player.spigot().sendMessage(
								plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg02")));
						return false;
					}
					if(Bukkit.getOfflinePlayer(args[2])==null)
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg02")));
						return false;
					}
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
					int page = 0;
					if(!args[1].matches("[0-9]+"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg03")));
						return false;
					}
					page = Integer.parseInt(args[1]);
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
					return true;
				}
			} else
			{
				TextComponent msg = plugin.getUtility().tc(
						plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
				player.spigot().sendMessage(msg);
				return false;
			}
		} else if("counttime".equalsIgnoreCase(args[0]) || "zeitzählen".equalsIgnoreCase(args[0]))
		{
			if(args.length==2)
			{
				if(!player.hasPermission("afkrecord.cmd.afkrecord.counttime.self"))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(language+".msg01")));
					return false;
				}
				if(!args[1].matches("[0-9]+"))
				{
					player.spigot().sendMessage(
							plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg03")));
					return false;
				}
				int days = Integer.parseInt(args[1]);
				PlayerInfo pi = plugin.getMysqlInterface().getCountTime(player.getUniqueId().toString(), days);
				player.sendMessage(plugin.getUtility().tl(
						plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg01")
						.replaceAll("%player%", player.getName())
						.replaceAll("%days%", args[1])));
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
				return true;
			} else if(args.length==3)
			{
				if(!player.hasPermission("afkrecord.cmd.afkrecord.counttime.other"))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(language+".msg01")));
					return false;
				}
				if(!args[1].matches("[0-9]+"))
				{
					player.spigot().sendMessage(
							plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg03")));
					return false;
				}
				if(args[2].matches("[0-9]+"))
				{
					player.spigot().sendMessage(
							plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg02")));
					return false;
				}
				if(Bukkit.getOfflinePlayer(args[2])==null)
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(language+".msg02")));
					return false;
				}
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
				int days = Integer.parseInt(args[1]);
				PlayerInfo pi = plugin.getMysqlInterface().getCountTime(target.getUniqueId().toString(), days);
				player.sendMessage(plugin.getUtility().tl(
						plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.counttime.msg01")
						.replaceAll("%player%", target.getName())
						.replaceAll("%days%", args[1])));
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
				return true;
			} else 
			{
				TextComponent msg = plugin.getUtility().tc(
						plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
				player.spigot().sendMessage(msg);
				return false;
			}
		} else if("getafk".equalsIgnoreCase(args[0]))
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.getafk"))
			{
				player.spigot().sendMessage(plugin.getUtility().tcl(
						plugin.getYamlHandler().getL().getString(language+".msg01")));
				return false;
			}
			if(args.length!=1)
			{
				TextComponent msg = plugin.getUtility().tc(
						plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
				player.spigot().sendMessage(msg);
				return false;
			}
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
				return false;
			}
			MSG.setExtra(list);
			player.spigot().sendMessage(MSG);
			return true;
		}/* else if("convertolddata".equalsIgnoreCase(args[0])) 
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.convertolddata"))
			{
				player.spigot().sendMessage(plugin.getUtility().tcl(
						plugin.getYamlHandler().getL().getString(language+".msg01")));
				return false;
			}
			if(args.length!=5)
			{
				TextComponent msg = plugin.getUtility().tc(
						plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
				player.spigot().sendMessage(msg);
				return false;
			}
			player.spigot().sendMessage(plugin.getUtility().tcl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.convertolddata.msg01")));
			String tableName = args[1];
			String playercolumn = args[2];
			String olddatacolumn = args[3];
			Boolean deletedata = Boolean.parseBoolean(args[4]);
			ArrayList<TopList> a = plugin.getMysqlInterface().getOldToConvertData(tableName, playercolumn, olddatacolumn);
			for(TopList tl : a)
			{
				if(plugin.getMysqlInterface().hasAccount(tl.getName()))
				{
					long thisdata = Long.parseLong((String) plugin.getMysqlInterface().getDataI(tl.getName(), "alltime", "player_uuid"))
							+ tl.getTime();
					plugin.getMysqlInterface().updateDataI(player, thisdata, "alltime");
					if(deletedata==true)
					{
						plugin.getMysqlInterface().deleteData(
								player.getUniqueId().toString(), playercolumn, tableName);
					}
				}
			}
			player.spigot().sendMessage(plugin.getUtility().tcl(
					plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.convertolddata.msg02")));
			return true;
		}*/ else
		{
			TextComponent msg = plugin.getUtility().tc(
					plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
			msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
			player.spigot().sendMessage(msg);
			return false;
		}
		return false;
	}	
}
