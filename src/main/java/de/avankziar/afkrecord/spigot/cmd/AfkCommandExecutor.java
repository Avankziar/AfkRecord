package main.java.de.avankziar.afkrecord.spigot.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
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
		Player player = (Player) sender;
		plugin.getUtility().save(player, false, true, false);
		return false;
	}
}