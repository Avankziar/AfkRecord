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
	
	public static ArrayList<UUID> playerWhoBypassAfkTracking = new ArrayList<>();
	private static HashMap<UUID, String> onlinePlayers = new HashMap<>();
	public static HashMap<UUID, Long> lastTimeChecked = new HashMap<>();
	public static HashMap<UUID, Long> lastActivity = new HashMap<>();
	public static HashMap<UUID, Boolean> activeStatus = new HashMap<>();
	//private HashMap<UUID, Long> totalTime = new HashMap<>();
	public static HashMap<UUID, Long> activeTime = new HashMap<>();
	public static HashMap<UUID, Long> afkTime = new HashMap<>();
	
	public static boolean isShutDown = false;
	
	public PlayerTimesHandler(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	public void join(final UUID uuid, final String name, boolean isAsync)
	{
		final long now = System.currentTimeMillis();
		onlinePlayers.put(uuid, name);
		lastTimeChecked.put(uuid, now);
		lastActivity.put(uuid, now);
		activeStatus.put(uuid, true);
		if(!hasAccount(uuid))
		{
			createAccount(uuid, name);
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				setOnline(uuid, true);
				setActive(uuid, true);
				setLastActivity(uuid, now);
			}
		}.runTaskLaterAsynchronously(plugin, 20L*4);
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
	
	private void callAfkEvent(UUID uuid, boolean isAsync)
	{
		PlayerChangeToAfkEvent event = new PlayerChangeToAfkEvent(Bukkit.getPlayer(uuid), isAsync);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	private void callNotAfkEvent(UUID uuid, boolean isAsync)
	{
		PlayerChangeToNotAfkEvent event = new PlayerChangeToNotAfkEvent(Bukkit.getPlayer(uuid), isAsync);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	public boolean saveRAM(UUID uuid, Boolean activeOrAfk,
			boolean join, boolean forcedQuit, boolean isAsync)
	{
		//Player p = Bukkit.getPlayer(uuid);
		if(join && !forcedQuit)
		{
			//Join per Event
			final long now = System.currentTimeMillis();
			addTime(uuid, 0, 0, now, now, true, false);
			/*AfkRecord.log.info("0");
			p.sendMessage(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" >> 0"); */
			return true;
		} else if(!join && forcedQuit)
		{
			//Quit per ServerDown or QuitEvent
			if(isShutDown)
			{
				//AfkRecord.log.info("1-0");
				//Check if the the server going down. And so the player already is save out.
				return false;
			}
			final long now = System.currentTimeMillis();
			final long then = lastTimeChecked.get(uuid);
			final long dif = now-then;
			final boolean isAfk = !(activeStatus.containsKey(uuid) ? activeStatus.get(uuid) : true);
			final long act = (activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0)
					+ (isAfk ? 0 : dif);
			final long afkt = (afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0)
					+ (isAfk ? dif : 0);
			/*AfkRecord.log.info("now : "+now);
			AfkRecord.log.info("then : "+then);
			AfkRecord.log.info("dif : "+dif);
			AfkRecord.log.info("now - dif = ? : "+(now-dif));
			AfkRecord.log.info("act : "+act);
			AfkRecord.log.info("afk : "+afkt);*/
			addTime(uuid, act, afkt, now, now, false, false);
			setActive(uuid, isAsync);
			setLastActivity(uuid, now);
			setOnline(uuid, false);
			activeStatus.remove(uuid);
			activeTime.remove(uuid);
			afkTime.remove(uuid);
			activeStatus.get(uuid);
			onlinePlayers.remove(uuid);
			/*AfkRecord.log.info("1-1");
			p.sendMessage(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" >> 1"); //*/
			return true;
		} else if(join && forcedQuit)
		{
			//MySQL Save Run
			final long now = System.currentTimeMillis();
			final long then = lastTimeChecked.get(uuid);
			final long dif = now-then;
			boolean isAfk = !(activeStatus.containsKey(uuid) ? activeStatus.get(uuid) : true);
			final long act = (activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0)
					+ (isAfk ? 0 : dif);
			final long afkt = (afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0)
					+ (isAfk ? dif : 0);
			addTime(uuid, act, afkt, now, lastActivity.containsKey(uuid) ? lastActivity.get(uuid) : -1, true, isAfk);
			activeTime.put(uuid, 0L);
			afkTime.put(uuid, 0L);
			lastTimeChecked.put(uuid, now);
			/*AfkRecord.log.info("2");
			p.sendMessage(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" >> 2"); */
			return true;
		}
		/*AfkRecord.log.info("3");
		p.sendMessage(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" >> 3"); */
		final long now = System.currentTimeMillis();
		final long then = lastTimeChecked.get(uuid);
		final long dif = now-then;
		lastTimeChecked.put(uuid, now);
		boolean activestatus = activeStatus.containsKey(uuid) ? activeStatus.get(uuid) : false;
		if(activeOrAfk == null)
		{
			//no Info if active or afk
			if(activestatus)
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
			if(activestatus)
			{
				// was and is active
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
				lastActivity.put(uuid, now);
			} else
			{
				// was afk, is now active
				callNotAfkEvent(uuid, isAsync);
				Player player = Bukkit.getPlayer(uuid);
				if(player != null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfk.NoMoreAfk")
							.replace("%time%",
									LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
									.format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
				}
				final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
				afkTime.put(uuid, dif+afkt);
				activeStatus.put(uuid, true);
				setActivity(uuid, now, false);
				lastActivity.put(uuid, now);
			}
		} else
		{
			//is now Afk
			if(activestatus && playerWhoBypassAfkTracking.contains(uuid))
			{
				// was active, were now afk, but bypass that
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
			} else if(activestatus)
			{
				// was active, is now afk
				callAfkEvent(uuid, isAsync);
				Player player = Bukkit.getPlayer(uuid);
				if(player != null)
				{
					player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("CmdAfk.SetAfk")
							.replace("%time%", 
									LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
									.format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
				}				
				final long act = activeTime.containsKey(uuid) ? activeTime.get(uuid) : 0;
				activeTime.put(uuid, dif+act);
				activeStatus.put(uuid, false);
				setActivity(uuid, now, true);
			} else
			{
				// was and is afk
				final long afkt = afkTime.containsKey(uuid) ? afkTime.get(uuid) : 0;
				afkTime.put(uuid, dif+afkt);
			}
		}
		return true;
	}
	
	private void setActivity(UUID uuid, long lastTimeChecked, boolean isAfk)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		if(user == null)
		{
			return;
		}
		user.setLastTimeCheck(lastTimeChecked);
		user.setAFK(isAfk);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
	}
	
	public boolean addTime(UUID uuid, long activeTime, long afkTime,
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
		user.setTotalTime(user.getTotalTime()+activeTime+afkTime);
		user.setActiveTime(user.getActiveTime()+activeTime);
		user.setAfkTime(user.getAfkTime()+afkTime);
		user.setLastTimeCheck(lastTimeChecked);
		if(lastActivity > 0)
			user.setLastActivity(lastActivity);
		user.setOnline(isOnline);
		user.setAFK(isAfk);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		return addTimeRecord(uuid, activeTime, afkTime);		
	}
	
	public boolean addTimeRecord(UUID uuid, long activeTime, long afkTime)
	{
		long today = TimeHandler.getDate(TimeHandler.getDate(System.currentTimeMillis()));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		if(tr == null)
		{
			tr = new TimeRecord(uuid, getPlayerName(uuid), today, activeTime+afkTime, activeTime, afkTime);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
			return true;
		}
		tr.setTotalTime(tr.getTotalTime()+activeTime+afkTime);
		tr.setActiveTime(tr.getActiveTime()+activeTime);
		tr.setAfkTime(tr.getAfkTime()+afkTime);
		plugin.getMysqlHandler().updateData(Type.TIMERECORD, tr, "`player_uuid` = ? AND `timestamp_unix` = ?",
				uuid.toString(), today);
		return true;
	}
	
	public void afkChecker(UUID uuid, long afkAfterLastActivityInSeconds, boolean isAsync)
	{
		if(!onlinePlayers.containsKey(uuid)
				|| !activeStatus.containsKey(uuid)
				|| !lastActivity.containsKey(uuid))
		{
			//If not online, activeStatus is unknow or lastActivity is unknow
			return;
		}
		long tt = lastActivity.get(uuid)+afkAfterLastActivityInSeconds;
		if(activeStatus.get(uuid) //was active
				&& tt < System.currentTimeMillis())
			saveRAM(uuid, false, false, false, isAsync);
	}
	
	public void afkKicker(final UUID uuid, final String time, long kickAfterLastActivityInSeconds, boolean isAsync)
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
			saveRAM(uuid, false, false, true, isAsync); //Save for quit
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
	
	public void afkTeleport(final UUID uuid, long teleportAfterLastActivityInSeconds, boolean isAsync)
	{
		if(!onlinePlayers.containsKey(uuid)
				|| !activeStatus.containsKey(uuid)
				|| !lastActivity.containsKey(uuid))
		{
			return;
		}
		if(!activeStatus.get(uuid) //was afk
				&& lastActivity.get(uuid)+teleportAfterLastActivityInSeconds < System.currentTimeMillis())
		{
			saveRAM(uuid, false, false, false, isAsync); //Save for quit
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					Player player = Bukkit.getPlayer(uuid);
					for(String s : plugin.getYamlHandler().getConfig().getStringList(""))
					{
						String[] split = s.split(";");
						if(split.length != 2)
						{
							continue;
						}
						if(split[0].equalsIgnoreCase("console"))
						{
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), split[1].replace("%player%", player.getName()));
						} else if(split[0].equalsIgnoreCase("player"))
						{
							Bukkit.dispatchCommand(player, split[1].replace("%player%", player.getName()));
						}
					}
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

	public boolean setActive(UUID uuid, boolean isAsync)
	{
		return saveRAM(uuid, true, false, false, isAsync);
	}

	public boolean setInactive(UUID uuid, boolean isAsync)
	{
		return saveRAM(uuid, false, false, false, isAsync);
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
		return (user != null) ? !user.isAFK() : false;
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
	
	public String formatTimePeriod(long time, boolean ye, boolean da)
	{
		long ll = time;
		String year = "";
		if(ye)
		{
			long y = Math.floorDiv(ll, YEAR);
			year += String.valueOf(y);
			ll = ll - y*YEAR;
		}
		String day = "";
		if(da)
		{
			long d = Math.floorDiv(ll, DAY);
			day += String.valueOf(d);
			ll = ll - d*DAY;
		}
		
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
		if(ye && da)
		{
			return plugin.getYamlHandler().getLang().getString("TimeFormat.Year").replace("%value%", year)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Day").replace("%value%", day)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Hour").replace("%value%", hour)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Minute").replace("%value%", min)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Second").replace("%value%", sec);
		} else if(da)
		{
			return plugin.getYamlHandler().getLang().getString("TimeFormat.Day").replace("%value%", day)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Hour").replace("%value%", hour)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Minute").replace("%value%", min)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Second").replace("%value%", sec);
		} else
		{
			return plugin.getYamlHandler().getLang().getString("TimeFormat.Hour").replace("%value%", hour)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Minute").replace("%value%", min)
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Seperator")
					+ plugin.getYamlHandler().getLang().getString("TimeFormat.Second").replace("%value%", sec);
		}
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