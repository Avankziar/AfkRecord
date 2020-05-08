package main.java.de.avankziar.afkrecord.bungee;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.java.de.avankziar.afkrecord.bungee.object.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Utility 
{	
	private AfkRecord plugin;
	private String prefix;
	private String language;
	
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
	
	public  String getDate()//FIN
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dt = sdf.format(now);
		return dt;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
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
	
	public ArrayList<User> sortAfkList(ProxiedPlayer player)
	{
		ArrayList<User> b = new ArrayList<>();
		for(ProxiedPlayer p : plugin.getProxy().getPlayers())
		{
			if(plugin.getMysqlHandler().hasAccount(p.getName()))
			{
				User u = new User(p, p.getName(),
						System.currentTimeMillis(),
						(Long) plugin.getMysqlHandler().getDataI(p.getName(), "activitytime", "player_name"),
						(Long) plugin.getMysqlHandler().getDataI(p.getName(), "afktime", "player_name"),
						(Long) plugin.getMysqlHandler().getDataI(p.getName(), "alltime", "player_name"),
						(Long) plugin.getMysqlHandler().getDataI(p.getName(), "lastactivity", "player_name"),
						(Boolean) plugin.getMysqlHandler().getDataI(p.getName(), "isafk", "player_name"));
				b.add(u);
			}
		}
        ArrayList<User> c = new ArrayList<>();
        while(b.size()!=0)
        {
        	User u = null;
        	for(User us : b)
        	{
        		if(u == null && us != null)
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
	
	public void getafk(ProxiedPlayer player)
	{
		String language = plugin.getUtility().getLanguage();
		ArrayList<User> user = plugin.getUtility().sortAfkList(player);
		boolean check = false;
		TextComponent playerlist = plugin.getUtility().tc("");
		TextComponent MSG = plugin.getUtility().tctl(
				plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.GetAfk.PlayerAfk"));
		List<BaseComponent> list = new ArrayList<>();
		for(User u : user)
		{
			if(u.isIsafk())
			{
				long t = System.currentTimeMillis()-u.getLastactivity();
				long time = t/(1000*60);
				if(time<15)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.Under15Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=15 && time<30)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.15Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=30 && time<45)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.30Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=45 && time<60)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.45Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=60 && time<90)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.60Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=90 && time<120)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.90Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				} else if(time>=120)
				{
					String pc = plugin.getYamlHandler().getL()
							.getString(language+".CmdAfkRecord.GetAfk.PlayerColor.120Min");
					playerlist = plugin.getUtility().tctl(pc+u.getName()
							+"&f|"+plugin.getUtility().timetl(t)+" ");
					playerlist.setClickEvent(new ClickEvent(
							ClickEvent.Action.RUN_COMMAND, "/afkr time "+u.getName()));
				}
				list.add(playerlist);
				if(check==false)
				{
					check = true;
				}
			}
		}
		if(check==false)
		{
			player.sendMessage(plugin.getUtility().tctl(
					plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.GetAfk.NoPlayerAfk")));
			return;
		}
		MSG.setExtra(list);
		player.sendMessage(MSG);
	}
}
