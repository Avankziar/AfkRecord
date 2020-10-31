package main.java.de.avankziar.afkrecord.spigot.command.afkrecord;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;
import main.java.de.avankziar.afkrecord.spigot.object.User;

public class ARGTime extends CommandModule
{
	private AfkRecord plugin;
	
	public ARGTime(AfkRecord plugin)
	{
		super("time","afkrecord.cmd.afkrecord.time.self",AfkRecord.afkrarguments,1,2);
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String language = plugin.getUtility().getLanguage();
		if(args.length==1)
		{
			User u = User.getUser(player);
			plugin.getCommandHelper().time(player, player, u);
			return;
		} else if(args.length==2)
		{
			if(!player.hasPermission("afkrecord.cmd.afkrecord.time.other"))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getL().getString(language+".NoPermission")));
				return;
			}
			if(!plugin.getMysqlHandler().hasAccount(Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString()))
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getL().getString(language+".PlayerNotExist")));
				return;
			}
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
			User u = User.getUser(target);
			plugin.getCommandHelper().time(player, target, u);
			return;
		}
	}
}