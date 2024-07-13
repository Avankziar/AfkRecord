package main.java.me.avankziar.afkr.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.commands.tree.ArgumentConstructor;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.cmd.tree.ArgumentModule;
import main.java.me.avankziar.afkr.spigot.object.PluginSettings;
import main.java.me.avankziar.afkr.spigot.permission.KeyHandler;

public class ARGTop extends ArgumentModule
{
	private AfkR plugin;
	
	public ARGTop(AfkR plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int page = 0;
		plugin.getCommandHelper().top(player, "alltime", page, "CmdAfkRecord.Top.HeadlineAlT",
				PluginSettings.settings.getCommands(KeyHandler.TOP_ALLTIME));
	}
}