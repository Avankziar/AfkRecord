package main.java.de.avankziar.afkrecord.spigot.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.object.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MultipleCommandExecutor implements CommandExecutor 
{
	private AfkRecord plugin;
	
	public MultipleCommandExecutor(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		// Checks if the label is one of yours.
		String language = plugin.getUtility().getLanguage();
		if (cmd.getName().equalsIgnoreCase("afkr") || cmd.getName().equalsIgnoreCase("afkrecord")) 
		{		
			if (!(sender instanceof Player)) 
			{
				AfkRecord.log.info("/afkr is only for Player!");
				return false;
			}
			Player player = (Player) sender;
			if (args.length == 0) 
			{
				plugin.getCommandHelper().afkr(player); //Info Command
				return false;
			}
			if (AfkRecord.afkrarguments.containsKey(args[0])) 
			{
				CommandModule mod = AfkRecord.afkrarguments.get(args[0]);
				//Abfrage ob der Spieler die Permission hat
				if (player.hasPermission(mod.permission)) 
				{
					//Abfrage, ob der Spieler in den min und max Argumenten Bereich ist.
					if(args.length >= mod.minArgs && args.length <= mod.maxArgs)
					{
						mod.run(sender, args);
					} else
					{
						///Deine Eingabe ist fehlerhaft, klicke hier auf den Text um &cweitere Infos zu bekommen!
						player.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getL().getString(language+".InputIsWrong"),
								ClickEvent.Action.RUN_COMMAND, "/afkr"));
						return false;
					}
				} else 
				{
					///Du hast dafür keine Rechte!
					player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".NoPermission")));
					return false;
				}
			} else 
			{
				///Deine Eingabe ist fehlerhaft, klicke hier auf den Text um &cweitere Infos zu bekommen!
				player.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getL().getString(language+".InputIsWrong"),
						ClickEvent.Action.RUN_COMMAND, "/afkr"));
				return false;
			}
		} else if(cmd.getName().equalsIgnoreCase("afk"))
		{
			if(!(sender instanceof Player))
	    	{
				AfkRecord.log.info("/afk is only for Player!");
				return false;
	    	}
			Player player = (Player) sender;
			if(args.length==0)
			{
				if(!player.hasPermission("afkrecord.cmd.afk"))
				{
					player.spigot().sendMessage(ChatApi.tctl(
							plugin.getYamlHandler().getL().getString(language+".NoPermission")));
					return false;
				}
				User u = User.getUser(player);
				if(u!=null)
				{
					if(u.isIsafk()==false)
					{
						plugin.getUtility().softSave(player, false, false, true);
						plugin.getMysqlHandler().updateDataI(player, true, "isafk");
					} else
					{
						player.spigot().sendMessage(ChatApi.tctl(
								plugin.getYamlHandler().getL().getString(language+".CmdAfk.AlreadyAfk")));
					}
					return true;
				} else
				{
					if((boolean) plugin.getMysqlHandler().getDataI(player, "isafk", "player_uuid")==false)
					{
						plugin.getUtility().softSave(player, false, false, true);
						plugin.getMysqlHandler().updateDataI(player, true, "isafk");
					} else
					{
						player.spigot().sendMessage(ChatApi.tctl(
								plugin.getYamlHandler().getL().getString(language+".CmdAfk.AlreadyAfk")));
					}
					return true;
				}
			} else
			{
				TextComponent msg = ChatApi.tc(
						ChatApi.tl(plugin.getYamlHandler().getL().getString(language+".InputIsWrong")));
				msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkr"));
				player.spigot().sendMessage(msg);
			}
			return false;
		}
		return false;
	}
}