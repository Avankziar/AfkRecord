package main.java.me.avankziar.afkr.spigot.ifh;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.spigot.AfkR;
import me.avankziar.ifh.general.interfaces.PlayerTimes;

public class PlayerTimesAPI implements PlayerTimes
{
	private AfkR plugin;
	
	public PlayerTimesAPI(AfkR plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean addActiveTime(UUID uuid, long... time)
	{
		return plugin.getPlayerTimes().addActiveTime(uuid, time);
	}

	@Override
	public boolean addInactiveTime(UUID uuid, long... time)
	{
		return plugin.getPlayerTimes().addInactiveTime(uuid, time);
	}

	@Override
	public boolean addTotalTime(UUID uuid, long... time)
	{
		return plugin.getPlayerTimes().addTotalTime(uuid, time);
	}

	@Override
	public boolean createAccount(UUID uuid, String playername)
	{
		return plugin.getPlayerTimes().createAccount(uuid, playername);
	}

	@Override
	public boolean deleteAccount(UUID uuid)
	{
		return plugin.getPlayerTimes().deleteAccount(uuid);
	}

	@Override
	public long getActiveTime(UUID uuid)
	{
		return plugin.getPlayerTimes().getActiveTime(uuid);
	}

	@Override
	public long getActiveTime(UUID uuid, int days)
	{
		return plugin.getPlayerTimes().getActiveTime(uuid, days);
	}

	@Override
	public long getInactiveTime(UUID uuid)
	{
		return plugin.getPlayerTimes().getInactiveTime(uuid);
	}

	@Override
	public long getInactiveTime(UUID uuid, int days)
	{
		return plugin.getPlayerTimes().getInactiveTime(uuid, days);
	}

	@Override
	public long getLastActivity(UUID uuid)
	{
		return plugin.getPlayerTimes().getLastActivity(uuid);
	}

	@Override
	public long getTotalTime(UUID uuid)
	{
		return plugin.getPlayerTimes().getTotalTime(uuid);
	}

	@Override
	public long getTotalTime(UUID uuid, int days)
	{
		return plugin.getPlayerTimes().getTotalTime(uuid, days);
	}

	@Override
	public long getVacation(UUID uuid)
	{
		return plugin.getPlayerTimes().getVacation(uuid);
	}

	@Override
	public boolean hasAccount(UUID uuid)
	{
		return plugin.getPlayerTimes().hasAccount(uuid);
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
	public boolean isOnline(UUID uuid)
	{
		return plugin.getPlayerTimes().isOnline(uuid);
	}

	@Override
	public boolean setActive(UUID uuid)
	{
		new BukkitRunnable()
		{
			
			@Override
			public void run()
			{
				plugin.getPlayerTimes().setActive(uuid);
			}
		}.runTaskAsynchronously(plugin);
		return true;
	}

	@Override
	public boolean setInactive(UUID uuid)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				plugin.getPlayerTimes().setInactive(uuid);
			}
		}.runTaskAsynchronously(plugin);
		return true;
	}

	@Override
	public boolean setOnline(UUID uuid, boolean online)
	{
		return plugin.getPlayerTimes().setOnline(uuid, online);
	}

	@Override
	public boolean setVacation(UUID uuid, long time)
	{
		return plugin.getPlayerTimes().setVacation(uuid, time);
	}

	@Override
	public boolean setVacation(UUID uuid, int year, int month, int day, int hour, int minute, int second)
	{
		long time = LocalDateTime.of(year, month, day, hour, minute, second, 0).toEpochSecond(OffsetDateTime.now().getOffset());
		return setVacation(uuid, time);
	}

	@Override
	public boolean isActive(UUID uuid)
	{
		return plugin.getPlayerTimes().isActive(uuid);
	}

	@Override
	public boolean isVacacation(UUID uuid)
	{
		return plugin.getPlayerTimes().isVacacation(uuid);
	}

	@Override
	public boolean setActiveTime(UUID uuid, long time)
	{
		return plugin.getPlayerTimes().setActiveTime(uuid, time);
	}

	@Override
	public boolean setInactiveTime(UUID uuid, long time)
	{
		return plugin.getPlayerTimes().setInactiveTime(uuid, time);
	}

	@Override
	public boolean setLastActivity(UUID uuid, long time)
	{
		return plugin.getPlayerTimes().setLastActivity(uuid, time);
	}

	@Override
	public boolean setTotalTime(UUID uuid, long time)
	{
		return plugin.getPlayerTimes().setTotalTime(uuid, time);
	}

	@Override
	public boolean isTotalTimeEnabled()
	{
		return true;
	}

	@Override
	public boolean supportDailyLog()
	{
		return true;
	}

	@Override
	public String formatDate(long time)
	{
		return plugin.getPlayerTimes().formatDate(time);
	}

	@Override
	public String formatDate(long time, boolean useYears, boolean useMonths, boolean useDays,
			boolean useHours, boolean useMinutes, boolean useSeconds)
	{
		return plugin.getPlayerTimes().formatTimePeriod(time, useYears, useDays, useHours, useMinutes, useSeconds);
	}

	@Override
	public String formatTimePeriod(long time)
	{
		return plugin.getPlayerTimes().formatTimePeriod(time, true, true);
	}

	@Override
	public String formatTimePeriod(long time, boolean useYears, boolean useDays, boolean useHours, boolean useMinutes, boolean useSeconds)
	{
		return plugin.getPlayerTimes().formatTimePeriod(time, useYears, useDays, useHours, useMinutes, useSeconds);
	}
	
	@Override
	public String getAfkReason(UUID uuid)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return user != null ? user.getAfkReason() : null;
	}
}