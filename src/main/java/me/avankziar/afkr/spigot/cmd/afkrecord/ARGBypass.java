package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;
import main.java.me.avankziar.afkr.spigot.handler.PlayerTimesHandler;

public class ARGBypass extends ArgumentModule
{
	private AfkR plugin;
	
	public ARGBypass(AfkR plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		if(PlayerTimesHandler.playerWhoBypassAfkTracking.contains(uuid))
		{
			PlayerTimesHandler.playerWhoBypassAfkTracking.remove(uuid);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Bypass.YouDontBypass")));
		} else
		{
			PlayerTimesHandler.playerWhoBypassAfkTracking.add(uuid);
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Bypass.YouBypass")));
		}
	}

}
