package main.java.de.avankziar.afkrecord.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.interfaces.User;

public class EVENTJoinLeave implements Listener
{
	private AfkRecord plugin; 
	
	public EVENTJoinLeave(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(!plugin.getMysqlInterface().hasAccount(player))
		{
			plugin.getMysqlInterface().createAccount(player);
		}
		if(!plugin.getMysqlInterface().existDate(player, plugin.getUtility().getDate()))
		{
			plugin.getMysqlInterface().createDate(player);
		}
		User u = new User(player, System.currentTimeMillis(), 0, 0, 0, System.currentTimeMillis(), false);
		User.addUser(u);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		plugin.getUtility().softSave(player, true, true, false);
		plugin.getUtility().hardSave(player, true);
	}
}
