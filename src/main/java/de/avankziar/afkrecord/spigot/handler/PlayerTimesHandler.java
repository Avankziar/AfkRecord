package main.java.de.avankziar.afkrecord.spigot.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;
import main.java.me.avankziar.ifh.spigot.event.player.PlayerChangeToAfkEvent;
import main.java.me.avankziar.ifh.spigot.event.player.PlayerChangeToNotAfkEvent;

public class PlayerTimesHandler
{
	private AfkRecord plugin;
	
	final static long SEC = 1000L;
	final static long MIN = SEC*60;
	final static long HOUR = MIN*60;
	final static long DAY = HOUR*24;
	final static long WEEK = DAY*7;
	final static long YEAR = DAY*365;
	
	public ArrayList<UUID> playerWhoBypassAfkTracking = new ArrayList<>();
	private HashMap<UUID, String> onlinePlayers = new HashMap<>();
	private HashMap<UUID, Long> lastTimeChecked = new HashMap<>();
	private HashMap<UUID, Long> lastActivity = new HashMap<>();
	private HashMap<UUID, Boolean> activeStatus = new HashMap<>();
	private HashMap<UUID, Long> totalTime = new HashMap<>();
	private HashMap<UUID, Long> activeTime = new HashMap<>();
	private HashMap<UUID, Long> afkTime = new HashMap<>();
	
