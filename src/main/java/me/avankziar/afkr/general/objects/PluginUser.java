package main.java.me.avankziar.afkr.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import main.java.me.avankziar.afkr.general.database.MysqlBaseHandler;
import main.java.me.avankziar.afkr.general.database.MysqlHandable;
import main.java.me.avankziar.afkr.general.database.QueryType;

public class PluginUser implements MysqlHandable
{
	private String playername;
	private UUID uuid;
	private long lastTimeCheck; //Letzte Interaction des Spielers
	private long activityTime; //insgesamte Activitätszeit
	private long afkTime; //insgesamte Afkzeit
	private long allTime; //insgesamte Zeit
	private long lastActivity;
	private boolean isAFK;
	private boolean isOnline;
	private long vacationTime;
	
	public PluginUser() {}
	
	public PluginUser(UUID uuid, String playername,
			long lastTimeCheck,
			long activityTime, long afkTime, long allTime,
			long lastActivity,
			boolean isAFK, boolean isOnline,
			long vacationTime)
	{
		setPlayerName(playername);
		setUUID(uuid);
		setLastTimeCheck(lastTimeCheck);
		setActiveTime(activityTime);
		setAfkTime(afkTime);
		setTotalTime(allTime);
		setLastActivity(lastActivity);
		setAFK(isAFK);
		setOnline(isOnline);
		setVacationTime(vacationTime);
	}

	public String getPlayerName()
	{
		return playername;
	}

	public void setPlayerName(String playername)
	{
		this.playername = playername;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public long getLastTimeCheck()
	{
		return lastTimeCheck;
	}

	public void setLastTimeCheck(long lastTimeCheck)
	{
		this.lastTimeCheck = lastTimeCheck;
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

	public long getLastActivity()
	{
		return lastActivity;
	}

	public void setLastActivity(long lastActivity)
	{
		this.lastActivity = lastActivity;
	}

	public boolean isAFK()
	{
		return isAFK;
	}

	public void setAFK(boolean isAFK)
	{
		this.isAFK = isAFK;
	}

	public boolean isOnline()
	{
		return isOnline;
	}

	public void setOnline(boolean isOnline)
	{
		this.isOnline = isOnline;
	}

	public long getVacationTime()
	{
		return vacationTime;
	}

	public void setVacationTime(long vacationTime)
	{
		this.vacationTime = vacationTime;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `player_name`, `alltime`, `activitytime`, `afktime`,"
					+ " `lastactivity`, `lasttimecheck`, `isafk`, `isonline`, `vacationtime`) " 
					+ "VALUES("
					+ "?, ?, ?, ?, ?,"
					+ "?, ?, ?, ?, ?"
					+ ")";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getPlayerName());
	        ps.setLong(3, getTotalTime());
	        ps.setLong(4, getActiveTime());
	        ps.setLong(5, getAfkTime());
	        ps.setLong(6, getLastActivity());
	        ps.setLong(7, getLastTimeCheck());
	        ps.setBoolean(8, isAFK());
	        ps.setBoolean(9, isOnline());
	        ps.setLong(10, getVacationTime());
	        
	        int i = ps.executeUpdate();
	        MysqlBaseHandler.addRows(QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try 
		{
			String sql = "UPDATE `" + tablename
					+ "` SET `player_uuid` = ?, `player_name` = ?,"
					+ " `alltime` = ?, `activitytime` = ?, `afktime` = ?,"
					+ " `lastactivity` = ?, `lasttimecheck` = ?, `isafk`= ?, `isonline` = ?,"
					+ " `vacationtime` = ?" 
					+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
		    ps.setString(2, getPlayerName());
		    ps.setLong(3, getTotalTime());
		    ps.setLong(4, getActiveTime());
		    ps.setLong(5, getAfkTime());
		    ps.setLong(6, getLastActivity());
		    ps.setLong(7, getLastTimeCheck());
	        ps.setBoolean(8, isAFK());
	        ps.setBoolean(9, isOnline());
	        ps.setLong(10, getVacationTime());
	        int i = 11;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlBaseHandler.addRows(QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + tablename + "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new PluginUser(
	        			UUID.fromString(rs.getString("player_uuid")),
	        			rs.getString("player_name"),
	        			rs.getLong("lasttimecheck"),
	        			rs.getLong("activitytime"),
	        			rs.getLong("afktime"),
	        			rs.getLong("alltime"),
	        			rs.getLong("lastactivity"),
	        			rs.getBoolean("isafk"),
	        			rs.getBoolean("isonline"),
	        			rs.getLong("vacationtime")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<PluginUser> convert(ArrayList<Object> arrayList)
	{
		ArrayList<PluginUser> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof PluginUser)
			{
				l.add((PluginUser) o);
			}
		}
		return l;
	}	
}
