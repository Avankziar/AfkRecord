package main.java.de.avankziar.afkrecord.spigot.object;

public class PlayerInfoEx
{
	private String uuid;
	private String playername;
	private String date;
	private long activitytime;
	private long afktime;
	private long alltime;
	
	public PlayerInfoEx(String uuid, String playername, String date, long activitytime, long afktime, long alltime)
	{
		setUuid(uuid);
		setPlayername(playername);
		setDate(date);
		setActivitytime(activitytime);
		setAfktime(afktime);
		setAlltime(alltime);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getActivitytime() {
		return activitytime;
	}

	public void setActivitytime(long activitytime) {
		this.activitytime = activitytime;
	}

	public long getAfktime() {
		return afktime;
	}

	public void setAfktime(long afktime) {
		this.afktime = afktime;
	}

	public long getAlltime() {
		return alltime;
	}

	public void setAlltime(long alltime) {
		this.alltime = alltime;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public String getPlayername()
	{
		return playername;
	}

	public void setPlayername(String playername)
	{
		this.playername = playername;
	}
}