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
		runRAMSave();
		runMySQLSave();
		runAfkCheckerTask();
		if(plugin.getYamlHandler().getConfig().getBoolean("General.AfkKicker.IsActive", false))
		{
			runAfkKickerTask();
		}
	}
	
	public void runRAMSave()
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getPlayerTimes().saveRAM(player.getUniqueId(), null, false, false, true);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.RAMSave.InSeconds")*20L);
	}
	
	public void runMySQLSave()
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getPlayerTimes().saveRAM(player.getUniqueId(), null, true, true, true);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.MySQLSave.InSeconds")*20L);
	}
	
	public void runAfkCheckerTask()
	{
		new BukkitRunnable() 
		{
			final long afkAfterLastActivityInSeconds = plugin.getYamlHandler().getConfig()
					.getLong("General.AfkChecker.AfkAfterLastActivityInSeconds")*1000L;
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getPlayerTimes().afkChecker(player.getUniqueId(), afkAfterLastActivityInSeconds);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.AfkChecker.InSeconds")*20L);
	}
	
	public void runAfkKickerTask()
	{
		new BukkitRunnable() 
		{
			final long kickAfterLastActivityInSeconds = plugin.getYamlHandler().getConfig()
					.getInt("General.AfkKicker.KickAfterLastActivityInSeconds")*1000L;
			final String msg = plugin.getYamlHandler().getLang().getString("AfkKicker.Kick")
					.replace("%time%",
									String.valueOf(plugin.getYamlHandler().getConfig()
									.getInt("General.AfkKicker.KickAfterLastActivityInSeconds")/60
									+ plugin.getYamlHandler().getConfig()
									.getInt("General.AfkChecker.AfkAfterLastActivityInSeconds")/60)+" min"
					);
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					plugin.getPlayerTimes().afkKicker(player.getUniqueId(), msg, kickAfterLastActivityInSeconds);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.AfkKicker.InSeconds")*20L);
	}
	
	public void onShutDownDataSave()
	{
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			plugin.getPlayerTimes().saveRAM(player.getUniqueId(), null, false, true, false);
		}
	}
}
