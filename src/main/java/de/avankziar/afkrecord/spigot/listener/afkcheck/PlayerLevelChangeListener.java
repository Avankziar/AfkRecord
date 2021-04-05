package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerLevelChangeListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerLevelChangeListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerLevelChangeEvent event)
	{
		plugin.getUtility().debug(event.getPlayer(), "AfkR PlayerLevelChangeEvent");
		plugin.getUtility().save(event.getPlayer(), true, false, false, false);
	}
}
