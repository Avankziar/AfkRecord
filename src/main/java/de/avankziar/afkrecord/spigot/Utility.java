package main.java.de.avankziar.afkrecord.spigot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.object.TopList;
import main.java.de.avankziar.afkrecord.spigot.object.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utility 
{
	private AfkRecord plugin;
	private String prefix;
	private String language;
	
	final public static String 
	PERMTIMELASTACTIVITY = "afkrecord.cmd.afkrecord.time.lastactivity",
	PERMCOUNTTIMELASTACTIVITY = "afkrecord.cmd.afkrecord.counttime.lastactivity";
	
	public Utility(AfkRecord plugin)
	{
		this.plugin = plugin;
		loadUtility();
	}
	
	public boolean loadUtility()
	{
		setPrefix(plugin.getYamlHandler().get().getString("Prefix", "&7[&cAfk&eRecord&7] &r"));
		setLanguage(plugin.getYamlHandler().getLanguages());
		return true;
	}

	public String tl(String path)
	{
		return ChatColor.translateAlternateColorCodes('&', path);
	}
	
	public TextComponent tc(String s)
	{
		return new TextComponent(s);
	}
	
	public TextComponent tctl(String s)
	{
		return new TextComponent(ChatColor.translateAlternateColorCodes('&', s));
	}
	
	public TextComponent tctlYaml(String path)
	{
		return new TextComponent(ChatColor.translateAlternateColorCodes('&', plugin.getYamlHandler().getL().getString(path)));
	}
	
	public TextComponent TextWithExtra(String s, List<BaseComponent> list)
	{
		TextComponent tc = tctl(s);
		tc.setExtra(list);
		return tc;
	}
	
	public TextComponent clickEvent(String text, ClickEvent.Action caction, String cmd, boolean yaml)
	{
		TextComponent msg = null;
		if(yaml)
		{
			msg = tctl(plugin.getYamlHandler().getL().getString(text));
		} else
		{
			msg = tctl(text);
		}
		msg.setClickEvent( new ClickEvent(caction, cmd));
		return msg;
	}
	
	public TextComponent hoverEvent(String text, HoverEvent.Action haction, String hover, boolean yaml)
	{
		TextComponent msg = null;
		if(yaml)
		{
			msg = tctl(plugin.getYamlHandler().getL().getString(text));
		} else
		{
			msg = tctl(text);
		}
		msg.setHoverEvent( new HoverEvent(haction, new ComponentBuilder(tl(hover)).create()));
		return msg;
	}
	
	public void softSave(Player player, boolean setactiv, boolean newactivity, boolean setafk)
	{
		User u = User.getUser(player);
		if(u!=null)
		{
			if(System.currentTimeMillis()>=u.getLasttimecheck()) //normaler Check
			{
				long now = System.currentTimeMillis();
				long difference = now - u.getLasttimecheck();
				if(u.isIsafk())
				{
					long afktime = u.getAfktime()+difference;
					u.setAfktime(afktime);
					if(setactiv==true)
					{
						u.setIsafk(false);
						plugin.getMysqlHandler().updateDataI(player, false, "isafk");
						player.spigot().sendMessage(plugin.getUtility().tctl(
								plugin.getYamlHandler().getL().getString(language+".CmdAfk.NoMoreAfk")));
					}
				} else
				{
					long activitytime = u.getActivitytime()+difference;
					u.setActivitytime(activitytime);
					if(setafk==true)
					{
						u.setIsafk(true);
						plugin.getMysqlHandler().updateDataI(player, true, "isafk");
						player.spigot().sendMessage(plugin.getUtility().tctl(
								plugin.getYamlHandler().getL().getString(language+".CmdAfk.SetAfk")));
					}
				}
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = now;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlHandler().updateDataI(player, now, "lastactivity");
				}
			} else if(setactiv == true && newactivity == true && setafk == false && u.isIsafk() == false) 
				//wenn man nicht afk war aber durch externe Anfragen auf den softsave zugegriffen wird.
			{
				long now = System.currentTimeMillis();
				long difference = now - u.getLasttimecheck();
				long activitytime = u.getActivitytime()+difference;
				u.setActivitytime(activitytime);
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = now;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlHandler().updateDataI(player, now, "lastactivity");
				}
			} else if(setactiv == true && newactivity == true && u.isIsafk()) //Wenn man afk war, und nun sich bewegt etc.
			{
				long now = System.currentTimeMillis();
				long difference = now - u.getLasttimecheck();
				long afktime = u.getAfktime()+difference;
				u.setAfktime(afktime);
				u.setIsafk(false);
				plugin.getMysqlHandler().updateDataI(player, false, "isafk");
				player.spigot().sendMessage(plugin.getUtility().tctl(
						plugin.getYamlHandler().getL().getString(language+".CmdAfk.NoMoreAfk")));
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = now;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlHandler().updateDataI(player, now, "lastactivity");
				}
			} else if(setafk == true && u.isIsafk() == false) //wenn man /afk nutzt
			{
				long now = System.currentTimeMillis();
				long difference = now - u.getLasttimecheck();
				long activitytime = u.getActivitytime()+difference;
				u.setActivitytime(activitytime);
				u.setIsafk(true);
				plugin.getMysqlHandler().updateDataI(player, true, "isafk");
				player.spigot().sendMessage(plugin.getUtility().tctl(
						plugin.getYamlHandler().getL().getString(language+".CmdAfk.SetAfk")));
				long alltime = u.getAlltime()+difference;
				u.setAlltime(alltime);
				long newlasttimecheck = now;
				u.setLasttimecheck(newlasttimecheck);
				if(newactivity==true)
				{
					u.setLastactivity(now);
					plugin.getMysqlHandler().updateDataI(player, now, "lastactivity");
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
			long activitytimeI = u.getActivitytime() 
					+ (Long) plugin.getMysqlHandler().getDataI(player, "activitytime", "player_uuid");
			plugin.getMysqlHandler().updateDataI(player, activitytimeI, "activitytime");
			long activitytimeII = u.getActivitytime() 
					+ (Long) plugin.getMysqlHandler().getDataII(player, "activitytime", date);
			plugin.getMysqlHandler().updateDataII(player, activitytimeII, "activitytime", date);
			u.setActivitytime(0);
			long afktimeI = u.getAfktime()+(Long) plugin.getMysqlHandler().getDataI(player, "afktime", "player_uuid");
			plugin.getMysqlHandler().updateDataI(player, afktimeI, "afktime");
			long afktimeII = u.getAfktime()+(Long) plugin.getMysqlHandler().getDataII(player, "afktime", date);
			plugin.getMysqlHandler().updateDataII(player, afktimeII, "afktime", date);
			u.setAfktime(0);
			long alltimeI = u.getAlltime()+ (Long) plugin.getMysqlHandler().getDataI(player, "alltime", "player_uuid");
			plugin.getMysqlHandler().updateDataI(player, alltimeI, "alltime");
			long alltimeII = u.getAlltime()+(Long) plugin.getMysqlHandler().getDataII(player, "alltime", date);
			plugin.getMysqlHandler().updateDataII(player, alltimeII, "alltime", date);
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
			long lastactivity = u.getLastactivity()+plugin.getYamlHandler().get().getInt("General.AfkAfterInSeconds")*1000L;
			if(now>=lastactivity)
			{
				softSave(player, false, false, true);
				plugin.getMysqlHandler().updateDataI(player, true, "isafk");
			}
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
		String ss = plugin.getYamlHandler().getL().getString(language+".Time.Seconds");
	    String mm = plugin.getYamlHandler().getL().getString(language+".Time.Minutes");
	    String HH = plugin.getYamlHandler().getL().getString(language+".Time.Hours");
	    String dd = plugin.getYamlHandler().getL().getString(language+".Time.Days");
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
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0001")+place;
			return color;
		} else if(place==2)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0002")+place;
			return color;
		} else if(place==3)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0003")+place;
			return color;
		} else if(place>3 && place<=5)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0005")+place;
			return color;
		} else if(place>5 && place<=10)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0010")+place;
			return color;
		} else if(place>10 && place<=25)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0025")+place;
			return color;
		} else if(place>25 && place<=50)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0050")+place;
			return color;
		} else if(place>50 && place<=100)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0100")+place;
			return color;
		} else if(place>100 && place<=250)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0250")+place;
			return color;
		} else if(place>250 && place<=500)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top0500")+place;
			return color;
		} else if(place>500 && place<=1000)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top1000")+place;
			return color;
		} else if(place>1000 && place<=2500)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top2500")+place;
			return color;
		} else if(place>2500 && place<=5000)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top5000")+place;
			return color;
		} else if(place>5000 && place<=9999)
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Top9999")+place;
			return color;
		} else
		{
			color = plugin.getYamlHandler().getL().getString(language+".Placement.Above")+place;
			return color;
		}
	}
	
	/*public ArrayList<TopList> sortTopList(ArrayList<TopList> a)
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
	}*/
	
	public ArrayList<User> sortAfkList(ArrayList<User> a, String list)
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
	
	public int getPlace(OfflinePlayer player, ArrayList<TopList> array)
	{
		int place = 0;
		for(TopList tl : array)
		{
			if(tl.getName().equals(player.getName()))
			{
				place = tl.getPlace();
				break;
			}
		}
		return place;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
}
