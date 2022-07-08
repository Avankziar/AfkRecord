package main.java.de.avankziar.afkrecord.spigot.cmd.afkrecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentConstructor;
import main.java.de.avankziar.afkrecord.spigot.cmd.tree.ArgumentModule;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginSettings;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.permission.KeyHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGCountTimePermission extends ArgumentModule
{
	private AfkRecord plugin;
	private static boolean inProgress = false;
	private static long H = 1000*60*60;
	private static long sixH = 1000*60*60*6;
	private static long twelveH = 1000*60*60*12;
	private static long twentyfourH = 1000*60*60*24;
	private static long oneW = 1000*60*60*24*7;
	
	public ARGCountTimePermission(AfkRecord plugin, ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		if(inProgress)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.InProgress")));
			return;
		}
		if(!args[1].matches("[0-9]+"))
		{
			player.spigot().sendMessage(
					ChatApi.tctl(plugin.getYamlHandler().getLang().getString("IllegalArgument")));
			return;
		}
		final int days = Integer.parseInt(args[1]);
		if(plugin.getPerms() == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoVault")));
			return;
		}
		final String perm = args[2];
		player.sendMessage(ChatApi.tl(
				plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.WIP")
				.replaceAll("%perm%", perm)
				.replaceAll("%days%", String.valueOf(days))));
		inProgress = true;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				ArrayList<String> uuids = new ArrayList<>();
				for(OfflinePlayer off : Bukkit.getOfflinePlayers())
				{
					if(plugin.getPerms().playerHas(null, off, perm))
					{
						if(!uuids.contains(off.getUniqueId().toString()))
						{
							uuids.add(off.getUniqueId().toString());
						}
					}
				}
				ArrayList<BaseComponent> bc = new ArrayList<>();
				long before = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()))
						- (days-1)*1000L*60*60*24;
				for(String uuid : uuids)
				{
					final PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid);
					if(user == null)
					{
						continue;
					}
					long act = plugin.getMysqlHandler().getSumII(plugin, "`player_uuid`", "`activitytime`",
							"`player_uuid` = ? AND `timestamp_unix` >= ?", user.getUUID().toString(), before);
					long afkt = plugin.getMysqlHandler().getSumII(plugin, "`player_uuid`", "`afktime`",
							"`player_uuid` = ? AND `timestamp_unix` >= ?", user.getUUID().toString(), before);
					long allt = plugin.getMysqlHandler().getSumII(plugin, "`player_uuid`", "`alltime`",
							"`player_uuid` = ? AND `timestamp_unix` >= ?", user.getUUID().toString(), before);
					//SELECT `player_name`, SUM(`activitytime`) as ergebnis FROM `SecretCraftAfkDateList` WHERE `player_name` = 'Avankziar' AND `timestamp_unix` >= '0' GROUP BY `player_name`
					//SELECT `player_name`, SUM(`activitytime`) as ergebnis FROM `SecretCraftAfkDateList` WHERE `player_name` = 'loki1818' AND `timestamp_unix` >= '0' GROUP BY `player_name`
					/*
					 * 38422214 ac 10,6 h
					 * 44539016 all 12,3 h
					 * 6116802 afk 1,69 h
					 */
					TextComponent tc = ChatApi.apiChat(getColor(user.getPlayerName(), act)+" &r",
							Action.RUN_COMMAND, PluginSettings.settings.getCommands(KeyHandler.COUNTTIME)+days+" "+user.getPlayerName(),
							HoverEvent.Action.SHOW_TEXT,
							String.join("~!~", 
									replacer(plugin.getYamlHandler().getLang().getStringList("CmdAfkRecord.CountTimePerm.Hover"),
											afkt, allt, act, user.getLastActivity())));
					bc.add(tc);
				}
				inProgress = false;
				TextComponent tc = ChatApi.tctl("");
				tc.setExtra(bc);
				player.sendMessage(ChatApi.tl(
						plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Headline")
						.replaceAll("%perm%", perm)
						.replaceAll("%days%", String.valueOf(days))));
				player.spigot().sendMessage(tc);
			}
		}.runTaskAsynchronously(plugin);
	}
	
	public String getColor(String s, long active)
	{
		String a = "";
		if(active < H)
		{
			a += plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Colors.Under1H")+s;
		} else if(active < sixH)
		{
			a += plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Colors.Under6H")+s;
		} else if(active < twelveH)
		{
			a += plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Colors.Under12H")+s;
		} else if(active < twentyfourH)
		{
			a += plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Colors.Under24H")+s;
		} else if(active < oneW)
		{
			a += plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Colors.Under7D")+s;
		} else
		{
			a += plugin.getYamlHandler().getLang().getString("CmdAfkRecord.CountTimePerm.Colors.Over7D")+s;
		}
		return a;
	}
	
	public ArrayList<String> replacer(List<String> list, long afk, long all, long active, long lastActivity)
	{
		ArrayList<String> array = new ArrayList<>();
		String format = "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") +
				"&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") +
				"&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") +
				"&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds");
		for(String s : list)
		{
			String a = s.replace("%afktime%", TimeHandler.getRepeatingTime(afk, format))
					.replace("%alltime%",  TimeHandler.getRepeatingTime(all, format))
					.replace("%activitytime%",  TimeHandler.getRepeatingTime(active, format))
					.replace("%lastactivity%",  plugin.getPlayerTimes().formatDate(lastActivity));
			array.add(a);
		}
		return array;
	}
	
	public class RAMUser
	{
		String uuid;
		String name;
		long activitytime;
		long alltime;
		long afktime;
		long lastactivity;
		
		public RAMUser(String uuid, String name, long activitytime, long alltime, long afktime, long lastactivity)
		{
			this.uuid = uuid;
			this.name = name;
			this.activitytime = activitytime;
			this.alltime = alltime;
			this.afktime = afktime;
			this.lastactivity = lastactivity;
		}
	}
}