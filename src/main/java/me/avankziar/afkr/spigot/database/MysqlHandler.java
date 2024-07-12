package main.java.me.avankziar.afkr.spigot.database;

import main.java.me.avankziar.afkr.general.database.MysqlBaseHandler;
import main.java.me.avankziar.afkr.spigot.AfkR;

public class MysqlHandler extends MysqlBaseHandler
{		
	public MysqlHandler(AfkR plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}