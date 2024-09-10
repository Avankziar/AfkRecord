package main.java.me.avankziar.afkr.spigot.cmd;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.afkr.general.commands.tree.CommandConstructor;
import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.assistance.ChatApi;
import main.java.me.avankziar.afkr.spigot.handler.PlayerTimesHandler;

public class AfkCommandExecutor implements CommandExecutor
{
	private AfkR plugin;
	
	public AfkCommandExecutor(AfkR plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			return false;
		}
		final Player player = (Player) sender;
		final UUID uuid = player.getUniqueId();
		String afkreason = null;
		if(args.length > 0)
		{
			afkreason = String.join(" ", args);
		}
		if(PlayerTimesHandler.playerWhoBypassAfkTracking.contains(uuid))
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.Bypass.YouAredBypass")));
		} else
		{
			plugin.getPlayerTimes().saveRAM(uuid, !plugin.getPlayerTimes().isActive(uuid), false, false);
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		if(user != null)
		{
			user.setAfkReason(afkreason);
			plugin.getMysqlHandler().updateData(MysqlType.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		}
		return true;
	}
}