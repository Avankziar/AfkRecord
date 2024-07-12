package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.permission.BypassPermission;
import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;

public class ARGTime extends ArgumentModule
{
	private AfkR plugin;
	
	public ARGTime(AfkR plugin, ArgumentConstructor argumentConstructor)
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
			if(!plugin.getMysqlHandler().exist(MysqlType.PLUGINUSER,
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