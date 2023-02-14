package main.java.de.avankziar.afkrecord.spigot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;
import main.java.de.avankziar.afkrecord.spigot.object.TimeRecord;

public class MysqlHandler
{
	public enum Type
	{
		PLUGINUSER("afkrecordPlayerData", new PluginUser()),
		TIMERECORD("afkrecordDateList", new TimeRecord())
		;
		
		private Type(String value, Object object)
		{
			this.value = value;
			this.object = object;
		}
		
		private final String value;
		private final Object object;

		public String getValue()
		{
			return value;
		}
		
		public Object getObject()
		{
			return object;
		}
	}
	
	public enum QueryType
	{
		INSERT, UPDATE, DELETE, READ;
	}
	
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
	
	private AfkRecord plugin;
	
	public MysqlHandler(AfkRecord plugin) 
	{
		this.plugin = plugin;
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
	
	public boolean exist(Type type, String whereColumn, Object... whereObject)
	{
		//All Object which leaves the try-block, will be closed. So conn and ps is closed after the methode
		//No finally needed.
		//So much as possible in async methode use
		try (Connection conn = plugin.getMysqlSetup().getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT `id` FROM `" + type.getValue()+ "` WHERE "+whereColumn+" LIMIT 1",
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return true;
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(Level.WARNING, "Could not check "+type.getObject().getClass().getName()+" Object if it exist!", e);
			  }
		}
		return false;
	}
	
