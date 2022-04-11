package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.permission.BypassPermission;

public class ARGGetTime extends ArgumentModule
{
	private AfkRecord plugin;
	private ArgumentConstructor argumentConstructor;
	
	public ARGGetTime(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.argumentConstructor = argumentConstructor;
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int page = 0;
		if(args.length >= 2)
		{
			if(!args[1].matches("[0-9]+"))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("IllegalArgument")));
				return;
			}
			page = Integer.parseInt(args[1]);
		}
		if(args.length == 3)
		{
			if(!player.hasPermission(BypassPermission.GETTIMEOTHER)
					&& !player.getName().equals(args[2]))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			if(!plugin.getMysqlHandler().exist(Type.PLUGINUSER,
					"`player_uuid` = ?", Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString()))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
			plugin.getCommandHelper().gettime(player, target, page, argumentConstructor.getCommandString());
			return;
		}
		plugin.getCommandHelper().gettime(player, player, 0, argumentConstructor.getCommandString());
	}
}