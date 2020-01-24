package main.java.de.avankziar.afkrecord.spigot;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BackgroundTask 
{
	private AfkRecord plugin;
	
	public BackgroundTask(AfkRecord plugin)
	{
		this.plugin = plugin;
		runTask();
	}
	
	public void runTask()
	{
		runSave();
		runDataSave();
		runAfkTrackerTask();
	}
	
	public void runSave() //Interne Abspeicherung
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getUtility().softSave(player, false, false, false);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				Long.parseLong(plugin.getYamlHandler().get().getString("general.softsaveinseconds"))*20L);
	}
	
	public void runDataSave() //mysql abspeicherung
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getUtility().hardSave(player, false);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 15L,
				Long.parseLong(plugin.getYamlHandler().get().getString("general.mysqlsaveinseconds"))*20L);
	}
	
	public void runAfkTrackerTask()
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getUtility().afkchecker(player);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				Long.parseLong(plugin.getYamlHandler().get().getString("general.afkcheckerinseconds"))*20L);
	}
	
	public void onShutDownDataSave()
	{
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			plugin.getUtility().hardSave(player, true);
		}
	}
}
