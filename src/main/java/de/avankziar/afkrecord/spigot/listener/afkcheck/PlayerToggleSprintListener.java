package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerToggleSprintListener implements Listener
{
	private AfkRecord plugin;
	
	public PlayerToggleSprintListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(PlayerToggleSprintEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
}