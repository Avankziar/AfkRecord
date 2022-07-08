package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;

public class ARGBypass extends ArgumentModule
{
	private AfkRecord plugin;
	
	public ARGBypass(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		if(plugin.getPlayerTimes().playerWhoBypassAfkTracking.contains(uuid))
		{
			plugin.getPlayerTimes().playerWhoBypassAfkTracking.remove(uuid);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Bypass.YouDontBypass")));
		} else
		{
			plugin.getPlayerTimes().playerWhoBypassAfkTracking.add(uuid);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Bypass.YouBypass")));
		}
	}

}
