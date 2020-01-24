package main.java.de.avankziar.afkrecord.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class EVENTAkfCheck implements Listener
{
	private AfkRecord plugin;
	
	public EVENTAkfCheck(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
	
	@EventHandler
	public void onFish(PlayerFishEvent event)
	{
		plugin.getUtility().softSave(event.getPlayer(), true, true, false);
	}
}
