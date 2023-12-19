package main.java.de.avankziar.afkrecord.spigot.hook;

import java.util.UUID;

import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.handler.PlayerTimesHandler;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends PlaceholderExpansion
{
	private AfkRecord plugin;
	
	public PAPIHook(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean persist()
	{
		return true;
	}
	
	@Override
	public boolean canRegister()
	{
		return true;
	}
	
	@Override
	public String getAuthor()
	{
		return plugin.getDescription().getAuthors().toString();
	}
	
	@Override
	public String getIdentifier()
	{
		return "afkr";
	}
	
	@Override
	public String getVersion()
	{
		return plugin.getDescription().getVersion();
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String identifier)
	{
		if(player == null)
		{
			return "";
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		if(user == null)
		{
			return "";
		}
		UUID uuid = player.getUniqueId();
		String format = "";
		long act = (PlayerTimesHandler.activeTime.containsKey(uuid) ? PlayerTimesHandler.activeTime.get(uuid) : 0);
		long afk = (PlayerTimesHandler.afkTime.containsKey(uuid) ? PlayerTimesHandler.afkTime.get(uuid) : 0);
		switch(identifier)
		{
		case "raw_user_total_alltime":
			return String.valueOf(user.getTotalTime()+act+afk);
		case "user_total_alltime":
			format = 
			(user.getTotalTime()+act+afk > 1000*60*60*24L ? "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") : "") +
			(user.getTotalTime()+act+afk > 1000*60*60L ? "&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") : "") +
			(user.getTotalTime()+act+afk > 1000*60L ? "&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") : "") +
			"&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds");
			return TimeHandler.getRepeatingTime(user.getTotalTime()+act+afk, format);
		case "raw_user_total_activitytime":
			return String.valueOf(user.getActiveTime()+act);
		case "user_total_activitytime":
			format = 
			(user.getActiveTime()+act > 1000*60*60*24L ? "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") : "") +
			(user.getActiveTime()+act > 1000*60*60L ? "&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") : "") +
			(user.getActiveTime()+act > 1000*60L ? "&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") : "") +
			"&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds");
			return TimeHandler.getRepeatingTime(user.getActiveTime()+act, format);
		case "raw_user_total_afktime":
			return String.valueOf(user.getAfkTime()+afk);
		case "user_total_afktime":
			format = 
			(user.getAfkTime()+afk > 1000*60*60*24L ? "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") : "") +
			(user.getAfkTime()+afk > 1000*60*60L ? "&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") : "") +
			(user.getAfkTime()+afk > 1000*60L ? "&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") : "") +
			"&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds");
			return TimeHandler.getRepeatingTime(user.getAfkTime()+afk, format);
		case "user_lastactivity":
			return plugin.getPlayerTimes().formatDate(
					PlayerTimesHandler.lastActivity.containsKey(uuid) ? PlayerTimesHandler.lastActivity.get(uuid)  : user.getLastActivity());
		case "user_lasttimechecked":
			return plugin.getPlayerTimes().formatDate(
					PlayerTimesHandler.lastTimeChecked.containsKey(uuid) ? PlayerTimesHandler.lastTimeChecked.get(uuid) : user.getLastTimeCheck());
		case "user_isafk":
			return getLanguageBoolean(
					PlayerTimesHandler.activeStatus.containsKey(uuid) ? !PlayerTimesHandler.activeStatus.get(uuid) : user.isAFK());
		case "user_isonline":
			return getLanguageBoolean(user.isOnline());
		case "user_30days_activitytime":
			long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*29;
			long sum = (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "activitytime",
					"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeDays);
			return TimeHandler.getRepeatingTime(sum, format);
		case "user_30days_afktime":
			beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*29;
			sum = (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "afktime",
					"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeDays);
			return TimeHandler.getRepeatingTime(sum, format);
		case "user_30days_alltime":
			beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*29;
			sum = (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "alltime",
					"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeDays);
			return TimeHandler.getRepeatingTime(sum, format);
		case "user_365days_activitytime":
			long beforeYear = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*364;
			sum = (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "activitytime",
					"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeYear);
			return TimeHandler.getRepeatingTime(sum, format);
		case "user_365days_afktime":
			beforeYear = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*364;
			sum = (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "afktime",
					"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeYear);
			return TimeHandler.getRepeatingTime(sum, format);
		case "user_365days_alltime":
			beforeYear = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*364;
			sum = (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "alltime",
					"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeYear);
			return TimeHandler.getRepeatingTime(sum, format);
		case "user_toplist_place_activitytime":
			return String.valueOf(plugin.getMysqlHandler().getTopListPlaceI(plugin,
					"`activitytime`", "`activitytime` > ?", user.getActiveTime()));
		case "user_toplist_place_afktime":
			return String.valueOf(plugin.getMysqlHandler().getTopListPlaceI(plugin,
					"`afktime`", "`afktime` > ?", user.getAfkTime()));
		case "user_toplist_place_alltime":
			return String.valueOf(plugin.getMysqlHandler().getTopListPlaceI(plugin,
					"`alltime`", "`alltime` > ?", user.getTotalTime()));
		case "user_toplist_place_activitytime_with_format":
			return plugin.getUtility().getPlaceColor(plugin.getMysqlHandler().getTopListPlaceI(plugin,
					"`activitytime`", "`activitytime` > ?", user.getActiveTime()));
		case "user_toplist_place_afktime_with_format":
			return plugin.getUtility().getPlaceColor(plugin.getMysqlHandler().getTopListPlaceI(plugin,
					"`afktime`", "`afktime` > ?", user.getAfkTime()));
		case "user_toplist_place_alltime_with_format":
			return plugin.getUtility().getPlaceColor(plugin.getMysqlHandler().getTopListPlaceI(plugin,
					"`alltime`", "`alltime` > ?", user.getTotalTime()));
		default:
			break;
		}
		
		return null;
	}
	
	private String getLanguageBoolean(boolean boo)
	{
		String lang = plugin.getAdministration() == null 
				? plugin.getYamlHandler().getConfig().getString("Language", "ENG").toUpperCase() 
				: plugin.getAdministration().getLanguage().toUpperCase();
		if(lang.equals("GER"))
		{
			if(boo)
			{
				return "Ja";
			} else
			{
				return "Nein";
			}
		} else
		{
			if(boo)
			{
				return "Yes";
			} else
			{
				return "No";
			}
		}
	}
}
