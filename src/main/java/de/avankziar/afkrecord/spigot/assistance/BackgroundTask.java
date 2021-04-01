package main.java.de.avankziar.afkrecord.spigot.assistance;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

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
					plugin.getUtility().save(player, false, false, false);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.SoftSaveInSeconds")*20L);
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
				plugin.getYamlHandler().getConfig().getInt("General.AfkCheckerInSeconds")*20L);
	}
	
	public void onShutDownDataSave()
	{
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			plugin.getUtility().saveAndServerDisable(player, false, false);
		}
	}
}
