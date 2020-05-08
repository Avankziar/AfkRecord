package main.java.de.avankziar.afkrecord.spigot.object;


public class PlayerInfo 
{
	private String date;
	private long activitytime;
	private long afktime;
	private long alltime;
	
	public PlayerInfo(String date, long activitytime, long afktime, long alltime)
	{
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
}
