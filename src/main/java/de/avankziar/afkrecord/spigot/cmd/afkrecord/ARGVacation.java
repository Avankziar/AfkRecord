package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.assistance.MatchApi;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.permission.BypassPermission;

public class ARGVacation extends ArgumentModule
{
	private AfkRecord plugin;
	
	public ARGVacation(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		
		if(args.length == 1)
		{
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
					"`player_uuid` = ?", player.getUniqueId().toString());
			if(user == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			if(System.currentTimeMillis() > user.getVacationTime())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.NotInVacation")));
				return;
			} else
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.YourAreInVacation")
						.replace("%time%", plugin.getPlayerTimes().formatDate(user.getVacationTime()))));
				return;
			}
		} else if(args.length == 2)
		{
			if(!player.hasPermission(BypassPermission.VACATIONOTHER)
					&& !args[1].equals(player.getName()))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_name` = ?", args[1]);
			if(user == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			if(MatchApi.isNumber(args[1]))
			{
				long days = Long.parseLong(args[1]);
				if(days == 0)
				{
					user.setVacationTime(0);
					plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.NowNotInVacation")));
					return;
				}
				final long datetime = days*24*60*60*1000 + System.currentTimeMillis();
				if(datetime < System.currentTimeMillis())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.NoVacationInThePast")));
					return;
				}
				user.setVacationTime(datetime);
				plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.SetVacation")
						.replace("%time%", plugin.getPlayerTimes().formatDate(datetime))));
			} else
			{
				if(System.currentTimeMillis() > user.getVacationTime())
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.ThePlayerNotInVacation")
							.replace("%player%", args[1])));
					return;
				} else
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.ThePlayerIsInVacation")
							.replace("%player%", args[1])
							.replace("%time%", plugin.getPlayerTimes().formatDate(user.getVacationTime()))));
					return;
				}
			}			
		} else if(args.length == 3)
		{
			String[] date = args[1].split("\\.");
			String[] time = args[2].split("\\:");
			if(date.length != 3 || time.length != 2)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.WrongFormat")));
				return;
			}
			if(!MatchApi.isInteger(date[0]) || !MatchApi.isInteger(date[1]) || !MatchApi.isInteger(date[2])
					|| !MatchApi.isInteger(time[0]) || !MatchApi.isInteger(time[1]))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("IllegalArgument")));
				return;
			}
			int min = Integer.parseInt(time[1]);
			int hour = Integer.parseInt(time[0]);
			int day = Integer.parseInt(date[0]);
			int month = Integer.parseInt(date[1]);
			int year = Integer.parseInt(date[2]);
			if(min < 0 || min > 59 
					|| hour < 0 || hour > 23 
					|| day < 1 || day > 31 
					|| month < 1 || month > 12 
					|| year < 0 || year > 9999)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("IllegalNumber")));
				return;
			}
			PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
					"`player_uuid` = ?", player.getUniqueId().toString());
			if(user == null)
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			final long datetime = LocalDateTime.of(
					year, month, day,
					hour, min, 0, 0)
					.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
			if(datetime < System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.NoVacationInThePast")));
				return;
			}
			user.setVacationTime(datetime);
			plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Vacation.SetVacation")
					.replace("%time%", plugin.getPlayerTimes().formatDate(datetime))));
		}
	}
}
