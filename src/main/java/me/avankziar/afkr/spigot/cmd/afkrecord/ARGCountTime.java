package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.permission.BypassPermission;
import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;

public class ARGCountTime extends ArgumentModule
{
	private AfkR plugin;
	
	public ARGCountTime(AfkR plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		if(!args[1].matches("[0-9]+"))
		{
			player.spigot().sendMessage(
					ChatApi.tctl(plugin.getYamlHandler().getLang().getString("IllegalArgument")));
			return;
		}
		int days = Integer.parseInt(args[1]);
		if(args.length >= 3)
		{
			if(!player.hasPermission(BypassPermission.COUNTTIMEOTHER))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			if(!plugin.getMysqlHandler().exist(MysqlType.PLUGINUSER, "`player_uuid` = ?", Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString()))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
			plugin.getCommandHelper().counttime(player, target, days);
			return;
		}
		plugin.getCommandHelper().counttime(player, player, days);
	}
}