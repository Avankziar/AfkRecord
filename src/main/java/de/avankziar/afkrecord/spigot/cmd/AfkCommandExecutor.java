package main.java.de.avankziar.afkrecord.spigot.cmd;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.CommandConstructor;

public class AfkCommandExecutor implements CommandExecutor
{
	private AfkRecord plugin;
	
	public AfkCommandExecutor(AfkRecord plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			return false;
		}
		final Player player = (Player) sender;
		final UUID uuid = player.getUniqueId();
		if(plugin.getPlayerTimes().playerWhoBypassAfkTracking.contains(uuid))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Bypass.YouAredBypass")));
		} else
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					plugin.getPlayerTimes().saveRAM(uuid, !plugin.getPlayerTimes().isActive(uuid), false, false, true);
				}
			}.runTaskAsynchronously(plugin);
		}
		return true;
	}
}