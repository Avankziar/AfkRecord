package main.java.de.avankziar.afkrecord.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.object.User;

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
		if(!plugin.getMysqlHandler().hasAccount(player))
		{
			plugin.getMysqlHandler().createAccount(player);
		}
		String oldplayername = (String) plugin.getMysqlHandler().getDataI(
				player.getUniqueId().toString(), "player_name", "player_uuid");
		//Names Aktualisierung
		if(!oldplayername.equals(player.getName()))
		{
			plugin.getMysqlHandler().updateDataI(player.getUniqueId().toString(), player.getName(), "player_name");
		}
		if(!plugin.getMysqlHandler().existDate(player, plugin.getUtility().getDate()))
		{
			plugin.getMysqlHandler().createDate(player);
		}
		User u = new User(player, player.getName(), System.currentTimeMillis(), 0, 0, 0, System.currentTimeMillis(), false);
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
