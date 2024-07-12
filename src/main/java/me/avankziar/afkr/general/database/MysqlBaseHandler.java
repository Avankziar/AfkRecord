package main.java.me.avankziar.afkr.general.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import main.java.me.avankziar.afkr.general.objects.PluginUser;

public class MysqlBaseHandler
{	
	/*
	 * Alle Mysql Reihen, welche durch den Betrieb aufkommen.
	 */
	public static long startRecordTime = System.currentTimeMillis();
	public static int inserts = 0;
	public static int updates = 0;
	public static int deletes = 0;
	public static int reads = 0;
	
	public static void addRows(QueryType type, int amount)
	{
		switch(type)
		{
		case DELETE:
			deletes += amount;
			break;
		case INSERT:
			inserts += amount;
		case READ:
			reads += amount;
			break;
		case UPDATE:
			updates += amount;
			break;
		}
	}
	
	public static void resetsRows()
	{
		inserts = 0;
		updates = 0;
		reads = 0;
		deletes = 0;
	}
	
	@Nullable
	private static Logger logger;
	private MysqlBaseSetup mysqlBaseSetup;
	
	public MysqlBaseHandler(Logger logger, MysqlBaseSetup mysqlSetup) 
	{
		MysqlBaseHandler.logger = logger;
		this.mysqlBaseSetup = mysqlSetup;
	}
	
	@Nullable
	public static Logger getLogger()
	{
		return MysqlBaseHandler.logger;
	}
	
	private PreparedStatement getPreparedStatement(Connection conn, String sql, int count, Object... whereObject) throws SQLException
	{
		PreparedStatement ps = conn.prepareStatement(sql);
		int i = count;
        for(Object o : whereObject)
        {
        	ps.setObject(i, o);
        	i++;
        }
        return ps;
	}
	
