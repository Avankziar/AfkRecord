package main.java.de.avankziar.afkrecord.spigot.interfacehub;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;
import main.java.me.avankziar.ifh.spigot.interfaces.PlayerTimes;

public class PlayerTimesAPI implements PlayerTimes
{
	private AfkRecord plugin;
	private final static long SEC = 1000L;
	private final static long MIN = SEC*60;
	private final static long HOUR = MIN*60;
	private final static long DAY = HOUR*24;
	private final static long WEEK = DAY*7;
	private final static long YEAR = DAY*365;
	
	public PlayerTimesAPI(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean addActiveTime(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		user.setActivityTime(user.getActivityTime()+time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				player.getUniqueId().toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(player.getUniqueId(), player.getName(), today, 0, time, 0);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setActivityTime(tr.getActivityTime()+time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				player.getUniqueId().toString(), today);
		return true;
	}

	@Override
	public boolean addActiveTime(OfflinePlayer player, int years, int weeks, int days, int hours, int minutes, int seconds)
	{
		long time = years*YEAR + weeks*WEEK + days*DAY + hours*HOUR + minutes*MIN + seconds*SEC;
		return addActiveTime(player, time);
	}

	@Override
	public boolean addInactiveTime(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		user.setAfkTime(user.getAfkTime()+time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				player.getUniqueId().toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(player.getUniqueId(), player.getName(), today, 0, 0, time);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setAfkTime(tr.getAfkTime()+time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				player.getUniqueId().toString(), today);
		return true;
	}

	@Override
	public boolean addInactiveTime(OfflinePlayer player, int years, int weeks, int days, int hours, int minutes, int seconds)
	{
		long time = years*YEAR + weeks*WEEK + days*DAY + hours*HOUR + minutes*MIN + seconds*SEC;
		return addInactiveTime(player, time);
	}

	@Override
	public boolean addTotalTime(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		user.setAllTime(user.getAllTime()+time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				player.getUniqueId().toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(player.getUniqueId(), player.getName(), today, time, 0, 0);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setAllTime(tr.getAllTime()+time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				player.getUniqueId().toString(), today);
		return true;
	}

	@Override
	public boolean addTotalTime(OfflinePlayer player, int years, int weeks, int days, int hours, int minutes, int seconds)
	{
		long time = years*YEAR + weeks*WEEK + days*DAY + hours*HOUR + minutes*MIN + seconds*SEC;
		return addTotalTime(player, time);
	}

	@Override
	public boolean createAccount(OfflinePlayer player)
	{
		if(hasAccount(player))
		{
			return false;
		}
		long now = System.currentTimeMillis();
		PluginUser user = new PluginUser(player.getUniqueId(), player.getName(), now, 0, 0, 0, now, false, false, 0);
		plugin.getMysqlHandler().create(Type.PLUGINUSER, user);
		return true;
	}

	@Override
	public boolean deleteAccount(OfflinePlayer player)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		plugin.getMysqlHandler().deleteData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}

	@Override
	public long getActiveTime(OfflinePlayer player)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getActivityTime() : 0;
	}

	@Override
	public long getActiveTime(OfflinePlayer player, int days)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*(days-1);
		return (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "activitytime",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeDays);
	}

	@Override
	public long getInactiveTime(OfflinePlayer player)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getAfkTime() : 0;
	}

	@Override
	public long getInactiveTime(OfflinePlayer player, int days)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*(days-1);
		return (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "afktime",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeDays);
	}

	@Override
	public long getLastActivity(OfflinePlayer player)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getLastActivity() : 0;
	}

	@Override
	public String getName()
	{
		return plugin.getDescription().getFullName();
	}

	@Override
	public long getTotalTime(OfflinePlayer player)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getAllTime() : 0;
	}

	@Override
	public long getTotalTime(OfflinePlayer player, int days)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*(days-1);
		return (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "alltime",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", player.getUniqueId().toString(), beforeDays);
	}

	@Override
	public long getVacation(OfflinePlayer player)
	{
		if(!hasAccount(player))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getVacationTime() : 0;
	}

	@Override
	public String getVersion()
	{
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean hasAccount(OfflinePlayer player)
	{
		if(plugin.getMysqlHandler().exist(Type.PLUGINUSER, "`player_uuid` = ?", player.getUniqueId().toString()))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isActiveTimeEnabled()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public boolean isInactiveTimeEnabled()
	{
		return true;
	}

	@Override
	public boolean isOnline(OfflinePlayer player)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.isOnline() : false;
	}

	@Override
	public boolean isTotalTimeEnabled()
	{
		return true;
	}

	@Override
	public boolean setActive(OfflinePlayer player)
	{
		if(player.isOnline())
		{
			Player p = player.getPlayer();
			plugin.getUtility().save(p, true, false, false, false);
		}
		return true;
	}

	@Override
	public boolean setInactive(OfflinePlayer player)
	{
		if(player.isOnline())
		{
			Player p = player.getPlayer();
			plugin.getUtility().save(p, false, true, false, false);
		}
		return true;
	}

	@Override
	public boolean setOnline(OfflinePlayer player, boolean online)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		user.setOnline(online);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}

	@Override
	public boolean setVacation(OfflinePlayer player, long time)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		user.setVacationTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}

	@Override
	public boolean setVacation(OfflinePlayer player, int year, int month, int day, int hour, int minute, int second)
	{
		long time = LocalDateTime.of(year, month, day, hour, minute, second, 0).toEpochSecond(OffsetDateTime.now().getOffset());
		return setVacation(player, time);
	}

	@Override
	public boolean supportDailyLog()
	{
		return true;
	}

	@Override
	public String getAuthors()
	{
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public boolean isActive(OfflinePlayer player)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? !user.isAFK() : true;
	}

	@Override
	public boolean isVacacation(OfflinePlayer player)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		return (user != null) ? user.getVacationTime() > System.currentTimeMillis() : false;
	}

	@Override
	public boolean setActiveTime(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		user.setActivityTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}

	@Override
	public boolean setInactiveTime(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		user.setAfkTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}

	@Override
	public boolean setLastActivity(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		user.setLastActivity(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}

	@Override
	public boolean setTotalTime(OfflinePlayer player, long time)
	{
		if(!hasAccount(player))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		user.setAllTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		return true;
	}	
}