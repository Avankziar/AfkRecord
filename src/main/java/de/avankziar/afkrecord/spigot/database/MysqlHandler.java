package main.java.de.avankziar.afkrecord.spigot.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.TimeHandler;
import main.java.de.avankziar.afkrecord.spigot.database.tables.TableI;
import main.java.de.avankziar.afkrecord.spigot.database.tables.TableII;

public class MysqlHandler implements TableI, TableII
{
	public enum Type
	{
		PLUGINUSER, TIMERECORD;
	}
	
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
		tableNameI = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameI");
		if(tableNameI == null)
		{
			return false;
		}
		tableNameII = plugin.getYamlHandler().getConfig().getString("Mysql.TableNameII");
		if(tableNameII == null)
		{
			return false;
		}
		return true;
	}
	
	public boolean exist(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.existI(plugin, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.existII(plugin, whereColumn, whereObject);
		}
		return false;
	}
	
	public boolean create(Type type, Object object)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.createI(plugin, object);
		case TIMERECORD:
			return TableII.super.createII(plugin, object);
		}
		return false;
	}
	
	public boolean updateData(Type type, Object object, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.updateDataI(plugin, object, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.updateDataII(plugin, object, whereColumn, whereObject);
		}
		return false;
	}
	
	public Object getData(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getDataI(plugin, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.getDataII(plugin, whereColumn, whereObject);
		}
		return null;
	}
	
	public boolean deleteData(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.deleteDataI(plugin, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.deleteDataII(plugin, whereColumn, whereObject);
		}
		return false;
	}
	
	public int lastID(Type type)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.lastIDI(plugin);
		case TIMERECORD:
			return TableII.super.lastIDII(plugin);
		}
		return 0;
	}
	
	public int countWhereID(Type type, String whereColumn, Object... whereObject)
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.countWhereIDI(plugin, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.countWhereIDII(plugin, whereColumn, whereObject);
		}
		return 0;
	}
	
	public ArrayList<?> getList(Type type, String orderByColumn,
			boolean desc, int start, int quantity, String whereColumn, Object...whereObject) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getListI(plugin, orderByColumn, desc, start, quantity, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.getListII(plugin, orderByColumn, desc, start, quantity, whereColumn, whereObject);
		}
		return null;
	}
	
	public ArrayList<?> getTop(Type type, String orderByColumn, boolean desc, int start, int amount) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getTopI(plugin, orderByColumn, desc, start, amount);
		case TIMERECORD:
			return TableII.super.getTopII(plugin, orderByColumn, desc, start, amount);
		}
		return null;
	}
	
	public ArrayList<?> getAllListAt(Type type, String orderByColumn,
			boolean desc, String whereColumn, Object...whereObject) throws IOException
	{
		switch(type)
		{
		case PLUGINUSER:
			return TableI.super.getAllListAtI(plugin, orderByColumn, desc, whereColumn, whereObject);
		case TIMERECORD:
			return TableII.super.getAllListAtII(plugin, orderByColumn, desc, whereColumn, whereObject);
		}
		return null;
	}
	
	public void startConvert(int lastid)
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
					return;
				}
				convertII(start, amount);
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
				String sql = "SELECT `datum`, `id` FROM `" + tableNameII + "` WHERE `timestamp_unix` = ? ORDER BY `id` ASC LIMIT "+start+", "+amount;
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
				String data = "UPDATE `" + tableNameII
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
}
