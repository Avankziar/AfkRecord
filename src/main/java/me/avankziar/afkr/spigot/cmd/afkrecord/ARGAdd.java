package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.assistance.MatchApi;
import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;

public class ARGAdd extends ArgumentModule
{
	private AfkR plugin;
	
	public ARGAdd(AfkR plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String other = args[1];
		String type = args[2];
		String dura = args[3];
		long dur = 0;
		if(!"alltime".equals(type) && !"afktime".equals(type) && !"onlinetime".equals(type))
		{
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.Format")));
			return;
		}
		if(MatchApi.isLong(dura))
		{
			dur = Long.valueOf(dura);
		} else
		{
			if(StringUtils.countMatches(dura, ":") != 3)
			{
				player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.TimeFormat")));
				return;
			}
			long negative = 1;
			String durat = dura;
			if(dura.startsWith("-"))
			{
				durat = dura.substring(1);
				negative = -1;
			}
			String[] du = durat.split(":");
			if(du.length != 4)
			{
				player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.TimeFormat")));
				return;
			}
			long days = Long.valueOf(du[0]) * 1000 * 60 * 60 * 24;
			long hours = Long.valueOf(du[1]) * 1000 * 60 * 60;
			long mins = Long.valueOf(du[2]) * 1000 * 60;
			long secs = Long.valueOf(du[3]) * 1000;
			dur = negative * (days + hours + mins + secs);
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER, "`player_name` = ?", other);
		if(user == null)
		{
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		switch(type)
		{
		case "alltime":
			user.setTotalTime(user.getTotalTime()+dur);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.Alltime")
					.replace("%player%", other)
					.replace("%time%", dura)));
			break;
		case "afktime":
			user.setAfkTime(user.getAfkTime()+dur);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.Afktime")
					.replace("%player%", other)
					.replace("%time%", dura)));
			break;
		case "onlinetime":
			user.setActiveTime(user.getActiveTime()+dur);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.Onlinetime")
					.replace("%player%", other)
					.replace("%time%", dura)));
			break;
		}
		plugin.getMysqlHandler().updateData(MysqlType.PLUGINUSER, user, "`player_uuid` = ?", user.getUUID().toString());
	}
}