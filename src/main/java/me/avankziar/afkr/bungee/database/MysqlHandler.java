package main.java.me.avankziar.afkr.bungee.database;

import main.java.me.avankziar.afkr.bungee.AfkR;
import main.java.me.avankziar.afkr.general.database.MysqlBaseHandler;

public class MysqlHandler extends MysqlBaseHandler
{		
	public MysqlHandler(AfkR plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}