package main.java.me.avankziar.afkr.velocity.database;

import main.java.me.avankziar.afkr.velocity.AfkR;
import main.java.me.avankziar.afkr.general.database.MysqlBaseHandler;

public class MysqlHandler extends MysqlBaseHandler
{		
	public MysqlHandler(AfkR plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}