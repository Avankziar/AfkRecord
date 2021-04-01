package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;

public class ARGGetAfk extends ArgumentModule
{
	private AfkRecord plugin;
	
	public ARGGetAfk(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		 String allPlayerUUID = "";
		 for(Player all : plugin.getServer().getOnlinePlayers())
		 {
		   	allPlayerUUID+=all.getUniqueId().toString()+"@";
		 }
		 plugin.getCommandHelper().getafk(player,allPlayerUUID);
		 return;
	}
}