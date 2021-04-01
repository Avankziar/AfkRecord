package main.java.de.avankziar.afkrecord.spigot.object;

import java.util.UUID;

public class TimeRecord
{
	private UUID uuid;
	private String playerName;
	private long timeStamp;
	private long allTime;
	private long activityTime;
	private long afkTime;
	
	public TimeRecord(UUID uuid, String playerName, long timeStamp, long allTime, long activityTime, long afkTime)
	{
		setUUID(uuid);
		setPlayerName(playerName);
		setTimeStamp(timeStamp);
		setAllTime(allTime);
		setActivityTime(activityTime);
		setAfkTime(afkTime);
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
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

}
