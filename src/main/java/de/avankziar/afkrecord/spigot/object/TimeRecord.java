package main.java.de.avankziar.afkrecord.spigot.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandable;
import main.java.de.avankziar.afkrecord.spigot.database.MysqlHandler;

public class TimeRecord implements MysqlHandable
{
	private UUID uuid;
	private String playerName;
	private long timeStamp;
	private long allTime;
	private long activityTime;
	private long afkTime;
	
	public TimeRecord() {}
	
	public TimeRecord(UUID uuid, String playerName, long timeStamp, long allTime, long activityTime, long afkTime)
	{
		setUUID(uuid);
		setPlayerName(playerName);
		setTimeStamp(timeStamp);
		setTotalTime(allTime);
		setActiveTime(activityTime);
		setAfkTime(afkTime);
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getPlayerName()
	{
		return playerName;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public long getActiveTime()
	{
		return activityTime;
	}

	public void setActiveTime(long activityTime)
	{
		this.activityTime = activityTime;
	}

	public long getAfkTime()
	{
		return afkTime;
	}

	public void setAfkTime(long afkTime)
	{
		this.afkTime = afkTime;
	}

	public long getTotalTime()
	{
		return allTime;
	}

	public void setTotalTime(long allTime)
	{
		this.allTime = allTime;
	}

	@Override
	public boolean create(Connection conn, String tablename) throws SQLException
	{
		String sql = "INSERT INTO `" + tablename 
				+ "`(`player_uuid`, `player_name`, `timestamp_unix`, `alltime`, `activitytime`, `afktime`) " 
				+ "VALUES("
				+ "?, ?, ?, ?, ?, ?"
				+ ")";
		PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, getUUID().toString());
        ps.setString(2, getPlayerName());
        ps.setLong(3, getTimeStamp());
        ps.setLong(4, getTotalTime());
        ps.setLong(5, getActiveTime());
        ps.setLong(6, getAfkTime());
        
        int i = ps.executeUpdate();
        MysqlHandler.addRows(MysqlHandler.QueryType.INSERT, i);
        return true;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject) throws SQLException
	{
		String sql = "UPDATE `" + tablename
				+ "` SET `player_uuid` = ?, `player_name` = ?,"
				+ " `timestamp_unix` = ?, `alltime` = ?, `activitytime` = ?, `afktime` = ?" 
				+ " WHERE "+whereColumn;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, getUUID().toString());
        ps.setString(2, getPlayerName());
        ps.setLong(3, getTimeStamp());
        ps.setLong(4, getTotalTime());
        ps.setLong(5, getActiveTime());
        ps.setLong(6, getAfkTime());
		int i = 7;
		for(Object o : whereObject)
		{
			ps.setObject(i, o);
			i++;
		}			
		int u = ps.executeUpdate();
		MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
		return true;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject) throws SQLException
	{
		String sql = "SELECT * FROM `" + tablename
			+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
		PreparedStatement ps = conn.prepareStatement(sql);
		int i = 1;
		for(Object o : whereObject)
		{
			ps.setObject(i, o);
			i++;
		}
		
		ResultSet rs = ps.executeQuery();
		MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
		ArrayList<Object> al = new ArrayList<>();
		while (rs.next()) 
		{
			al.add(new TimeRecord(
        			UUID.fromString(rs.getString("player_uuid")),
        			rs.getString("player_name"),
        			rs.getLong("timestamp_unix"),
        			rs.getLong("alltime"),
        			rs.getLong("activitytime"),
        			rs.getLong("afktime")));
		}
		return al;
	}
	
	public static ArrayList<TimeRecord> convert(ArrayList<Object> arrayList)
	{
		ArrayList<TimeRecord> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof TimeRecord)
			{
				l.add((TimeRecord) o);
			}
		}
		return l;
	}
}