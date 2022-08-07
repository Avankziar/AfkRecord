package main.java.de.avankziar.afkrecord.bungee.ifh;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;
import main.java.de.avankziar.afkrecord.bungee.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;
import main.java.me.avankziar.ifh.general.interfaces.PlayerTimes;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerTimesProvider implements PlayerTimes
{
	private AfkRecord plugin;
	
	public PlayerTimesProvider(AfkRecord plugin)
	{
		this.plugin = plugin;
	}	

	private long addUpTime(long...time)
	{
		long addUp = 0;
		for(long t : time)
		{
			addUp += t;
		}
		return addUp;
	}
	
	private String getPlayerName(UUID uuid)
	{
		return ((PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString())).getPlayerName();
	}

	@Override
	public boolean addActiveTime(UUID uuid, long... time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		long t = addUpTime(time);
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		user.setActiveTime(user.getActiveTime()+t);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(uuid, getPlayerName(uuid), today, 0, t, 0);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setActiveTime(tr.getActiveTime()+t);
		plugin.getMysqlHandler().updateData(Type.TIMERECORD, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		return true;
	}

	@Override
	public boolean addInactiveTime(UUID uuid, long... time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		long t = addUpTime(time);
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		user.setAfkTime(user.getAfkTime()+t);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(uuid, getPlayerName(uuid), today, 0, 0, t);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setAfkTime(tr.getAfkTime()+t);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		return true;
	}

	@Override
	public boolean addTotalTime(UUID uuid, long... time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		long t = addUpTime(time);
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		user.setTotalTime(user.getTotalTime()+t);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(uuid, getPlayerName(uuid), today, t, 0, 0);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setTotalTime(tr.getTotalTime()+t);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		return true;
	}

	@Override
	public boolean createAccount(UUID uuid, String playername)
	{
		long now = System.currentTimeMillis();
		PluginUser user = new PluginUser(uuid, playername, now, 0, 0, 0, now, false, false, 0);
		plugin.getMysqlHandler().create(Type.PLUGINUSER, user);
		return true;
	}

	@Override
	public boolean deleteAccount(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		plugin.getMysqlHandler().deleteData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return true;
	}

	@Override
	public long getActiveTime(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getActiveTime() : 0;
	}

	@Override
	public long getActiveTime(UUID uuid, int days)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*(days-1);
		return (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "activitytime",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", uuid.toString(), beforeDays);
	}

	@Override
	public long getInactiveTime(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getAfkTime() : 0;
	}

	@Override
	public long getInactiveTime(UUID uuid, int days)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*(days-1);
		return (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "afktime",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", uuid.toString(), beforeDays);
	}

	@Override
	public long getLastActivity(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getLastActivity() : 0;
	}

	@Override
	public long getTotalTime(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getTotalTime() : 0;
	}

	@Override
	public long getTotalTime(UUID uuid, int days)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		long beforeDays = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis())) - 1000L*60*60*24*(days-1);
		return (long) plugin.getMysqlHandler().getSumII(plugin, "player_uuid", "alltime",
				"`player_uuid` = ? AND `timestamp_unix` >= ?", uuid.toString(), beforeDays);
	}

	@Override
	public long getVacation(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getVacationTime() : 0;
	}

	@Override
	public boolean hasAccount(UUID uuid)
	{
		if(plugin.getMysqlHandler().exist(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString()))
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
	public boolean isOnline(UUID uuid)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.isOnline() : false;
	}

	@Override
	public boolean setActive(UUID uuid)
	{
		softsave(true);
		return true;
	}

	@Override
	public boolean setInactive(UUID uuid)
	{
		softsave(false);
		return true;
	}

	@Override
	public boolean setOnline(UUID uuid, boolean online)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		if(user == null)
		{
			return false;
		}
		user.setOnline(online);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return true;
	}

	@Override
	public boolean setVacation(UUID uuid, long time)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		if(user == null)
			return false;
		user.setVacationTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return true;
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
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		return (user != null) ? !user.isAFK() : true;
	}

	@Override
	public boolean isVacacation(UUID uuid)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getVacationTime() > System.currentTimeMillis() : false;
	}

	@Override
	public boolean setActiveTime(UUID uuid, long time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		user.setActiveTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return true;
	}

	@Override
	public boolean setInactiveTime(UUID uuid, long time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		user.setAfkTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return true;
	}

	@Override
	public boolean setLastActivity(UUID uuid, long time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		user.setLastActivity(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return true;
	}

	@Override
	public boolean setTotalTime(UUID uuid, long time)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		user.setTotalTime(time);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return true;
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
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"));
	}

	@Override
	public String formatDate(long time, boolean useYears, boolean useMonths, boolean useDays,
			boolean useHours, boolean useMinutes, boolean useSeconds)
	{
		String t = "";
		if(useDays)
			t += "dd";
		if(useDays && useMonths)
			t += ".";
		if(useMonths)
			t += "MM";
		if(useMonths && useYears)
			t += ".";
		if((useDays || useMonths || useYears) && (useHours || useMinutes || useSeconds))
			t += " ";
		if(useHours)
			t += "HH";
		if(useHours && useMinutes)
			t += ":";
		if(useMinutes)
			t += "mm";
		if(useMinutes && useSeconds)
			t += ":";
		if(useSeconds)
			t += "ss";
		t = t.strip();
		return t;
	}

	@Override
	public String formatTimePeriod(long time)
	{
		long ll = time;
		String year = "";
		long y = Math.floorDiv(ll, YEAR);
		year += String.valueOf(y);
		ll = ll - y*YEAR;
		
		String day = "";
		long d = Math.floorDiv(ll, DAY);
		day += String.valueOf(d);
		ll = ll - d*DAY;
		
		String hour = "";
		long H = Math.floorDiv(ll, HOUR);
		if(H < 10)
		{
			hour += String.valueOf(0);
		}
		hour += String.valueOf(H);
		ll = ll - H*HOUR;
		
		long m = Math.floorDiv(ll, MIN);
		String min = "";
		if(m < 10)
		{
			min += String.valueOf(0);
		}
		min += String.valueOf(m);
		ll = ll - m*MIN;
		
		long s = Math.floorDiv(ll, SEC);
		String sec = "";
		if(s < 10)
		{
			sec += String.valueOf(0);
		}
		sec += String.valueOf(s);
		return plugin.getYamlHandler().getLang().getString("TimeFormat.Year").replace("%value%", year)
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Day").replace("%value%", day)
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Hour").replace("%value%", hour)
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Minute").replace("%value%", min)
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
				+ plugin.getYamlHandler().getLang().getString("TimeFormat.Second").replace("%value%", sec);
	}

	@Override
	public String formatTimePeriod(long time, boolean useYears, boolean useDays, boolean useHours, boolean useMinutes, boolean useSeconds)
	{
		StringBuilder sb = new StringBuilder();
		long ll = time;
		if(useYears)
		{
			long y = Math.floorDiv(ll, YEAR);
			ll = ll - y*YEAR;
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Year").replace("%value%", String.valueOf(y)));
		}
		if(useYears && useDays)
		{
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator"));
		}
		if(useDays)
		{
			long d = Math.floorDiv(ll, DAY);
			ll = ll - d*DAY;
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Day").replace("%value%", String.valueOf(d)));
		}
		if(useDays && useHours)
		{
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator"));
		}
		if(useHours)
		{
			StringBuilder hour = new StringBuilder();
			long H = Math.floorDiv(ll, HOUR);
			if(H < 10)
			{
				hour.append(String.valueOf(0));
			}
			hour.append(String.valueOf(H));
			ll = ll - H*HOUR;
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Hour").replace("%value%", hour.toString()));
		}
		if(useHours && useMinutes)
		{
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator"));
		}
		if(useMinutes)
		{
			StringBuilder min = new StringBuilder();
			long m = Math.floorDiv(ll, MIN);
			if(m < 10)
			{
				min.append(String.valueOf(0));
			}
			min.append(String.valueOf(m));
			ll = ll - m*MIN;
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Minute").replace("%value%", min.toString()));
		}
		if(useMinutes && useSeconds)
		{
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator"));
		}
		if(useSeconds)
		{
			StringBuilder sec = new StringBuilder();
			long s = Math.floorDiv(ll, SEC);			
			if(s < 10)
			{
				sec.append(String.valueOf(0));
			}
			sec.append(String.valueOf(s));
			sb.append(plugin.getYamlHandler().getLang().getString("TimeFormat.Second").replace("%value%", sec.toString()));
		}
		return sb.toString();
	}
	
	private void softsave(boolean active)
	{
		for(ProxiedPlayer player : plugin.getProxy().getPlayers())
		{
			if(player == null)
			{
				return;
			}
			ByteArrayOutputStream streamout = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(streamout);
	        try {
				out.writeUTF("afk-softsave");
				out.writeUTF(player.getUniqueId().toString());
				out.writeBoolean(active);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        player.getServer().getInfo().sendData("afkr:afkrecordout", streamout.toByteArray());
		    return;
		}
	}
}