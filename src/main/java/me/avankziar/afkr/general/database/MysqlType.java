package main.java.me.avankziar.afkr.general.database;

import main.java.me.avankziar.afkr.general.objects.PluginUser;
import main.java.me.avankziar.afkr.general.objects.TimeRecord;

public enum MysqlType
{
	PLUGINUSER("afkrecordPlayerData", new PluginUser(), "ALL",
			"CREATE TABLE IF NOT EXISTS `%%tablename%%"
			+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
			+ " player_uuid char(36) NOT NULL UNIQUE,"
    		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
    		+ " alltime BIGINT NULL DEFAULT '0',"
    		+ " activitytime BIGINT NULL DEFAULT '0',"
    		+ " afktime BIGINT NULL DEFAULT '0',"
    		+ " lastactivity BIGINT NULL DEFAULT '0',"
    		+ " lasttimecheck BIGINT NULL DEFAULT '0',"
    		+ " isafk boolean,"
    		+ " isonline boolean,"
    		+ " vacationtime BIGINT NULL DEFAULT '0');"),
	TIMERECORD("afkrecordDateList", new TimeRecord(), "ALL",
			"CREATE TABLE IF NOT EXISTS `%%tablename%%"
			+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
			+ " player_uuid char(36) NOT NULL,"
    		+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
    		+ " timestamp_unix BIGINT NULL DEFAULT '0',"
    		+ " alltime BIGINT NULL DEFAULT '0',"
    		+ " activitytime BIGINT NULL DEFAULT '0',"
    		+ " afktime BIGINT NULL DEFAULT '0');");
	
	private MysqlType(String tableName, Object object, String usedOnServer, String setupQuery)
	{
		this.tableName = tableName;
		this.object = object;
		this.usedOnServer = usedOnServer;
		this.setupQuery = setupQuery.replace("%%tablename%%", tableName);
	}
	
	private final String tableName;
	private final Object object;
	private final String usedOnServer;
	private final String setupQuery;

	public String getValue()
	{
		return tableName;
	}
	
	public Object getObject()
	{
		return object;
	}
	
	public String getUsedOnServer()
	{
		return usedOnServer;
	}
	
	public String getSetupQuery()
	{
		return setupQuery;
	}
}