package main.java.me.avankziar.afkr.spigot.assistance;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.afkr.spigot.AfkR;
import main.java.me.avankziar.afkr.spigot.handler.PlayerTimesHandler;

public class BackgroundTask 
{
	private AfkR plugin;
	
	public BackgroundTask(AfkR plugin)
	{
		this.plugin = plugin;
		runRAMSave();
		runMySQLSave();
		runAfkCheckerTask();
		if(plugin.getYamlHandler().getConfig().getBoolean("General.AfkKicker.IsActive", false))
		{
			runAfkKickerTask();
		}
		if(plugin.getYamlHandler().getConfig().getBoolean("General.AfkTeleport.IsActive", false))
		{
			runAfkTeleportTask();
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
					plugin.getPlayerTimes().saveRAM(player.getUniqueId(), null, false, false);
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
					plugin.getPlayerTimes().saveRAM(player.getUniqueId(), null, true, true);
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
		List<String> excludedWorlds = plugin.getYamlHandler().getConfig().getStringList("General.AfkKicker.ExcludedWorlds");
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
					if(player != null && excludedWorlds.contains(player.getWorld().getName()))
					{
						continue;
					}
					plugin.getPlayerTimes().afkKicker(player.getUniqueId(), msg, kickAfterLastActivityInSeconds);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.AfkKicker.InSeconds")*20L);
	}
	
	public void runAfkTeleportTask()
	{
		List<String> excludedWorlds = plugin.getYamlHandler().getConfig().getStringList("General.AfkTeleport.ExcludedWorlds");
		new BukkitRunnable() 
		{
			final long doCommandAfterLastActivityInSeconds = plugin.getYamlHandler().getConfig()
					.getInt("General.AfkTeleport.DoCommandAfterLastActivityInSeconds")*1000L;
			@Override
			public void run() 
			{
				for(Player player : plugin.getServer().getOnlinePlayers())
				{
					if(player != null && excludedWorlds.contains(player.getWorld().getName()))
					{
						continue;
					}
					plugin.getPlayerTimes().afkTeleport(player.getUniqueId(), doCommandAfterLastActivityInSeconds);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L,
				plugin.getYamlHandler().getConfig().getInt("General.AfkTeleport.InSeconds")*20L);
	}
	
	public void onShutDownDataSave()
	{
		for(Player player : plugin.getServer().getOnlinePlayers())
		{
			plugin.getPlayerTimes().saveRAM(player.getUniqueId(), null, false, true);
		}
		PlayerTimesHandler.isShutDown = true;
	}
}
