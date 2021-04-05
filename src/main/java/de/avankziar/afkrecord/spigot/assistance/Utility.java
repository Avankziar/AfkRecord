package main.java.de.avankziar.afkrecord.spigot.assistance;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;

public class Utility 
{
	private AfkRecord plugin;
	private static boolean serverDisable = false;
	public static ArrayList<String> playerWhoBypassAfkTracking = new ArrayList<>(); //UUIDs
	
	final public static String 
	PERMTIMELASTACTIVITY = "afkrecord.cmd.afkrecord.time.lastactivity",
	PERMCOUNTTIMELASTACTIVITY = "afkrecord.cmd.afkrecord.counttime.lastactivity";
	
	public Utility(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	public void debug(Player player, String s)
	{
		boolean boo = false;
		if(boo)
		{
			if(player != null)
			{
				player.sendMessage(s);
			}
			System.out.println(s);
		}
	}
	
	public void saveAndServerDisable(Player player, boolean incomeNewActivity, boolean incomeSetAfk)
	{
		serverDisable = true;
		debug(player, "AfkR saveAndServerDisable ");
		save(player, incomeNewActivity, incomeSetAfk, false, true);
	}
	
	
	
	/*
	 * @param incomeNewActivity == If you move etc. Or if you become active
	 * @param incomeSetAfk == /afk
	 * @param incomeAfk == afkchecker
	 * @param playerQuit == When the player quit the server
	 */
	public void save(Player player,
			boolean incomeNewActivity, boolean incomeSetAfk, boolean incomeAfk, boolean playerQuit)
	{
		if(player == null)
		{
			return;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlHandler.Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		long now = System.currentTimeMillis();
		if(user == null)
		{
			user = new PluginUser(player.getUniqueId(), player.getName(), now, 0, 0, 0, now, false, true);
		}
		//Difference from last time the player was checked
		final long difference = now - user.getLastTimeCheck();
		//If server goes off, than never go in this if.
		if(!serverDisable && !playerQuit)
		{
			if(!incomeSetAfk)
			{
				if(user.isAFK() && !incomeNewActivity)
				{
					//if the user lasttimecheck is no far away AND no /afk is exceute, so return.
					if(difference < plugin.getYamlHandler().getConfig().getLong("General.SaveInSeconds", 60)*1000)
					{
						return;
					}
				} else if(!user.isAFK() && incomeNewActivity)
				{
					if(difference < plugin.getYamlHandler().getConfig().getLong("General.SaveInSeconds", 60)*1000)
					{
						return;
					}
				}
			}
		}
		long date = TimeHandler.getDate(TimeHandler.getDate(now));
		TimeRecord tr = (TimeRecord) plugin.getMysqlHandler().getData(Type.TIMERECORD,
				"`player_uuid` = ? AND `timestamp_unix` = ?", user.getUUID().toString(),
				date);
		boolean create = false;
		//Multiple Timehandler, from now => dd.MM.yyyy => long from day begin
		if(tr == null)
		{
			tr = new TimeRecord(user.getUUID(), user.getPlayerName(), date, 0, 0, 0);
			create = true;
		}
		debug(player, "---AfkR---");
		debug(player, "Afkr now: "+now);
		debug(player, "Afkr lasttime: "+user.getLastTimeCheck());
		debug(player, "Afkr difference: "+difference+" | "+TimeHandler.getRepeatingTime(difference, "dd-HH:mm:ss"));
		debug(player, "Afkr incomeNewActivity: "+incomeNewActivity);
		debug(player, "Afkr incomeSetAfk: "+incomeSetAfk);
		debug(player, "Afkr playerQuit: "+playerQuit);
		user.setLastTimeCheck(now);
		if(user.isAFK() && !playerWhoBypassAfkTracking.contains(player.getUniqueId().toString()))
		{
			user.setAfkTime(user.getAfkTime()+difference);
			tr.setAfkTime(tr.getAfkTime()+difference);
		} else
		{
			user.setActivityTime(user.getActivityTime()+difference);
			tr.setActivityTime(tr.getActivityTime()+difference);
		}
		user.setAllTime(user.getAllTime()+difference);
		tr.setAllTime(tr.getAllTime()+difference);
		if(incomeNewActivity)
		{
			if(user.isAFK())
			{
				user.setAFK(false);
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("CmdAfk.NoMoreAfk")
						.replace("%time%", TimeHandler.getTime(now))));
			}
			user.setActivityTime(now);
		} else if(incomeSetAfk)
		{
			if(!user.isAFK())
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("CmdAfk.SetAfk")
						.replace("%time%", TimeHandler.getTime(now))));
				user.setAFK(true);
			} else
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("CmdAfk.SetAntiAfk")
						.replace("%time%", TimeHandler.getTime(now))));
				user.setAFK(false);
			}
		} else if(incomeAfk)
		{
			if(!user.isAFK())
			{
				player.spigot().sendMessage(ChatApi.tctl(
						plugin.getYamlHandler().getLang().getString("CmdAfk.SetAfk")
						.replace("%time%", TimeHandler.getTime(now))));
				user.setAFK(true);
			}
		}
		if(playerQuit)
		{
			//If the player quit, he is always NOT Afk
			user.setOnline(false);
			user.setAFK(false);
		}
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", user.getUUID().toString());
		
		if(create)
		{
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
		} else
		{
			plugin.getMysqlHandler().updateData(Type.TIMERECORD, tr, 
					"`player_uuid` = ? AND `timestamp_unix` = ?",
					user.getUUID().toString(),
					date);
		}
	}
	
	public void afkchecker(Player player)
	{
		if(!player.isOnline())
		{
			return;
		}
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
							"`player_uuid` = ?", player.getUniqueId().toString());
		if(user != null)
		{
			if(System.currentTimeMillis() >= 
					(user.getLastActivity()+plugin.getYamlHandler().getConfig().getInt("General.AfkAfterInSeconds", 900)*1000L))
			{
				debug(player, "AfkR AfkChecker");
				save(player, false, false, true, false);
			}
		}
	}
	
	public String timetl(final long time) 
	{
		long t = time;
		String ss = plugin.getYamlHandler().getLang().getString("Time.Seconds");
	    String mm = plugin.getYamlHandler().getLang().getString("Time.Minutes");
	    String HH = plugin.getYamlHandler().getLang().getString("Time.Hours");
	    String dd = plugin.getYamlHandler().getLang().getString("Time.Days");
	    String msg = "";
	    if(t<=0)
	    {
	    	msg += 0+ss;
	    	return msg;
	    }
	    long days = t/(1000*60*60*24);
	    if(days>=1) 
	    {
	    	msg += days+dd;
	    	t = t - days*1000*60*60*24;
	    }
	    long hours = t/(1000*60*60);
	    if(hours>=1) 
	    {
	    	msg += hours+HH;
	    	t = t - hours*1000*60*60;
	    }
	    long minutes = t/(1000*60);
	    if(minutes>=1) 
	    {
	    	msg += minutes+mm;
	    	t = t - minutes*1000*60;
	    }
	    long seconds = t/(1000);
	    if(seconds>=1) 
	    {
	    	msg += seconds+ss;
	    }
	    return msg;
	}
	
	public String getPlaceColor(int place)
	{
		String color = "";
		if(place<=1)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0001")+place;
			return color;
		} else if(place==2)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0002")+place;
			return color;
		} else if(place==3)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0003")+place;
			return color;
		} else if(place>3 && place<=5)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0005")+place;
			return color;
		} else if(place>5 && place<=10)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0010")+place;
			return color;
		} else if(place>10 && place<=25)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0025")+place;
			return color;
		} else if(place>25 && place<=50)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0050")+place;
			return color;
		} else if(place>50 && place<=100)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0100")+place;
			return color;
		} else if(place>100 && place<=250)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0250")+place;
			return color;
		} else if(place>250 && place<=500)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top0500")+place;
			return color;
		} else if(place>500 && place<=1000)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top1000")+place;
			return color;
		} else if(place>1000 && place<=2500)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top2500")+place;
			return color;
		} else if(place>2500 && place<=5000)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top5000")+place;
			return color;
		} else if(place>5000 && place<=9999)
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Top9999")+place;
			return color;
		} else
		{
			color = plugin.getYamlHandler().getLang().getString("Placement.Above")+place;
			return color;
		}
	}
}
