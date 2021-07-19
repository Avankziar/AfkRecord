package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import java.util.LinkedHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerAsyncChatListener implements Listener
{
	private AfkRecord plugin;
	private LinkedHashMap<String, Long> cooldown = new LinkedHashMap<>();
	
	public PlayerAsyncChatListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEvent(AsyncPlayerChatEvent event)
	{
		//ADDME Config Cooldowns für alle Event.
		if(cooldown.containsKey(event.getPlayer().getUniqueId().toString()))
		{
			if(cooldown.get(event.getPlayer().getUniqueId().toString()) > System.currentTimeMillis())
			{
				return;
			}
			cooldown.replace(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis()+1000L*60);
		} else
		{
			cooldown.put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis()+1000L*60);
		}
		plugin.getUtility().save(event.getPlayer(), true, false, false, false);
	}
}
