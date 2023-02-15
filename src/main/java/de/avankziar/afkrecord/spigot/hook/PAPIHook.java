package main.java.de.avankziar.afkrecord.spigot.hook;

import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
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
		return AfkRecord.pluginName.toLowerCase();
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
		String format = "";
		switch(identifier)
		{
		case "user_total_alltime":
			format = 
			(user.getTotalTime() > 1000*60*60*24L ? "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") : "") +
			(user.getTotalTime() > 1000*60*60*5L ? "&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") : "") +
			(user.getTotalTime() > 1000*60L ? "&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") : "") +
			(user.getTotalTime() > 1000L ? "&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds") : "");
			return TimeHandler.getRepeatingTime(user.getTotalTime(), format);
		case "user_total_activitytime":
			format = 
			(user.getActiveTime() > 1000*60*60*24L ? "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") : "") +
			(user.getActiveTime() > 1000*60*60*5L ? "&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") : "") +
			(user.getActiveTime() > 1000*60L ? "&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") : "") +
			(user.getActiveTime() > 1000L ? "&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds") : "");
			return TimeHandler.getRepeatingTime(user.getActiveTime(), format);
		case "user_total_afktime":
			format = 
			(user.getAfkTime() > 1000*60*60*24L ? "&fdd"+plugin.getYamlHandler().getLang().getString("Time.Days") : "") +
			(user.getAfkTime() > 1000*60*60*5L ? "&fHH"+plugin.getYamlHandler().getLang().getString("Time.Hours") : "") +
			(user.getAfkTime() > 1000*60L ? "&fmm"+plugin.getYamlHandler().getLang().getString("Time.Minutes") : "") +
			(user.getAfkTime() > 1000L ? "&fss"+plugin.getYamlHandler().getLang().getString("Time.Seconds") : "");
			return TimeHandler.getRepeatingTime(user.getAfkTime(), format);
		case "user_lastactivity":
			return plugin.getPlayerTimes().formatDate(user.getLastActivity());
		case "user_lasttimechecked":
			return plugin.getPlayerTimes().formatDate(user.getLastTimeCheck());
		case "user_isafk":
			return getLanguageBoolean(user.isAFK());
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
		if(plugin.getYamlHandler().getConfig().getString("Language").equals("GER"))
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
