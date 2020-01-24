package main.java.de.avankziar.afkrecord.spigot.interfaces;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class User 
{
	private Player player;
	private long lasttimecheck; //Letzte Interaction des Spielers
	private long activitytime; //insgesamte Activitätszeit
	private long afktime; //insgesamte Afkzeit
	private long alltime; //insgesamte Zeit
	private long lastactivity;
	private boolean isafk;
	private static ArrayList<User> allUser = new ArrayList<User>();
	
	public User(Player player, long lasttimecheck, long activitytime, long afktime, long alltime, long lastactivity, boolean isafk)
	{
		setPlayer(player);
		setLasttimecheck(lasttimecheck);
		setActivitytime(activitytime);
		setAfktime(afktime);
		setAlltime(alltime);
		setLastactivity(lastactivity);
		setIsafk(isafk);
	}
	
	public static User getUser(Player player)
	{
		User u = null;
		for(User us : allUser)
		{
			if(us != null)
			{
				if(us.getPlayer() != null)
				{
					if(us.getPlayer().getName().equals(player.getName()))
					{
						u = us;
						break;
					}
				}
			}
		}
		return u;
	}
	
	public static void addUser(User u)
	{
		allUser.add(u);
	}
	
	public static void removeUser(User u)
	{
		allUser.remove(u);
	}
	
	public Player getPlayer() 
	{
		return player;
	}

	public void setPlayer(Player player) 
	{
		this.player = player;
	}

	public long getActivitytime() 
	{
		return activitytime;
	}

	public void setActivitytime(long activitytime) 
	{
		this.activitytime = activitytime;
	}

	public long getAfktime() 
	{
		return afktime;
	}

	public void setAfktime(long afktime) 
	{
		this.afktime = afktime;
	}

	public long getAlltime()
	{
		return alltime;
	}

	public void setAlltime(long alltime) 
	{
		this.alltime = alltime;
	}

	public long getLasttimecheck() {
		return lasttimecheck;
	}

	public void setLasttimecheck(long lasttimecheck) {
		this.lasttimecheck = lasttimecheck;
	}

	public boolean isIsafk() {
		return isafk;
	}

	public void setIsafk(boolean isafk) {
		this.isafk = isafk;
	}

	public long getLastactivity() {
		return lastactivity;
	}

	public void setLastactivity(long lastactivity) {
		this.lastactivity = lastactivity;
	}
}
