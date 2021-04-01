package main.java.de.avankziar.afkrecord.spigot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;

public class JoinQuitListener implements Listener
{
	private AfkRecord plugin; 
	
	public JoinQuitListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", player.getUniqueId().toString());
		long now = System.currentTimeMillis();
		if(user != null)
		{
			//Names Aktualisierung
			if(!user.getPlayerName().equals(player.getName()))
			{
				user.setPlayerName(player.getName());
			}		
			user.setOnline(true);
			user.setLastTimeCheck(now);
			user.setLastActivity(now);
			plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", player.getUniqueId().toString());
		} else
		{
			user = new PluginUser(player.getUniqueId(), player.getName(), now, 0, 0, 0, now, false, true);
			plugin.getMysqlHandler().create(Type.PLUGINUSER, user);
		}
		long date = TimeHandler.getDate(TimeHandler.getDate(now));
		if(!plugin.getMysqlHandler().exist(Type.TIMERECORD,
				"`player_uuid` = ? AND `timestamp_unix` = ?", user.getUUID().toString(), date))
		{
			TimeRecord tr = new TimeRecord(user.getUUID(), user.getPlayerName(), date, 0, 0, 0);
			plugin.getMysqlHandler().create(Type.TIMERECORD, tr);
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		plugin.getUtility().save(player, true, false, true);
	}
}
