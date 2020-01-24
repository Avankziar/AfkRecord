package main.java.de.avankziar.afkrecord.spigot.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CMDAfk implements CommandExecutor
{
	private AfkRecord plugin;
	
	public CMDAfk(AfkRecord plugin)
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
		Player player = (Player) sender;
		String language = plugin.getYamlHandler().get().getString("language");
		if(args.length==0)
		{
			if(!player.hasPermission("afkrecord.cmd.afk"))
			{
				player.spigot().sendMessage(plugin.getUtility().tcl(plugin.getYamlHandler().getL().getString(language+".msg01")));
				return false;
			}
			User u = User.getUser(player);
			if(u!=null)
			{
				if(u.isIsafk()==false)
				{
					plugin.getUtility().softSave(player, false, false, true);
					plugin.getMysqlInterface().updateDataI(player, true, "isafk");
				} else
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(language+".CMDAfk.msg02")));
				}
				return true;
			} else
			{
				if((boolean) plugin.getMysqlInterface().getDataI(player, "isafk", "player_uuid")==false)
				{
					plugin.getUtility().softSave(player, false, false, true);
					plugin.getMysqlInterface().updateDataI(player, true, "isafk");
				} else
				{
					player.spigot().sendMessage(plugin.getUtility().tcl(
							plugin.getYamlHandler().getL().getString(language+".CMDAfk.msg02")));
				}
				return true;
			}
		} else
		{
			TextComponent msg = plugin.getUtility().tc(
					plugin.getUtility().tl(plugin.getYamlHandler().getL().getString(language+".CMDAfkRecord.msg01")));
			msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkrecord"));
			player.spigot().sendMessage(msg);
		}
		return false;
	}
}
