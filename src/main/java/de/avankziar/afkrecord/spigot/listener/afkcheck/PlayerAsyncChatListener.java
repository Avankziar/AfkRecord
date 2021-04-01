package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerAsyncChatListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerAsyncChatListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(AsyncPlayerChatEvent event)
	{
		plugin.getUtility().save(event.getPlayer(), true, false, false);
	}
}
