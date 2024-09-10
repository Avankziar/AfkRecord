package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.assistance.MatchApi;
import main.java.me.avankziar.afkr.general.assistance.TimeHandler;
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
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.TimeFormat")));
			return;
		}
		if(MatchApi.isLong(dura))
		{
			dur = Long.valueOf(dura);
		} else
		{
			try
			{
				dur = LocalDateTime.parse(dura, DateTimeFormatter.ofPattern("dd:HH:mm:ss"))
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			} catch(Exception e)
			{
				player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("")));
				return;
			}
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
					.replace("%dur%", TimeHandler.getTime(dur, "dd-HH:mm:ss"))));
			break;
		case "afktime":
			user.setAfkTime(user.getAfkTime()+dur);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.Afktime")
					.replace("%player%", other)
					.replace("%dur%", TimeHandler.getTime(dur, "dd-HH:mm:ss"))));
			break;
		case "onlinetime":
			user.setActiveTime(user.getActiveTime()+dur);
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Add.Onlinetime")
					.replace("%player%", other)
					.replace("%dur%", TimeHandler.getTime(dur, "dd.HH:mm:ss"))));
			break;
		}
		plugin.getMysqlHandler().updateData(MysqlType.PLUGINUSER, user, "`player_uuid` = ?", user.getUUID().toString());
	}
}