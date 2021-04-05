package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerCommandPreprocessListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerCommandPreprocessListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerCommandPreprocessEvent event)
	{
		if(!event.getMessage().equalsIgnoreCase("/afk"))
		{
			plugin.getUtility().debug(event.getPlayer(), "AfkR PlayerCommandPreprocessEvent");
			plugin.getUtility().save(event.getPlayer(), true, false, false, false);
		}
	}
}