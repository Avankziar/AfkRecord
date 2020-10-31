package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerItemConsumeListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerItemConsumeListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerItemConsumeEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
}