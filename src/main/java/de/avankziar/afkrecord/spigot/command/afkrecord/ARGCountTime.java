package main.java.de.avankziar.afkrecord.spigot.command.afkrecord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;

public class ARGCountTime extends CommandModule
{
	private AfkRecord plugin;
	
	public ARGCountTime(AfkRecord plugin)
	{
		super("counttime","afkrecord.cmd.afkrecord.counttime.self",AfkRecord.afkrarguments,2,3);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String language = plugin.getUtility().getLanguage();
		if(args.length==2)
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.counttime.self"))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getL().getString(language+".NoPermission")));
				return;
			}
			if(!args[1].matches("[0-9]+"))
			{
				player.spigot().sendMessage(
						ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
				return;
			}
			int days = Integer.parseInt(args[1]);
			plugin.getCommandHelper().counttime(player, player, days);
			return;
		} else if(args.length==3)
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.counttime.other"))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getL().getString(language+".NoPermission")));
				return;
			}
			if(!args[1].matches("[0-9]+"))
			{
				player.spigot().sendMessage(
						ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".IllegalArgument")));
				return;
			}
			if(!plugin.getMysqlHandler().hasAccount(Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString()))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getL().getString(language+".PlayerNotExist")));
				return;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
			int days = Integer.parseInt(args[1]);
			plugin.getCommandHelper().counttime(player, target, days);
			return;
		}
	}
}