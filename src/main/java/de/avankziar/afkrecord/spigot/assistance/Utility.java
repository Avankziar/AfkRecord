package main.java.de.avankziar.afkrecord.spigot.assistance;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class Utility 
{
	private AfkRecord plugin;
	
	final public static String 
	PERMTIMELASTACTIVITY = "afkrecord.cmd.afkrecord.time.lastactivity",
	PERMCOUNTTIMELASTACTIVITY = "afkrecord.cmd.afkrecord.counttime.lastactivity";
	
	public Utility(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	/* Dont needed anymore
	public String timet(final long time) 
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
	}*/
	
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
