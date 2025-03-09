package main.java.me.avankziar.afkr.spigot.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.spigot.AfkR;
import me.avankziar.ifh.general.statistic.StatisticType;

public class JoinQuitListener implements Listener
{
	private AfkR plugin; 
	
	public JoinQuitListener(AfkR plugin)
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
		updatePlayerName(uuid, playername);
	}
	
	private void updatePlayerName(final UUID uuid, final String playername)
	{
		PluginUser user = (PluginUser) plugin.getMysqlHandler().getData(MysqlType.PLUGINUSER,
				"`player_uuid` = ?", uuid.toString());
		if(user == null)
		{
			return;	
		}
		user.setPlayerName(playername);
		plugin.getMysqlHandler().updateData(MysqlType.PLUGINUSER, user, "`player_uuid` = ?", uuid.toString());
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(plugin.getStatistic() != null)
				{
					Double afk = plugin.getStatistic().getStatistic(uuid, StatisticType.AFK_ONE_MINUTE);
					if(afk == null)
					{
						double d = (double) plugin.getPlayerTimes().getInactiveTime(uuid) / (1000.0 * 60.0);
						plugin.getStatistic().addStatisticValue(uuid, StatisticType.AFK_ONE_MINUTE, "null", d);
					}
					Double play = plugin.getStatistic().getStatistic(uuid, StatisticType.PLAY_ONE_MINUTE);
					if(play == null)
					{
						double d = (double) plugin.getPlayerTimes().getActiveTime(uuid) / (1000.0 * 60.0);
						plugin.getStatistic().addStatisticValue(uuid, StatisticType.PLAY_ONE_MINUTE, "null", d);
					}
				}
			}
		}.runTaskAsynchronously(plugin);	
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onLeave(PlayerQuitEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		plugin.getPlayerTimes().saveRAM(uuid, null, false, true);
	}
}
