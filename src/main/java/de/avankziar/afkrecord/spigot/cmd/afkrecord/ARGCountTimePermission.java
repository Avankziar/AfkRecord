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
import main.java.de.avankziar.afkrecord.spigot.object.ConvertHandler;
import main.java.de.avankziar.afkrecord.spigot.object.PluginSettings;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;
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
						uuids.add(off.getUniqueId().toString());
					}
				}
				ArrayList<RAMUser> ram = new ArrayList<>();
				for(String uuid : uuids)
				{
					final PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid);
					if(user == null)
					{
						continue;
					}
					ArrayList<TimeRecord> a = new ArrayList<>();
					try
					{
						a = ConvertHandler.convertListII(
								plugin.getMysqlHandler().getList(Type.TIMERECORD,
										"`timestamp_unix`", true, 0, days, "`player_uuid` = ?", uuid));
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					long act = 0;
					long afkt = 0;
					long allt = 0;
					for(TimeRecord tr : a)
					{
						act += tr.getActivityTime();
						afkt += tr.getAfkTime();
						allt += tr.getAllTime();
					}
					final String name = user.getPlayerName();
					ram.add(new RAMUser(uuid, name, act, allt, afkt, user.getLastActivity()));
				}
				ArrayList<BaseComponent> bc = new ArrayList<>();
				for(RAMUser user : ram)
				{
					TextComponent tc = ChatApi.apiChat(getColor(user.name, user.activitytime)+" &r",
							Action.RUN_COMMAND, PluginSettings.settings.getCommands(KeyHandler.COUNTTIME)+days+" "+user.name,
							HoverEvent.Action.SHOW_TEXT,
							String.join("~!~", 
									replacer(plugin.getYamlHandler().getLang().getStringList("CmdAfkRecord.CountTimePerm.Hover"),
											user.afktime, user.alltime, user.activitytime, user.lastactivity)));
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
					.replace("%lastactivity%",  TimeHandler.getDateTime(lastActivity));
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