	public boolean exist(MysqlType type, String whereColumn, Object... whereObject)
	{
		//All Object which leaves the try-block, will be closed. So conn and ps is closed after the methode
		//No finally needed.
		//So much as possible in async methode use
		try (Connection conn = mysqlBaseSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT `id` FROM `" + type.getValue()+ "` WHERE "+whereColumn+" LIMIT 1",
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return true;
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(logger, Level.WARNING, "Could not check "+type.getObject().getClass().getName()+" Object if it exist!", e);
			  }
		}
		return false;
	}
	
	public boolean create(MysqlType type, Object object)
	{
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = mysqlBaseSetup.getConnection();)
			{
				mh.create(conn, type.getValue());
				return true;
			} catch (Exception e)
			{
				mh.log(logger, Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return false;
	}
	
	public boolean updateData(MysqlType type, Object object, String whereColumn, Object... whereObject)
	{
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = mysqlBaseSetup.getConnection();)
			{
				mh.update(conn, type.getValue(), whereColumn, whereObject);
				return true;
			} catch (Exception e)
			{
				mh.log(logger, Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return false;
	}
	
	public Object getData(MysqlType type, String whereColumn, Object... whereObject)
	{
		Object object = type.getObject();
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = mysqlBaseSetup.getConnection();)
			{
				ArrayList<Object> list = mh.get(conn, type.getValue(), "`id` ASC", " Limit 1", whereColumn, whereObject);
				if(!list.isEmpty())
				{
					return list.get(0);
				}
			} catch (Exception e)
			{
				mh.log(logger, Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return null;
	}
	
	public int deleteData(MysqlType type, String whereColumn, Object... whereObject)
	{
		try (Connection conn = mysqlBaseSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"DELETE FROM `" + type.getValue() + "` WHERE "+whereColumn,
					1,
					whereObject);
	        int d = ps.executeUpdate();
			MysqlBaseHandler.addRows(QueryType.DELETE, d);
			return d;
	    } catch (SQLException e) 
		{
	    	if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(logger, Level.WARNING, "Could not delete "+type.getObject().getClass().getName()+" Object!", e);
			  }
		}
		return 0;
	}
	
	public int lastID(MysqlType type)
	{
		try (Connection conn = mysqlBaseSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT `id` FROM `" + type.getValue() + "` ORDER BY `id` DESC LIMIT 1",
					1);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt("id");
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(logger, Level.WARNING, "Could not get last id from "+type.getObject().getClass().getName()+" Object table!", e);
			  }
		}
		return 0;
	}
	
	public int getCount(MysqlType type, String whereColumn, Object... whereObject)
	{
		try (Connection conn = mysqlBaseSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					" SELECT count(*) FROM `" + type.getValue() + "` WHERE "+whereColumn,
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt(1);
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(logger, Level.WARNING, "Could not count "+type.getObject().getClass().getName()+" Object!", e);
			  }
		}
		return 0;
	}
	
	public double getSum(MysqlType type, String whereColumn, Object... whereObject)
	{
		try (Connection conn = mysqlBaseSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT sum("+whereColumn+") FROM `" + type.getValue() + "` WHERE 1",
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt(1);
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(logger, Level.WARNING, "Could not summarized "+type.getObject().getClass().getName()+" Object!", e);
			  }
		}
		return 0;
	}
	
	public ArrayList<Object> getList(MysqlType type, String orderByColumn, int start, int quantity, String whereColumn, Object...whereObject)
	{
		Object object = type.getObject();
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = mysqlBaseSetup.getConnection();)
			{
				ArrayList<Object> list = mh.get(conn, type.getValue(), orderByColumn, " Limit "+start+", "+quantity, whereColumn, whereObject);
				if(!list.isEmpty())
				{
					return list;
				}
			} catch (Exception e)
			{
				mh.log(logger, Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return new ArrayList<>();
	}
	
	public ArrayList<Object> getFullList(MysqlType type, String orderByColumn,
			String whereColumn, Object...whereObject)
	{
		Object object = type.getObject();
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = mysqlBaseSetup.getConnection();)
			{
				ArrayList<Object> list = mh.get(conn, type.getValue(), orderByColumn, "", whereColumn, whereObject);
				if(!list.isEmpty())
				{
					return list;
				}
			} catch (Exception e)
			{
				mh.log(logger, Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return new ArrayList<>();
	}
	
	//INFO:Alt lasten, aber noch wichtig
	public long getSumII(String groupBy, String sumColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = mysqlBaseSetup.getConnection();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (conn != null) 
		{
			try 
			{
				String sql = "SELECT "+groupBy+", SUM("+sumColumn+") as ergebnis FROM `" + MysqlType.TIMERECORD.getValue()
						+"` WHERE "+whereColumn+" GROUP BY "+groupBy;
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        result = preparedStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getLong("ergebnis");
		        }
		    } catch (SQLException e) 
			{
				  logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	public int getTopListPlaceI(String orderByColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = mysqlBaseSetup.getConnection();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (conn != null) 
		{
			try 
			{
				//whereColumn: for example: `afktime` > xxx
				String sql = " SELECT count(*) FROM `" + MysqlType.PLUGINUSER.getValue()
						+"` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        
		        result = preparedStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getInt(1) + 1;
		        }
		    } catch (SQLException e) 
			{
				  logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	public int getCountII(String orderByColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = mysqlBaseSetup.getConnection();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (conn != null) 
		{
			try 
			{
				String sql = " SELECT count(*) FROM `" + MysqlType.TIMERECORD.getValue()
						+"` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        
		        result = preparedStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getInt(1);
		        }
		    } catch (SQLException e) 
			{
				  logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	public ArrayList<PluginUser> getTop(String orderByColumn, boolean desc, int start, int end)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = mysqlBaseSetup.getConnection();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		if (conn != null) 
		{
			try 
			{
				String sql = "";
				if(desc)
				{
					sql = "SELECT * FROM `" + MysqlType.PLUGINUSER.getValue()
							+ "` ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
				} else
				{
					sql = "SELECT * FROM `" + MysqlType.PLUGINUSER.getValue()
							+ "` ORDER BY "+orderByColumn+" ASC LIMIT "+start+", "+end;
				}
				
		        preparedStatement = conn.prepareStatement(sql);
		        
		        result = preparedStatement.executeQuery();
		        ArrayList<PluginUser> list = new ArrayList<PluginUser>();
		        while (result.next()) 
		        {
		        	PluginUser ep = new PluginUser(
		        			UUID.fromString(result.getString("player_uuid")),
		        			result.getString("player_name"),
		        			result.getLong("lasttimecheck"),
		        			result.getLong("activitytime"),
		        			result.getLong("afktime"),
		        			result.getLong("alltime"),
		        			result.getLong("lastactivity"),
		        			result.getBoolean("isafk"),
		        			result.getBoolean("isonline"),
		        			result.getLong("vacationtime"));
		        	list.add(ep);
		        }
		        return list;
		    } catch (SQLException e) 
			{
				  logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return null;
	}
}
