package main.java.me.avankziar.afkr.velocity.assistance;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.velocitypowered.api.proxy.Player;

import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.velocity.AfkR;

public class BackgroundTask
{
	private AfkR plugin;
	
	public BackgroundTask(AfkR plugin)
	{
		this.plugin = plugin;
	}
	
	public void runProxyCheckOfflineAndAfkTask()
	{
		if(!plugin.getYamlHandler().getConfig().getBoolean("Proxy.Scheduler.CheckIfOfflineAndAfk.Active"))
		{
			return;
		}
		long loop = plugin.getYamlHandler().getConfig().getLong("Proxy.Scheduler.CheckIfOfflineAndAfk.LoopInMinutes");
		plugin.getProxy().getScheduler().buildTask(plugin, (task) ->
		{
			ArrayList<PluginUser> all = PluginUser.convert(plugin.getMysqlHandler().getFullList(MysqlType.PLUGINUSER, "`id` ASC",
					"`isonline` = ?", true));
			for(PluginUser pu : all)
			{
				Optional<Player> o = plugin.getProxy().getPlayer(pu.getUUID());
				if(o.isPresent())
				{
					continue;
				}
				pu.setAFK(false);
				pu.setOnline(false);
				plugin.getMysqlHandler().updateData(MysqlType.PLUGINUSER, pu, "`player_uuid` = ?", pu.getUUID().toString());
			}
		}).delay(1L, TimeUnit.SECONDS).repeat(loop, TimeUnit.MINUTES).schedule();
	}
}