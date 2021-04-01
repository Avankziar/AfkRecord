package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;
import main.java.de.avankziar.afkrecord.spigot.object.PluginSettings;
import main.java.de.avankziar.afkrecord.spigot.permission.KeyHandler;

public class ARGTop extends ArgumentModule
{
	private AfkRecord plugin;
	
	public ARGTop(AfkRecord plugin, ArgumentConstructor argumentConstructor)
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