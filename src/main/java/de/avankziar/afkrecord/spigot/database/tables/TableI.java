package main.java.de.avankziar.afkrecord.spigot.database.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.object.PluginUser;

public interface TableI
{
	default boolean existI(AfkRecord plugin, String whereColumn, Object... object) 
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameI 
						+ "` WHERE "+whereColumn+" LIMIT 1";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : object)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        
		        result = preparedStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return true;
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
		return false;
	}
	
	default boolean createI(AfkRecord plugin, Object object) 
	{
		if(!(object instanceof PluginUser))
		{
			return false;
		}
		PluginUser cu = (PluginUser) object;
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) {
			try 
			{
				String sql = "INSERT INTO `" + plugin.getMysqlHandler().tableNameI 
						+ "`(`player_uuid`, `player_name`, `alltime`, `activitytime`, `afktime`,"
						+ " `lastactivity`, `lasttimecheck`, `isafk`, `isonline`) " 
						+ "VALUES("
						+ "?, ?, ?, ?, ?,"
						+ "?, ?, ?, ?"
						+ ")";
				preparedStatement = conn.prepareStatement(sql);
		        preparedStatement.setString(1, cu.getUUID().toString());
		        preparedStatement.setString(2, cu.getPlayerName());
		        preparedStatement.setLong(3, cu.getAllTime());
		        preparedStatement.setLong(4, cu.getActivityTime());
		        preparedStatement.setLong(5, cu.getAfkTime());
		        preparedStatement.setLong(6, cu.getLastActivity());
		        preparedStatement.setLong(7, cu.getLastTimeCheck());
		        preparedStatement.setBoolean(8, cu.isAFK());
		        preparedStatement.setBoolean(9, cu.isOnline());
		        
		        preparedStatement.executeUpdate();
		        return true;
		    } catch (SQLException e) 
			{
				  AfkRecord.log.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	  try 
		    	  {
		    		  if (preparedStatement != null) 
		    		  {
		    			  preparedStatement.close();
		    		  }
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return false;
	}
	
	default boolean updateDataI(AfkRecord plugin, Object object, String whereColumn, Object... whereObject) 
	{
		if(!(object instanceof PluginUser))
		{
			return false;
		}
		if(whereObject == null)
		{
			return false;
		}
		PluginUser cu = (PluginUser) object;
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + plugin.getMysqlHandler().tableNameI
						+ "` SET `player_uuid` = ?, `player_name` = ?,"
						+ " `alltime` = ?, `activitytime` = ?, `afktime` = ?,"
						+ " `lastactivity`= ?, `lasttimecheck` = ?, `isafk`= ?, `isonline` = ?" 
						+ " WHERE "+whereColumn;
				preparedStatement = conn.prepareStatement(data);
				preparedStatement.setString(1, cu.getUUID().toString());
			    preparedStatement.setString(2, cu.getPlayerName());
			    preparedStatement.setLong(3, cu.getAllTime());
			    preparedStatement.setLong(4, cu.getActivityTime());
			    preparedStatement.setLong(5, cu.getAfkTime());
			    preparedStatement.setLong(6, cu.getLastActivity());
			    preparedStatement.setLong(7, cu.getLastTimeCheck());
		        preparedStatement.setBoolean(8, cu.isAFK());
		        preparedStatement.setBoolean(9, cu.isOnline());
		        
		        int i = 10;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
				
				preparedStatement.executeUpdate();
				return true;
			} catch (SQLException e) {
				AfkRecord.log.warning("Error: " + e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (preparedStatement != null) 
					{
						preparedStatement.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
        return false;
	}
	
	default Object getDataI(AfkRecord plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI 
						+ "` WHERE "+whereColumn+" LIMIT 1";
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
		        	return new PluginUser(
		        			UUID.fromString(result.getString("player_uuid")),
		        			result.getString("player_name"),
		        			result.getLong("lasttimecheck"),
		        			result.getLong("activitytime"),
		        			result.getLong("afktime"),
		        			result.getLong("alltime"),
		        			result.getLong("lastactivity"),
		        			result.getBoolean("isafk"),
		        			result.getBoolean("isonline"));
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
		return null;
	}
	
	default int getTopListPlaceI(AfkRecord plugin, String orderByColumn, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				//whereColumn: for example: `afktime` > xxx
				String sql = " SELECT count(*) FROM `"+plugin.getMysqlHandler().tableNameI
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
	
	default boolean deleteDataI(AfkRecord plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		try 
		{
			String sql = "DELETE FROM `" + plugin.getMysqlHandler().tableNameI + "` WHERE "+whereColumn;
			preparedStatement = conn.prepareStatement(sql);
			int i = 1;
	        for(Object o : whereObject)
	        {
	        	preparedStatement.setObject(i, o);
	        	i++;
	        }
			preparedStatement.execute();
			return true;
		} catch (Exception e) 
		{
			e.printStackTrace();
		} finally 
		{
			try {
				if (preparedStatement != null) 
				{
					preparedStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	default int lastIDI(AfkRecord plugin)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameI + "` ORDER BY `id` DESC LIMIT 1";
		        preparedStatement = conn.prepareStatement(sql);
		        
		        result = preparedStatement.executeQuery();
		        while(result.next())
		        {
		        	return result.getInt("id");
		        }
		    } catch (SQLException e) 
			{
		    	e.printStackTrace();
		    	return 0;
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
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	default int countWhereIDI(AfkRecord plugin, String whereColumn, Object... whereObject)
	{
		PreparedStatement preparedStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `id` FROM `" + plugin.getMysqlHandler().tableNameI
						+ "` WHERE "+whereColumn
						+ " ORDER BY `id` DESC";
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
		        result = preparedStatement.executeQuery();
		        int count = 0;
		        while(result.next())
		        {
		        	count++;
		        }
		        return count;
		    } catch (SQLException e) 
			{
		    	e.printStackTrace();
		    	return 0;
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
		    	  } catch (Exception e) 
		    	  {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return 0;
	}
	
	default ArrayList<PluginUser> getListI(AfkRecord plugin, String orderByColumn, boolean desc,
			int start, int end, String whereColumn, Object...whereObject)
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
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" ASC LIMIT "+start+", "+end;
				}
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
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
		        			result.getBoolean("isonline"));
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
	
	default ArrayList<PluginUser> getTopI(AfkRecord plugin, String orderByColumn, boolean desc, int start, int end)
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
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI 
							+ "` ORDER BY "+orderByColumn+" DESC LIMIT "+start+", "+end;
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI 
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
		        			result.getBoolean("isonline"));
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
	
	default ArrayList<PluginUser> getAllListAtI(AfkRecord plugin, String orderByColumn,
			boolean desc, String whereColumn, Object...whereObject)
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
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" DESC";
				} else
				{
					sql = "SELECT * FROM `" + plugin.getMysqlHandler().tableNameI
							+ "` WHERE "+whereColumn+" ORDER BY "+orderByColumn+" ASC";
				}
		        preparedStatement = conn.prepareStatement(sql);
		        int i = 1;
		        for(Object o : whereObject)
		        {
		        	preparedStatement.setObject(i, o);
		        	i++;
		        }
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
		        			result.getBoolean("isonline"));
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