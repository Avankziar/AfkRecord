package main.java.de.avankziar.afkrecord.spigot.command.afkrecord;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;

public class ARGGetAfk extends CommandModule
{
	private AfkRecord plugin;
	
	public ARGGetAfk(AfkRecord plugin)
	{
		super("getafk","afkrecord.cmd.afkrecord.getafk",AfkRecord.afkrarguments,1,1);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		if(plugin.getYamlHandler().get().getBoolean("Bungee", false))
		{
			String µ = "µ";
			String Category = "getafk";
	        String PlayerUUID = player.getUniqueId().toString();
			String message = Category+µ+PlayerUUID;
	        ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(stream);
	        try {
				out.writeUTF(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        player.sendPluginMessage(plugin, "afkrecord:afkrecordin", stream.toByteArray());
	        return;
		 } else
		 {
			 String allPlayerUUID = "";
			 for(Player all : plugin.getServer().getOnlinePlayers())
			 {
			   	allPlayerUUID+=all.getUniqueId().toString()+"@";
			 }
			 plugin.getCommandHelper().getafk(player,allPlayerUUID);
			 return;
		 }
	}
}