	public boolean create(Type type, Object object)
	{
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = plugin.getMysqlSetup().getConnection();)
			{
				mh.create(conn, type.getValue());
				return true;
			} catch (Exception e)
			{
				mh.log(Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return false;
	}
	
	public boolean updateData(Type type, Object object, String whereColumn, Object... whereObject)
	{
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = plugin.getMysqlSetup().getConnection();)
			{
				mh.update(conn, type.getValue(), whereColumn, whereObject);
				return true;
			} catch (Exception e)
			{
				mh.log(Level.WARNING, "Could not update "+object.getClass().getName()+" Object!", e);
			}
		}
		return false;
	}
	
	public Object getData(Type type, String whereColumn, Object... whereObject)
	{
		Object object = type.getObject();
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = plugin.getMysqlSetup().getConnection();)
			{
				ArrayList<Object> list = mh.get(conn, type.getValue(), "`id` ASC", " Limit 1", whereColumn, whereObject);
				if(!list.isEmpty())
				{
					return list.get(0);
				}
			} catch (Exception e)
			{
				mh.log(Level.WARNING, "Could not getData "+object.getClass().getName()+" Object!", e);
			}
		}
		return null;
	}
	
	public int deleteData(Type type, String whereColumn, Object... whereObject)
	{
		try (Connection conn = plugin.getMysqlSetup().getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"DELETE FROM `" + type.getValue() + "` WHERE "+whereColumn,
					1,
					whereObject);
	        int d = ps.executeUpdate();
			MysqlHandler.addRows(QueryType.DELETE, d);
			return d;
	    } catch (SQLException e) 
		{
	    	if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(Level.WARNING, "Could not delete "+type.getObject().getClass().getName()+" Object!", e);
			  }
		}
		return 0;
	}
	
	public int lastID(Type type)
	{
		try (Connection conn = plugin.getMysqlSetup().getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT `id` FROM `" + type.getValue() + "` ORDER BY `id` DESC LIMIT 1",
					1);
	        ResultSet rs = ps.executeQuery();
	        MysqlHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt("id");
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(Level.WARNING, "Could not get last id from "+type.getObject().getClass().getName()+" Object table!", e);
			  }
		}
		return 0;
	}
	
	public int getCount(Type type, String whereColumn, Object... whereObject)
	{
		try (Connection conn = plugin.getMysqlSetup().getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					" SELECT count(*) FROM `" + type.getValue() + "` WHERE "+whereColumn,
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt(1);
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(Level.WARNING, "Could not count "+type.getObject().getClass().getName()+" Object!", e);
			  }
		}
		return 0;
	}
	
	public double getSum(Type type, String whereColumn, Object... whereObject)
	{
		try (Connection conn = plugin.getMysqlSetup().getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT sum("+whereColumn+") FROM `" + type.getValue() + "` WHERE 1",
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt(1);
	        }
	    } catch (SQLException e) 
		{
			  if(type.getObject() instanceof MysqlHandable)
			  {
				  MysqlHandable mh = (MysqlHandable) type.getObject();
				  mh.log(Level.WARNING, "Could not summarized "+type.getObject().getClass().getName()+" Object!", e);
			  }
		}
		return 0;
	}
	
	public ArrayList<Object> getList(Type type, String orderByColumn, int start, int quantity, String whereColumn, Object...whereObject)
	{
		Object object = type.getObject();
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = plugin.getMysqlSetup().getConnection();)
			{
				ArrayList<Object> list = mh.get(conn, type.getValue(), orderByColumn, " Limit "+start+", "+quantity, whereColumn, whereObject);
				if(!list.isEmpty())
				{
					return list;
				}
			} catch (Exception e)
			{
				mh.log(Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return new ArrayList<>();
	}
	
	public ArrayList<Object> getFullList(Type type, String orderByColumn,
			String whereColumn, Object...whereObject)
	{
		Object object = type.getObject();
		if(object instanceof MysqlHandable)
		{
			MysqlHandable mh = (MysqlHandable) object;
			try (Connection conn = plugin.getMysqlSetup().getConnection();)
			{
				ArrayList<Object> list = mh.get(conn, type.getValue(), orderByColumn, "", whereColumn, whereObject);
				if(!list.isEmpty())
				{
					return list;
				}
			} catch (Exception e)
			{
				mh.log(Level.WARNING, "Could not create "+object.getClass().getName()+" Object!", e);
			}
		}
		return new ArrayList<>();
	}
	
	//Ab hier Altlasten
	
	public void startConvert(final Player player, int lastid)
	{
		new BukkitRunnable()
		{
			int start = 0;
			final int amount = 15;
			@Override
			public void run()
			{
				if(start >= lastid)
				{
					cancel();
					if(player != null)
					{
						player.sendMessage(ChatApi.tl("&6Convert finish!"));
					}
					return;
				}
				convertII(0, amount);
				start += amount;
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 5L);
	}
	
	private void convertII(int start, int amount)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `datum`, `id` FROM `" + Type.TIMERECORD.getValue() 
				+ "` WHERE `timestamp_unix` = ? ORDER BY `id` ASC LIMIT "+start+", "+amount;
		        
				preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setLong(1, 0);
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	int id = result.getInt("id");
		        	String datum = result.getString("datum");
		        	Long l = TimeHandler.getDate(datum);
		        	convertIIPost(id, l, datum);
		        }
		    } catch (SQLException e) 
			{
				  AfkRecord.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (result != null) 
		    		  {
		    			  result.close();
		    		  }
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return;
	}
	
	private void convertIIPost(int id, long l, String datum) 
	{
		PreparedStatement preparedUpdateStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + Type.TIMERECORD.getValue()
						+ "` " + "SET `timestamp_unix` = ?" + " WHERE `id` = ? AND `datum` = ?";
				preparedUpdateStatement = conn.prepareStatement(data);
				preparedUpdateStatement.setLong(1, l);
				preparedUpdateStatement.setInt(2, id);
				preparedUpdateStatement.setString(3, datum);
				preparedUpdateStatement.executeUpdate();
				return;
			} catch (SQLException e) {
				AfkRecord.log.warning("Error: " + e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (preparedUpdateStatement != null) {
						preparedUpdateStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
        return;
	}
	
	public long getSumII(AfkRecord plugin, String groupBy, String sumColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String sql = "SELECT "+groupBy+", SUM("+sumColumn+") as ergebnis FROM `" + Type.TIMERECORD.getValue()
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
				  AfkRecord.log.warning("Error: " + e.getMessage());
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
	
	public int getTopListPlaceI(AfkRecord plugin, String orderByColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				//whereColumn: for example: `afktime` > xxx
				String sql = " SELECT count(*) FROM `" + Type.PLUGINUSER.getValue()
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
				  AfkRecord.log.warning("Error: " + e.getMessage());
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
	
	public int getCountII(AfkRecord plugin, String orderByColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String sql = " SELECT count(*) FROM `" + Type.TIMERECORD.getValue()
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
				  AfkRecord.log.warning("Error: " + e.getMessage());
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
	
	public ArrayList<PluginUser> getTop(AfkRecord plugin, String orderByColumn, boolean desc, int start, int end)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String sql = "";
				if(desc)
				{
					sql = "SELECT * FROM `" + Type.PLUGINUSER.getValue()
							+ "` ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
				} else
				{
					sql = "SELECT * FROM `" + Type.PLUGINUSER.getValue()
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
				  AfkRecord.log.warning("Error: " + e.getMessage());
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
