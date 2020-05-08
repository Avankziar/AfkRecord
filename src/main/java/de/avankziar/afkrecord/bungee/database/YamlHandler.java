package main.java.de.avankziar.afkrecord.bungee.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class YamlHandler 
{
	private AfkRecord plugin;
	public Configuration cfg;
	public Configuration lgg;
	private String languages;
	
	public YamlHandler(AfkRecord plugin)
	{
		this.plugin= plugin;
		loadYamlHandler();
	}
	
	public boolean loadYamlHandler()
	{
		if(!mkdir())
		{
			return false;
		}
		if(!loadYaml())
		{
			return false;
		}
		languages = cfg.getString("Language", "English");
		return true;
	}
	
	public boolean loadYaml()
	 {
		 try 
		 {
			 cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
		 } catch (IOException e) 
		 {
			 e.printStackTrace();
			 return false;
		 }
		 try 
		 {
			 lgg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "language.yml"));
		 } catch (IOException e) 
		 {
			 e.printStackTrace();
			 return false;
		 }
		 return true;
	 }
		
	 public void saveConfig()
	 {
		 try {
			 ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, new File(plugin.getDataFolder(), "config.yml"));
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }
	 
	 public void saveLanguage()
	 {
		 try {
			 ConfigurationProvider.getProvider(YamlConfiguration.class).save(lgg, new File(plugin.getDataFolder(), "language.yml"));
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }
	 
	 public boolean mkdir()
	 {
		 if (!plugin.getDataFolder().exists())
		 {
			 plugin.getDataFolder().mkdir();
		 } 
	     File c = new File(plugin.getDataFolder(), "config.yml");  
	     if (!c.exists()) 
	     {
	    	 try (InputStream in = plugin.getResourceAsStream("config.yml")) 
	    	 {       
	    		 Files.copy(in, c.toPath());
	         } catch (IOException e) 
	    	 {
	        	 e.printStackTrace();
	        	 return false;
	         }
	     }
	     
	     File l = new File(plugin.getDataFolder(), "language.yml");  
	     if (!l.exists()) 
	     {
	    	 try (InputStream in = plugin.getResourceAsStream("language.yml")) 
	    	 {       
	    		 Files.copy(in, l.toPath());
	         } catch (IOException e) 
	    	 {
	        	 e.printStackTrace();
	        	 return false;
	         }
	     } 
	     return true;
	 }
	 
	 public Configuration get()
	 {
		 return cfg;
	 }
	 
	 public Configuration getL()
	 {
		 return lgg;
	 }

	public String getLanguages()
	{
		return languages;
	}
}
