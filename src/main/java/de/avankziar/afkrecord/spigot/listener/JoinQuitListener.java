package main.java.de.avankziar.afkrecord.spigot.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler.Type;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;

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
		final UUID uuid = player.getUniqueId();
		final String playername = player.getName();
		plugin.getPlayerTimes().join(uuid, playername, event.isAsynchronous());
		if(plugin.getPlayerTimes().saveRAM(uuid, null, true, false, event.isAsynchronous()))
			updatePlayerName(uuid, playername);
	}
	
	private void updatePlayerName(final UUID uuid, final String playername)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(Type.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		if(user == null)
			return;
		user.setPlayerName(playername);
		plugin.getMysqlHandler().updateData(Type.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onLeave(PlayerQuitEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		plugin.getPlayerTimes().saveRAM(uuid, null, false, true, event.isAsynchronous());
	}
}
