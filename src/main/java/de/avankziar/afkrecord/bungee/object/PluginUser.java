package main.java.de.avankziar.afkrecord.bungee.object;

import java.util.UUID;

public class PluginUser
{
	private String playername;
	private UUID uuid;
	private long lastTimeCheck; //Letzte Interaction des Spielers
	private long activityTime; //insgesamte Activitätszeit
	private long afkTime; //insgesamte Afkzeit
	private long allTime; //insgesamte Zeit
	private long lastActivity;
	private boolean isAFK;
	private boolean isOnline;
	
	public PluginUser(UUID uuid, String playername, long lastTimeCheck,
			long activityTime, long afkTime, long allTime, long lastActivity, boolean isAFK, boolean isOnline)
	{
		setPlayerName(playername);
		setUUID(uuid);
		setLastTimeCheck(lastTimeCheck);
		setActivityTime(activityTime);
		setAfkTime(afkTime);
		setAllTime(allTime);
		setLastActivity(lastActivity);
		setAFK(isAFK);
		setOnline(isOnline);
	}

	public String getPlayerName()
	{
		return playername;
	}

	public void setPlayerName(String playername)
	{
		this.playername = playername;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public long getLastTimeCheck()
	{
		return lastTimeCheck;
	}

	public void setLastTimeCheck(long lastTimeCheck)
	{
		this.lastTimeCheck = lastTimeCheck;
	}

	public long getActivityTime()
	{
		return activityTime;
	}

	public void setActivityTime(long activityTime)
	{
		this.activityTime = activityTime;
	}

	public long getAfkTime()
	{
		return afkTime;
	}

	public void setAfkTime(long afkTime)
	{
		this.afkTime = afkTime;
	}

	public long getAllTime()
	{
		return allTime;
	}

	public void setAllTime(long allTime)
	{
		this.allTime = allTime;
	}

	public long getLastActivity()
	{
		return lastActivity;
	}

	public void setLastActivity(long lastActivity)
	{
		this.lastActivity = lastActivity;
	}

	public boolean isAFK()
	{
		return isAFK;
	}

	public void setAFK(boolean isAFK)
	{
		this.isAFK = isAFK;
	}

	public boolean isOnline()
	{
		return isOnline;
	}

	public void setOnline(boolean isOnline)
	{
		this.isOnline = isOnline;
	}

}
