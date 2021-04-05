package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerInteractEntityListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerInteractEntityListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerInteractEntityEvent event)
	{
		plugin.getUtility().debug(event.getPlayer(), "AfkR PlayerInteractEntityEvent");
		plugin.getUtility().save(event.getPlayer(), true, false, false, false);
	}
}