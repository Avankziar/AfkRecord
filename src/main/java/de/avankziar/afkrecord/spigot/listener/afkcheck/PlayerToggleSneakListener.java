package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerToggleSneakListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerToggleSneakListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerToggleSneakEvent event)
	{
		plugin.getUtility().debug(event.getPlayer(), "AfkR PlayerToggleSneakEvent");
		plugin.getUtility().save(event.getPlayer(), true, false, false, false);
	}
}