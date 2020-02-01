package main.java.de.avankziar.afkrecord.spigot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.interfaces.TopList;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utility 
{
	private AfkRecord plugin;
	private String language;
	
	public Utility(AfkRecord plugin)
	{
		this.plugin = plugin;
		language = plugin.getYamlHandler().get().getString("language");
	}

	public String tl(String path)
	{
		return ChatColor.translateAlternateColorCodes('&', path);
	}
	
	public TextComponent tc(String s)
	{
		return new TextComponent(s);
	}
	
	public TextComponent tcl(String s)
	{
		return new TextComponent(ChatColor.translateAlternateColorCodes('&', s));
	}
	
	public void sendMessage(Player p, String path)
	{
		p.spigot().sendMessage(tc(tl(path)));
	}
	
	public void softSave(Player player, boolean setactiv, boolean newactivity, boolean setafk)
	{
		long now = System.currentTimeMillis();
		User u = User.getUser(player);
		if(u!=null)
		{
			if(now>=u.getLasttimecheck()) //normaler Check
			{
				long difference = now - u.getLasttimecheck()+1000L*15;
				if(u.isIsafk())
				{
					long afktime = u.getAfktime()+difference;
					u.setAfktime(afktime);
					if(setactiv==true)
					{
						u.setIsafk(false);
						plugin.getMysqlInterface().updateDataI(player, false, "isafk");
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfk.msg03")));
					}
				} else
				{
					long activitytime = u.getActivitytime()+difference;
					u.setActivitytime(activitytime);
					if(setafk==true)
					{
						u.setIsafk(true);
						plugin.getMysqlInterface().updateDataI(player, true, "isafk");
						player.spigot().sendMessage(plugin.getUtility().tcl(
								plugin.getYamlHandler().getL().getString(language+".CMDAfk.msg01")));
					}
				}
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = System.currentTimeMillis()+1000L*15;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlInterface().updateDataI(player, now, "lastactivity");
				}
			} else if(setactiv == true && newactivity == true && u.isIsafk()) //Wenn man afk war, und nun sich bewegt etc.
			{
				long difference = now - u.getLasttimecheck()+1000L*15;
				long afktime = u.getAfktime()+difference;
				u.setAfktime(afktime);
				u.setIsafk(false);
				plugin.getMysqlInterface().updateDataI(player, false, "isafk");
				player.spigot().sendMessage(plugin.getUtility().tcl(
						plugin.getYamlHandler().getL().getString(language+".CMDAfk.msg03")));
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = System.currentTimeMillis()+1000L*15;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlInterface().updateDataI(player, now, "lastactivity");
				}
			} else if(setafk == true && u.isIsafk() == false) //wenn man /afk nutzt
			{
				long difference = now - u.getLasttimecheck()+1000L*15;
				long activitytime = u.getActivitytime()+difference;
				u.setActivitytime(activitytime);
				u.setIsafk(true);
				plugin.getMysqlInterface().updateDataI(player, true, "isafk");
				player.spigot().sendMessage(plugin.getUtility().tcl(
						plugin.getYamlHandler().getL().getString(language+".CMDAfk.msg01")));
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = System.currentTimeMillis()+1000L*15;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlInterface().updateDataI(player, now, "lastactivity");
				}
			}
		}
	}
	
	public void hardSave(Player player, boolean removeuser)
	{
		User u = User.getUser(player);
		if(u!=null)
		{
			String date = getDate();
			long activitytimeI = u.getActivitytime()+ Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "activitytime", "player_uuid"));
			plugin.getMysqlInterface().updateDataI(player, activitytimeI, "activitytime");
			long activitytimeII = u.getActivitytime()+ Long.parseLong((String) plugin.getMysqlInterface().getDataII(player, "activitytime", date));
			plugin.getMysqlInterface().updateDataII(player, activitytimeII, "activitytime", date);
			u.setActivitytime(0);
			long afktimeI = u.getAfktime()+Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "afktime", "player_uuid"));
			plugin.getMysqlInterface().updateDataI(player, afktimeI, "afktime");
			long afktimeII = u.getAfktime()+Long.parseLong((String) plugin.getMysqlInterface().getDataII(player, "afktime", date));
			plugin.getMysqlInterface().updateDataII(player, afktimeII, "afktime", date);
			u.setAfktime(0);
			long alltimeI = u.getAlltime()+ Long.parseLong((String) plugin.getMysqlInterface().getDataI(player, "alltime", "player_uuid"));
			plugin.getMysqlInterface().updateDataI(player, alltimeI, "alltime");
			long alltimeII = u.getAlltime()+Long.parseLong((String) plugin.getMysqlInterface().getDataII(player, "alltime", date));
			plugin.getMysqlInterface().updateDataII(player, alltimeII, "alltime", date);
			u.setAlltime(0);
			if(removeuser==true)
			{
				User.removeUser(u);
				u = null;
			}
		}
	}
	
	public void afkchecker(Player player)
	{
		User u = User.getUser(player);
		if(u!=null)
		{
			long now = System.currentTimeMillis();
			long lastactivity = u.getLastactivity()+
					Long.parseLong(plugin.getYamlHandler().get().getString("general.afkafterinseconds"))*1000;
			if(now>=lastactivity)
			{
				softSave(player, false, false, true);
				plugin.getMysqlInterface().updateDataI(player, true, "isafk");
			}
		}
	}
	
	public boolean rightArgs(Player p, String[] args, int i)
    {
    	if(args.length!=i)
    	{
    		TextComponent msg = tc(tl(plugin.getYamlHandler().getL().getString(language+".CMD_SCCB.msg01")));
			msg.setClickEvent( new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/scc"));
			p.spigot().sendMessage(msg);
			return true;
    	} else
    	{
    		return false;
    	}
    }
	
	public String getDate()//FIN
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dt = sdf.format(now);
		return dt;
	}
	
	public String getDateExact(Long l)//FIN
	{
		Date now = new Date(l);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dt = sdf.format(now);
		return dt;
	}
	
	public String addingDaysToDate(String date, int days) //FIN
	{
		String oldDate = date;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = Calendar.getInstance();
		try
		{
			calendar.setTime(sdf.parse(oldDate));
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_MONTH, days);
		String newDate = sdf.format(calendar.getTime());
		return newDate;
	}
	
	public Long getDateInLong(String date)
	{
		SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
		try {
		    Date d = f.parse(date);
		    long time = d.getTime();
		    return time;
		} catch (ParseException e) 
		{
		    return 0L;
		}
	}
	
	public String timetl(final long time) 
	{
		long t = time;
		String ss = plugin.getYamlHandler().getL().getString(language+".time.seconds");
	    String mm = plugin.getYamlHandler().getL().getString(language+".time.minutes");
	    String HH = plugin.getYamlHandler().getL().getString(language+".time.hours");
	    String dd = plugin.getYamlHandler().getL().getString(language+".time.days");
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
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0001")+place;
			return color;
		} else if(place==2)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0002")+place;
			return color;
		} else if(place==3)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0003")+place;
			return color;
		} else if(place>3 && place<=5)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0005")+place;
			return color;
		} else if(place>5 && place<=10)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0010")+place;
			return color;
		} else if(place>10 && place<=25)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0025")+place;
			return color;
		} else if(place>25 && place<=50)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0050")+place;
			return color;
		} else if(place>50 && place<=100)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0100")+place;
			return color;
		} else if(place>100 && place<=250)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0250")+place;
			return color;
		} else if(place>250 && place<=500)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top0500")+place;
			return color;
		} else if(place>500 && place<=1000)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top1000")+place;
			return color;
		} else if(place>1000 && place<=2500)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top2500")+place;
			return color;
		} else if(place>2500 && place<=5000)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top5000")+place;
			return color;
		} else if(place>5000 && place<=9999)
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.top9999")+place;
			return color;
		} else
		{
			color = plugin.getYamlHandler().getL().getString(language+".placement.above")+place;
			return color;
		}
	}
	
	public ArrayList<TopList> sortTopList(ArrayList<TopList> a)
	{
		ArrayList<TopList> b = new ArrayList<>(a);
        ArrayList<TopList> c = new ArrayList<>();
        int i = 1;
        while(b.size()!=0)
        {
        	TopList t = new TopList(1, "", -1);
        	for(TopList tl : b)
        	{
        		if(tl.getTime()>t.getTime())
        		{
        			t = tl;
        		}
        	}
        	t.setPlace(i);
        	i++;
        	b.remove(t);
        	c.add(t);
        }
        return c;
	}
	
	public ArrayList<User> sortAfkList(ArrayList<User> a)
	{
		ArrayList<User> b = new ArrayList<>(a);
        ArrayList<User> c = new ArrayList<>();
        while(b.size()!=0)
        {
        	User u = null;
        	for(User us : b)
        	{
        		if(u == null)
        		{
        			u = us;
        		}
        		if(us.getActivitytime()<u.getLastactivity())
        		{
        			u = us;
        		}
        	}
        	b.remove(u);
        	c.add(u);
        }
        return c;
	}
}
