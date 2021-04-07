package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;

public class ARGConvert extends ArgumentModule
{
	private AfkRecord plugin;
	
	public ARGConvert(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int count= plugin.getMysqlHandler().getCountII(plugin, "`id`", "`timestamp_unix` = ?", 0);
		long time = (count/60)*1000;
		if(args.length == 2)
		{
			if(args[1].equalsIgnoreCase("bestätigen") || args[1].equalsIgnoreCase("confirm"))
			{
				time += System.currentTimeMillis();
				plugin.getMysqlHandler().startConvert(player, count);
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Convert.Start")
						.replace("%time%", TimeHandler.getDateTime(time))));
				return;
			}
		}
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Convert.PleaseConfirm")
				.replace("%count%", String.valueOf(count))
				.replace("%time%", TimeHandler.getRepeatingTime(time, "HH:mm:ss"))));
	}
}
