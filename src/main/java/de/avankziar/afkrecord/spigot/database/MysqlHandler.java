package main.java.de.avankziar.afkrecord.spigot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.object.PlayerInfo;
import main.java.de.avankziar.afkrecord.spigot.object.PlayerInfoEx;
import main.java.de.avankziar.afkrecord.spigot.object.TopList;

public class MysqlHandler 
{
	private AfkRecord plugin;
	public String tableNameI;
	public String tableNameII;
	
	public MysqlHandler(AfkRecord plugin) 
	{
		this.plugin = plugin;
		loadMysqlHandler();
	}
	
	public boolean loadMysqlHandler()
	{
		tableNameI = plugin.getYamlHandler().get().getString("Mysql.TableNameI");
		if(tableNameI == null)
		{
			return false;
		}
		tableNameII = plugin.getYamlHandler().get().getString("Mysql.TableNameII");
		if(tableNameII == null)
		{
			return false;
		}
		return true;
	}
	
	public boolean hasAccount(Player player) 
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `player_uuid` FROM `" + tableNameI + "` WHERE `player_uuid` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.getUniqueId().toString());
		        
		        result = preparedUpdateStatement.executeQuery();
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
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return false;
	}
	
	public boolean hasAccount(String player) 
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `player_uuid` FROM `" + tableNameI + "` WHERE `player_uuid` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player);
		        
		        result = preparedUpdateStatement.executeQuery();
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
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return false;
	}
	
	public boolean existDate(Player player, String date) 
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `player_uuid` FROM `" + tableNameII + "` WHERE `player_uuid` = ? AND `datum` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.getUniqueId().toString());
		        preparedUpdateStatement.setString(2, date);
		        
		        result = preparedUpdateStatement.executeQuery();
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
		    		  if (preparedUpdateStatement != null) 
		    		  {
		    			  preparedUpdateStatement.close();
		    		  }
		    	  } catch (Exception e) {
		    		  e.printStackTrace();
		    	  }
		      }
		}
		return false;
	}
	
	public boolean createAccount(Player player) 
	{
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) {
			try 
			{
				String sql = "INSERT INTO `" + tableNameI 
						+ "`(`player_uuid`, `player_name`, `alltime`, `activitytime`, `afktime`, `lastactivity`, `isafk`) " 
						+ "VALUES(?, ?, ?, ?, ?, ?, ?)";
				preparedStatement = conn.prepareStatement(sql);
		        preparedStatement.setString(1, player.getUniqueId().toString());
		        preparedStatement.setString(2, player.getName());
		        preparedStatement.setLong(3, 0L);
		        preparedStatement.setLong(4, 0L);
		        preparedStatement.setLong(5, 0L);
		        preparedStatement.setLong(6, System.currentTimeMillis());
		        preparedStatement.setBoolean(7, false);
		        
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
	
	public boolean createDate(Player player) 
	{
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) {
			try 
			{
				String sql = "INSERT INTO `" + tableNameII 
						+ "`(`player_uuid`, `player_name`, `datum`, `alltime`, `activitytime`, `afktime`) " 
						+ "VALUES(?, ?, ?, ?, ?, ?)";
				preparedStatement = conn.prepareStatement(sql);
		        preparedStatement.setString(1, player.getUniqueId().toString());
		        preparedStatement.setString(2, player.getName());
		        preparedStatement.setString(3, plugin.getUtility().getDate());
		        preparedStatement.setLong(4, 0L);
		        preparedStatement.setLong(5, 0L);
		        preparedStatement.setLong(6, 0L);
		        
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
	
	public boolean updateDataI(Player player, Object object, String selectcolumn) 
	{
		if (!hasAccount(player)) 
		{
			createAccount(player);
		}
		PreparedStatement preparedUpdateStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + tableNameI
						+ "` " + "SET `" + selectcolumn + "` = ?" + " WHERE `player_uuid` = ?";
				preparedUpdateStatement = conn.prepareStatement(data);
				preparedUpdateStatement.setObject(1, object);
				preparedUpdateStatement.setString(2, player.getUniqueId().toString());
				
				preparedUpdateStatement.executeUpdate();
				return true;
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
        return false;
	}
	
	public boolean updateDataI(String player, Object object, String selectcolumn) 
	{
		PreparedStatement preparedUpdateStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + tableNameI
						+ "` " + "SET `" + selectcolumn + "` = ?" + " WHERE `player_uuid` = ?";
				preparedUpdateStatement = conn.prepareStatement(data);
				preparedUpdateStatement.setObject(1, object);
				preparedUpdateStatement.setString(2, player);
				
				preparedUpdateStatement.executeUpdate();
				return true;
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
        return false;
	}
	
	public boolean updateDataII(Player player, Object object, String selectcolumn, String date) 
	{
		if (!existDate(player, date)) 
		{
			createDate(player);
		}
		PreparedStatement preparedUpdateStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String data = "UPDATE `" + tableNameII
						+ "` " + "SET `" + selectcolumn + "` = ?" + " WHERE `player_uuid` = ? AND `datum` = ?";
				preparedUpdateStatement = conn.prepareStatement(data);
				preparedUpdateStatement.setObject(1, object);
				preparedUpdateStatement.setString(2, player.getUniqueId().toString());
				preparedUpdateStatement.setString(3, date);
				
				preparedUpdateStatement.executeUpdate();
				return true;
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
        return false;
	}
	
	public Object getDataI(Player player, String selectcolumn, String wherecolumn)
	{
		if (!hasAccount(player)) 
		{
			createAccount(player);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `" + selectcolumn + "` FROM `" + tableNameI + "` WHERE `" + wherecolumn + "` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.getUniqueId().toString());
		        
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getObject(selectcolumn);
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
		return null;
	}
	
	public Object getDataII(Player player, String selectcolumn, String date)
	{
		if (!existDate(player, date)) 
		{
			createDate(player);
		}
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `" + selectcolumn + "` FROM `" + tableNameII +
						"` WHERE `player_uuid` = ? AND `datum` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.getUniqueId().toString());
		        preparedUpdateStatement.setString(2, date);
		        
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getObject(selectcolumn);
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
		return null;
	}
	
	public Object getDataI(String player, String selectcolumn, String wherecolumn)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `" + selectcolumn + "` FROM `" + tableNameI + "` WHERE `" + wherecolumn + "` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player);
		        
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getObject(selectcolumn);
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
		return null;
	}
	
	public Object getDataII(String player, String selectcolumn, String date)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT `" + selectcolumn + "` FROM `" + tableNameII +
						"` WHERE `player_uuid` = ? AND `datum` = ? LIMIT 1";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player);
		        preparedUpdateStatement.setString(2, date);
		        
		        result = preparedUpdateStatement.executeQuery();
		        while (result.next()) 
		        {
		        	return result.getObject(selectcolumn);
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
		return null;
	}
	
	public ArrayList<TopList> getTop(String toplist)
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{
				String sql = "SELECT * FROM `" + tableNameI + "` ORDER BY "+toplist+" DESC";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        
		        result = preparedUpdateStatement.executeQuery();
		        ArrayList<TopList> a = new ArrayList<>();
		        int i = 1;
		        while (result.next()) 
		        {
		        	if(result.getString("player_name")!=null && result.getString(toplist)!=null)
		        	{
		        		TopList tl = new TopList(i, result.getString("player_name"), result.getLong(toplist));
			        	a.add(tl);
			        	i++;
		        	}
		        }
		        return a;
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
		return null;
	}
	
	public ArrayList<PlayerInfo> getListII(String player) //TODO der geht nach tagen und nicht nach monaten vor
	{
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + tableNameII + "` WHERE `player_uuid` = ? ORDER BY `id` DESC";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player);
		        
		        result = preparedUpdateStatement.executeQuery();
		        ArrayList<PlayerInfo> a = new ArrayList<>();
		        while (result.next()) 
		        {
		        	a.add(new PlayerInfo(result.getString("datum"),
		        			result.getLong("activitytime"),
		        			result.getLong("afktime"), 
		        			result.getLong("alltime")));
		        }
		        return a;
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
		return null;
	}
	
	public PlayerInfo getCountTime(String player, int days)
	{
		String nowdates = plugin.getUtility().getDate();
		String olddates = plugin.getUtility().addingDaysToDate(nowdates, -days);
		long olddate = plugin.getUtility().getDateInLong(olddates);
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + tableNameII + "` WHERE `player_uuid` = ? ORDER BY `id` DESC";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player);
		        
		        result = preparedUpdateStatement.executeQuery();
		        PlayerInfo pi = new PlayerInfo(olddates, 0, 0, 0);
		        while (result.next()) 
		        {
		        	long current = plugin.getUtility().getDateInLong(result.getString("datum"));
		        	if(olddate<=current)
		        	{
		        		long acc = 0;
		        		long afkk = 0;
		        		long alll = 0;
		        		if(result.getLong("activitytime")!=0)
			        	{
			        		acc = result.getLong("activitytime");
			        	}
		        		if(result.getLong("afktime")!=0)
			        	{
			        		afkk = result.getLong("afktime");
			        	}
		        		if(result.getLong("alltime")!=0)
			        	{
			        		alll = result.getLong("alltime");
			        	}
		        		long ac = acc + pi.getActivitytime();
			        	pi.setActivitytime(ac);
			        	long afk = afkk + pi.getAfktime();
			        	pi.setAfktime(afk);
			        	long all = alll + pi.getAlltime();
			        	pi.setAlltime(all);
		        	} else
		        	{
		        		break;
		        	}
		        }
		        return pi;
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
		return null;
	}
	
	public ArrayList<PlayerInfoEx> getCountTimeList(int days, long min, String column)
	{
		String nowdates = plugin.getUtility().getDate();
		String olddates = plugin.getUtility().addingDaysToDate(nowdates, -days);
		long olddate = plugin.getUtility().getDateInLong(olddates);
		PreparedStatement preparedUpdateStatement = null;
		ResultSet result = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `" + tableNameII 
						+ "` WHERE `"+column+"` < ? ORDER BY `"+column+"` DESC";
		        preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, column);
		        
		        result = preparedUpdateStatement.executeQuery();
		        LinkedHashMap<String, PlayerInfoEx> list = new LinkedHashMap<>();
		        while (result.next()) 
		        {
		        	PlayerInfoEx pi = new PlayerInfoEx("", "",olddates, 0, 0, 0);
		        	String uuid = result.getString("player_uuid");
		        	if(list.containsKey(uuid))
		        	{
		        		pi = list.get(uuid);
		        	}
		        	long current = plugin.getUtility().getDateInLong(result.getString("datum"));
		        	if(olddate<=current)
		        	{
		        		long acc = 0;
		        		long afkk = 0;
		        		long alll = 0;
		        		if(result.getLong("activitytime")!=0)
			        	{
			        		acc = result.getLong("activitytime");
			        	}
		        		if(result.getLong("afktime")!=0)
			        	{
			        		afkk = result.getLong("afktime");
			        	}
		        		if(result.getLong("alltime")!=0)
			        	{
			        		alll = result.getLong("alltime");
			        	}
		        		pi.setUuid(uuid);
		        		pi.setPlayername(result.getString("player_name"));
		        		long ac = acc + pi.getActivitytime();
			        	pi.setActivitytime(ac);
			        	long afk = afkk + pi.getAfktime();
			        	pi.setAfktime(afk);
			        	long all = alll + pi.getAlltime();
			        	pi.setAlltime(all);
			        	if(list.containsKey(uuid))
			        	{
			        		list.replace(uuid, pi);
			        	} else
			        	{
			        		list.put(uuid, pi);
			        	}
		        	} else
		        	{
		        		break;
		        	}
		        }
		        ArrayList<PlayerInfoEx> playerlist = new ArrayList<>();
		        playerlist.addAll(list.values());
		        return playerlist;
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
		return null;
	}
	
	public void deleteData(Object object, String wherecolumn, String tableName)
	{
		PreparedStatement preparedStatement = null;
		Connection conn = plugin.getMysqlSetup().getConnection();
		try 
		{
			String sql = "DELETE FROM `" + tableName + "` WHERE `" + wherecolumn + "` = ?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setObject(1, object);
			preparedStatement.execute();
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
	}
}
