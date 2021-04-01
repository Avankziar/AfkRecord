package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerFishListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerFishListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerFishEvent event)
	{
		plugin.getUtility().save(event.getPlayer(), true, false, false);
	}
}