	public PlayerTimesHandler(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	public void join(final UUID uuid, final String name)
	{
		onlinePlayers.put(uuid, name);
		lastTimeChecked.put(uuid, System.currentTimeMillis());
		lastActivity.put(uuid, System.currentTimeMillis());
		activeStatus.put(uuid, true);
		if(!hasAccount(uuid))
		{
			createAccount(uuid, name);
		}
		setOnline(uuid, true);
	}
	
	public void quit(final UUID uuid)
	{
		onlinePlayers.remove(uuid);
		setOnline(uuid, false);
	}
	
	private String getOnlinePlayerName(UUID uuid)
	{
		return onlinePlayers.get(uuid);
	}
	
	private String getPlayerName(UUID uuid)
	{
		return ((PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString())).getPlayerName();
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
	
	private boolean callAfkEvent(UUID uuid, boolean isAsync)
	{
		PlayerChangeToAfkEvent event = new PlayerChangeToAfkEvent(Bukkit.getPlayer(uuid), isAsync);
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled();
	}
	
	private boolean callNotAfkEvent(UUID uuid, boolean isAsync)
	{
		PlayerChangeToNotAfkEvent event = new PlayerChangeToNotAfkEvent(Bukkit.getPlayer(uuid), isAsync);
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled();
	}
	
	public boolean saveRAM(UUID uuid, Boolean activeOrAfk, boolean join, boolean forcedQuit, boolean isAsync)
	{
		if(join && !forcedQuit)
		{
			//Join per Event
			final long now = System.currentTimeMillis();
			addTime(uuid, 0, 0, 0, now, now, true, false);
			lastTimeChecked.put(uuid, now);
			lastActivity.put(uuid, now);
			activeStatus.put(uuid, true);
			return true;
		} else if(!join && forcedQuit)
		{
			//Quit per ServerDown or QuitEvent
			if(!lastTimeChecked.containsKey(uuid)
					|| !activeStatus.containsKey(uuid))
			{
				//Check if the the server going down. And so the player already is save out.
				return false;
			}
			final long now = System.currentTimeMillis();
			final long tot = totalTime.containsKey(uuid) ? totalTime.get(uuid) : 0;
			final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
			final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
			final boolean isAfk = !activeStatus.get(uuid);
			addTime(uuid, tot, act, afkt, now, now, false, isAfk);
			lastTimeChecked.remove(uuid);
			activeStatus.remove(uuid);
			totalTime.remove(uuid);
			activeTime.remove(uuid);
			afkTime.remove(uuid);
			activeStatus.get(uuid);
			return true;
		} else if(join && forcedQuit)
		{
			//MySQL Save Run
			final long now = System.currentTimeMillis();
			final long tot = totalTime.containsKey(uuid) ? totalTime.get(uuid) : 0;
			final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
			final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
			boolean isAfk = !activeStatus.get(uuid);
			addTime(uuid, tot, act, afkt, now, now, true, isAfk);
			totalTime.put(uuid, 0L);
			activeTime.put(uuid, 0L);
			afkTime.put(uuid, 0L);
		}
		if(!lastTimeChecked.containsKey(uuid)
				&& !activeStatus.containsKey(uuid))
		{
			return false;
		}
		/*long nowMinus = System.currentTimeMillis()-(ramSaveCooldown*1000L);
		if(activeOrAfk == null || activeOrAfk == activeStatus.containsKey(uuid))
		{
			if(lastTimeChecked.get(uuid) > nowMinus)
			{
				return false;
			}
		}*/
		
		long now = System.currentTimeMillis();
		long dif = now-lastTimeChecked.get(uuid);
		final long tot = totalTime.containsKey(uuid) ? totalTime.get(uuid) : 0;
		totalTime.put(uuid, dif+tot);
		if(activeOrAfk == null)
		{
			//no Info if active or afk
			if(activeStatus.get(uuid))
			{
				// is/was active
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
			} else
			{
				// is/was afk
				final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
				afkTime.put(uuid, dif+afkt);
			}
		} else if(activeOrAfk)
		{
			//is now Active
			if(activeStatus.get(uuid))
			{
				// was and is active
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
			} else
			{
				// was afk, is now active
				if(callNotAfkEvent(uuid, isAsync))
				{
					return false;
				} else
				{
					Player player = Bukkit.getPlayer(uuid);
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfk.NoMoreAfk")
							.replace("%time%", 
									plugin.getPlayerTimes().formatDate(System.currentTimeMillis(), false, false, false, true, true, true))));
				}
				final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
				afkTime.put(uuid, dif+afkt);
				activeStatus.put(uuid, true);
			}
			lastActivity.put(uuid, now);
		} else
		{
			//is now Afk
			if(activeStatus.get(uuid) && playerWhoBypassAfkTracking.contains(uuid))
			{
				// was active, were now afk, but bypass that
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
			} else if(activeStatus.get(uuid))
			{
				// was active, is now afk
				if(callAfkEvent(uuid, isAsync))
				{
					return false;
				} else
				{
					Player player = Bukkit.getPlayer(uuid);
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfk.SetAfk")
							.replace("%time%", 
									plugin.getPlayerTimes().formatDate(System.currentTimeMillis(), false, false, false, true, true, true))));
				}
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
				activeStatus.put(uuid, false);
			} else
			{
				// was and is afk
				final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
				afkTime.put(uuid, dif+afkt);
			}
			lastActivity.put(uuid, now);
		}
		lastTimeChecked.put(uuid, now);
		return true;
	}
	
	public boolean addTime(UUID uuid, long totalTime, long activeTime, long afkTime,
			long lastTimeChecked, long lastActivity, boolean isOnline, boolean isAfk)
	{
		if(!hasAccount(uuid))
		{
			String pn = getOnlinePlayerName(uuid);
			if(pn == null)
			{
				return false;
			}
			boolean ca = createAccount(uuid, pn);
			if(!ca)
				return false;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		if(totalTime > 0)
			user.setTotalTime(user.getTotalTime()+totalTime);
		if(activeTime > 0)
			user.setActiveTime(user.getActiveTime()+activeTime);
		if(afkTime > 0)
			user.setAfkTime(user.getAfkTime()+afkTime);
		if(lastTimeChecked > 0)
			user.setLastTimeCheck(lastTimeChecked);
		if(lastActivity > 0)
			user.setLastActivity(lastActivity);
		user.setOnline(isOnline);
		user.setAFK(isAfk);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return addTimeRecord(uuid, totalTime, activeTime, afkTime);		
	}
	
	public boolean addTimeRecord(UUID uuid, long totalTime, long activeTime, long afkTime)
	{
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(uuid, getPlayerName(uuid), today, totalTime, activeTime, afkTime);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		if(totalTime > 0)
			tr.setTotalTime(tr.getTotalTime()+totalTime);
		if(activeTime > 0)
			tr.setActiveTime(tr.getActiveTime()+activeTime);
		if(afkTime > 0)
			tr.setAfkTime(tr.getAfkTime()+afkTime);
		plugin.getMysqlHandler().updateData(Type.TIMERECORD, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		return true;
	}
	
	public void afkChecker(UUID uuid, long afkAfterLastActivityInSeconds)
	{
		if(!onlinePlayers.containsKey(uuid)
				|| !activeStatus.containsKey(uuid)
				|| !lastActivity.containsKey(uuid))
		{
			//If not online, activeStatus is unknow or lastActivity is unknow
			return;
		}
		if(activeStatus.get(uuid) //was active
				&& lastActivity.get(uuid)+afkAfterLastActivityInSeconds < System.currentTimeMillis())
			saveRAM(uuid, false, false, false, true);
	}
	
	public void afkKicker(final UUID uuid, final String time, long kickAfterLastActivityInSeconds)
	{
		if(!onlinePlayers.containsKey(uuid)
				|| !activeStatus.containsKey(uuid)
				|| !lastActivity.containsKey(uuid))
		{
			//If not online, activeStatus is unknow or lastActivity is unknow
			return;
		}
		if(!activeStatus.get(uuid) //was afk
				&& lastActivity.get(uuid)+kickAfterLastActivityInSeconds < System.currentTimeMillis())
		{
			saveRAM(uuid, false, false, true, true); //Save for quit
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					Player player = Bukkit.getPlayer(uuid);
					player.kickPlayer(ChatApi.tl(time));
				}
			}.runTask(plugin);
		}
	}
	
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

	public boolean createAccount(UUID uuid, String playername)
	{
		long now = System.currentTimeMillis();
		PluginUser user = new PluginUser(uuid, playername, now, 0, 0, 0, now, false, false, 0);
		plugin.getMysqlHandler().create(Type.PLUGINUSER, user);
		return true;
	}

	public boolean deleteAccount(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return false;
		}
		plugin.getMysqlHandler().deleteData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return true;
	}

	public long getActiveTime(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getActiveTime() : 0;
	}

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

	public long getInactiveTime(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getAfkTime() : 0;
	}

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

	public long getLastActivity(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getLastActivity() : 0;
	}

	public long getTotalTime(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getTotalTime() : 0;
	}

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

	public long getVacation(UUID uuid)
	{
		if(!hasAccount(uuid))
		{
			return 0;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getVacationTime() : 0;
	}

	public boolean hasAccount(UUID uuid)
	{
		if(plugin.getMysqlHandler().exist(Type.PLUGINUSER, "`player_uuid` = ?", uuid.toString()))
		{
			return true;
		}
		return false;
	}

	public boolean isOnline(UUID uuid)
	{
		if(onlinePlayers.containsKey(uuid))
		{
			return true;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.isOnline() : false;
	}

	public boolean setActive(UUID uuid)
	{
		return saveRAM(uuid, true, false, false, false);
	}

	public boolean setInactive(UUID uuid)
	{
		return saveRAM(uuid, false, false, false, false);
	}

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

	public boolean setVacation(UUID uuid, int year, int month, int day, int hour, int minute, int second)
	{
		long time = LocalDateTime.of(year, month, day, hour, minute, second, 0).toEpochSecond(OffsetDateTime.now().getOffset());
		return setVacation(uuid, time);
	}

	public boolean isActive(UUID uuid)
	{
		if(onlinePlayers.containsKey(uuid))
		{
			return activeStatus.get(uuid);
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		return (user != null) ? !user.isAFK() : true;
	}
	
	public boolean isRAMActive(UUID uuid)
	{
		return activeStatus.containsKey(uuid) ? activeStatus.get(uuid) : false;
	}
	
	public boolean isVacacation(UUID uuid)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		return (user != null) ? user.getVacationTime() > System.currentTimeMillis() : false;
	}

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
	
	public String formatDate(long time)
	{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"));
	}
	
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
}