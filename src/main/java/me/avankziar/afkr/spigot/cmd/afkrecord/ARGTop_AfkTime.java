package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;
import main.java.me.avankziar.afkr.spigot.object.PluginSettings;
import main.java.me.avankziar.afkr.spigot.permission.KeyHandler;

public class ARGTop_AfkTime extends ArgumentModule
{
	private AfkR plugin;
	
	public ARGTop_AfkTime(AfkR plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int page = 0;
		if(args.length >= 3)
		{
			if(!args[2].matches("[0-9]+"))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("IllegalArgument")));
				return;
			}
			page = Integer.parseInt(args[2]);
		}
		plugin.getCommandHelper().top(player, "afktime", page, "CmdAfkRecord.Top.HeadlineAfT",
				PluginSettings.settings.getCommands(KeyHandler.TOP_AFKTIME));
	}
}