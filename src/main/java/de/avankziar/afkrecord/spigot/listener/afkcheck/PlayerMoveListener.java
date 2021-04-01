package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerMoveListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerMoveListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		plugin.getUtility().save(event.getPlayer(), true, false, false);
	}
}
