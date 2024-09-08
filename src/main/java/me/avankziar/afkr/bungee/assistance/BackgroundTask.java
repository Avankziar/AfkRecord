package main.java.me.avankziar.afkr.bungee.assistance;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import main.java.me.avankziar.afkr.bungee.AfkR;
import main.java.me.avankziar.afkr.general.database.MysqlType;
import main.java.me.avankziar.afkr.general.objects.PluginUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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
		plugin.getProxy().getScheduler().schedule(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<PluginUser> all = PluginUser.convert(plugin.getMysqlHandler().getFullList(MysqlType.PLUGINUSER, "`id` ASC",
						"`isonline` = ?", true));
				for(PluginUser pu : all)
				{
					ProxiedPlayer o = plugin.getProxy().getPlayer(pu.getUUID());
					if(o != null && o.isConnected())
					{
						continue;
					}
					pu.setAFK(false);
					pu.setOnline(false);
					plugin.getMysqlHandler().updateData(MysqlType.PLUGINUSER, pu, "`player_uuid` = ?", pu.getUUID().toString());
				}
			}
		}, 1000L*60*loop, 1000L*60, TimeUnit.MILLISECONDS);
	}
}
