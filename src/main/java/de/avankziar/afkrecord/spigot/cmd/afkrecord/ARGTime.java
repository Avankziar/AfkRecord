package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.permission.BypassPermission;

public class ARGTime extends ArgumentModule
{
	private AfkRecord plugin;
	
	public ARGTime(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		if(args.length == 2)
		{
			if(!player.hasPermission(BypassPermission.TIMEOTHER))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			if(!plugin.getMysqlHandler().exist(Type.PLUGINUSER,
					"`player_uuid` = ?", Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString()))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
				return;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					plugin.getCommandHelper().time(player, target);
				}
			}.runTaskAsynchronously(plugin);
			return;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				plugin.getCommandHelper().time(player, player);
			}
		}.runTaskAsynchronously(plugin);
		return;
	}
}