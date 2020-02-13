package main.java.de.avankziar.afkrecord.spigot.cmd;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;
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
			plugin.getCommandHandler().info(player, language);
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
				plugin.getCommandHandler().time(player, player, u, language);
				return true;
			} else if(args.length==2)
			{
				if(!player.hasPermission("afkrecord.cmd.afkrecord.time.other"))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
					return false;
				}
				if(!plugin.getMysqlInterface().hasAccount(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString()))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg02")));
					return false;
				}
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
				User u = User.getUser(target);
				plugin.getCommandHandler().time(player, target, u, language);
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
					int page = 0;
					if(args.length==2)
					{
						plugin.getCommandHandler().top(player, language, plugin.getBackgroundTask().ac, args,
								page, ".CMDAfkRecord.top.msg01", "onlinetime");
					} else if(args.length==3)
					{
						if(!args[2].matches("[0-9]+"))
						{
							player.spigot().sendMessage(plugin.getUtility().tcl(
									plugin.getYamlHandler().getL().getString(language+".msg03")));
							return false;
						}
						page = Integer.parseInt(args[2]);
						plugin.getCommandHandler().top(player, language, plugin.getBackgroundTask().ac, args,
								page, ".CMDAfkRecord.top.msg01", "onlinetime");
					}
					return true;
				} else if(args[1].equalsIgnoreCase("alltime") || args[1].equalsIgnoreCase("gesamtzeit"))
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.top.alltime"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					int page = 0;
					if(args.length==2)
					{
						plugin.getCommandHandler().top(player, language, plugin.getBackgroundTask().all, args,
								page, ".CMDAfkRecord.top.msg02", "alltime");
					} else if(args.length==3)
					{
						if(!args[2].matches("[0-9]+"))
						{
							player.spigot().sendMessage(plugin.getUtility().tcl(
									plugin.getYamlHandler().getL().getString(language+".msg03")));
							return false;
						}
						page = Integer.parseInt(args[2]);
						plugin.getCommandHandler().top(player, language, plugin.getBackgroundTask().all, args,
								page, ".CMDAfkRecord.top.msg02", "alltime");
					}
					return true;
				} else if(args[1].equalsIgnoreCase("afktime") || args[1].equalsIgnoreCase("afkzeit"))
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.top.afktime"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					int page = 0;
					if(args.length==2)
					{
						plugin.getCommandHandler().top(player, language, plugin.getBackgroundTask().afk, args,
								page, ".CMDAfkRecord.top.msg03", "afktime");
					} else if(args.length==3)
					{
						if(!args[2].matches("[0-9]+"))
						{
							player.spigot().sendMessage(plugin.getUtility().tcl(
									plugin.getYamlHandler().getL().getString(language+".msg03")));
							return false;
						}
						page = Integer.parseInt(args[2]);
						plugin.getCommandHandler().top(player, language, plugin.getBackgroundTask().afk, args,
								page, ".CMDAfkRecord.top.msg03", "afktime");
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
					plugin.getCommandHandler().gettime(player, player, language, 0);
					return true;
				} else if(args.length==2)
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.gettime.self"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					if(!args[1].matches("[0-9]+"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg03")));
						return false;
					}
					int page = Integer.parseInt(args[1]);
					plugin.getCommandHandler().gettime(player, player, language, page);
					return true;
				} else if(args.length==3)
				{
					if(!player.hasPermission("afkrecord.cmd.afkrecord.gettime.other"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg01")));
						return false;
					}
					if(!plugin.getMysqlInterface().hasAccount(Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString()))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg02")));
						return false;
					}
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
					if(!args[1].matches("[0-9]+"))
					{
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".msg03")));
						return false;
					}
					int page = Integer.parseInt(args[1]);
					plugin.getCommandHandler().gettime(player, target, language, page);
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
				plugin.getCommandHandler().counttime(player, player, language, days);
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
				if(!plugin.getMysqlInterface().hasAccount(Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString()))
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(language+".msg02")));
					return false;
				}
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
				int days = Integer.parseInt(args[1]);
				plugin.getCommandHandler().counttime(player, target, language, days);
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
			plugin.getCommandHandler().getafk(player, language);
			return true;
		} else
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
