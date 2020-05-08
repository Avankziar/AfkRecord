package main.java.de.avankziar.afkrecord.bungee.object;

import java.util.ArrayList;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class User 
{
	private ProxiedPlayer player;
	private String name;
	private long lasttimecheck; //Letzte Interaction des Spielers
	private long activitytime; //insgesamte Activitätszeit
	private long afktime; //insgesamte Afkzeit
	private long alltime; //insgesamte Zeit
	private long lastactivity;
	private boolean isafk;
	private static ArrayList<User> allUser = new ArrayList<User>();
	
	public User(ProxiedPlayer player, String name,
			long lasttimecheck, long activitytime, long afktime, long alltime,
			long lastactivity, boolean isafk)
	{
		setPlayer(player);
		setName(name);
		setLasttimecheck(lasttimecheck);
		setActivitytime(activitytime);
		setAfktime(afktime);
		setAlltime(alltime);
		setLastactivity(lastactivity);
		setIsafk(isafk);
	}
	
	public static User getUser(ProxiedPlayer player)
	{
		User u = null;
		for(User us : allUser)
		{
			if(us != null)
			{
				if(us.getPlayer() != null)
				{
					if(player!=null)
					{
						if(us.getPlayer().getName().equals(player.getName()))
						{
							u = us;
							break;
						}
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
	
	public static ArrayList<User> getUsers()
	{
		return allUser;
	}
	
	public ProxiedPlayer getPlayer() 
	{
		return player;
	}

	public void setPlayer(ProxiedPlayer player) 
	{
		this.player = player;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public long getLasttimecheck() 
	{
		return lasttimecheck;
	}

	public void setLasttimecheck(long lasttimecheck) 
	{
		this.lasttimecheck = lasttimecheck;
	}

	public boolean isIsafk() 
	{
		return isafk;
	}

	public void setIsafk(boolean isafk) 
	{
		this.isafk = isafk;
	}

	public long getLastactivity() 
	{
		return lastactivity;
	}

	public void setLastactivity(long lastactivity) 
	{
		this.lastactivity = lastactivity;
	}
}
