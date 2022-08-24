package main.java.de.avankziar.afkrecord.bungee.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;
import main.java.de.avankziar.afkrecord.bungee.database.Language.ISO639_2B;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class YamlHandler
{
	private AfkRecord plugin;
	private File config = null;
	private Configuration cfg = new Configuration();
	
	private String languages;
	private File language = null;
	private Configuration lang = new Configuration();

	public YamlHandler(AfkRecord plugin)
	{
		this.plugin = plugin;
		loadYamlHandler();
	}
	
	public Configuration getConfig()
	{
		return cfg;
	}
	
	public Configuration getLang()
	{
		return lang;
	}
	
	public boolean loadYamlHandler()
	{
		/*
		 * Init all path from all yamls files
		 */
		plugin.setYamlManager(new YamlManager());
		/*
		 * Load all files, which are unique, for examples config.yml, commands.yml etc.
		 */
		if(!mkdirStaticFiles())
		{
			return false;
		}
		if(!mkdirDynamicFiles())
		{
			return false;
		}
		return true;
	}
	
	public boolean mkdirStaticFiles()
	{
		/*
		 * Create the plugin general directory
		 */
		File directory = new File(plugin.getDataFolder()+"");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		/*
		 * Init config.yml
		 */
		config = new File(plugin.getDataFolder(), "config.yml");
		if(!config.exists()) 
		{
			AfkRecord.log.info("Create config.yml...");
			/*
			 * If config.yml dont exist in the main directory, than create config.yml as empty file
			 */
			 try (InputStream in = plugin.getResourceAsStream("default.yml")) 
	    	 {       
	    		 Files.copy(in, config.toPath());
	         } catch (IOException e) 
	    	 {
	        	 e.printStackTrace();
	        	 return false;
	         }
		}
		/*
		 * Load the config.yml
		 */
		cfg = loadYamlTask(config, cfg);
		/*
		 * Write all path for the configfile
		 * Make sure, you use the right linkedHashmap from the YamlManager
		 */
		writeFile(config, cfg, plugin.getYamlManager().getConfigKey());
		
		languages = plugin.getAdministration() == null 
				? cfg.getString("Language", "ENG").toUpperCase() 
				: plugin.getAdministration().getLanguage();
		return true;
	}
	
	private boolean mkdirDynamicFiles()
	{
		List<Language.ISO639_2B> types = new ArrayList<Language.ISO639_2B>(EnumSet.allOf(Language.ISO639_2B.class));
		ISO639_2B languageType = ISO639_2B.ENG;
		for(ISO639_2B type : types)
		{
			if(type.toString().equals(languages))
			{
				languageType = type;
				break;
			}
		}
		plugin.getYamlManager().setLanguageType(languageType);
		if(!mkdirLanguage())
		{
			return false;
		}
		return true;
	}
	
	private boolean mkdirLanguage()
	{
		String languageString = plugin.getYamlManager().getLanguageType().toString().toLowerCase();
		File directory = new File(plugin.getDataFolder()+"/Languages/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		language = new File(directory.getPath(), languageString+".yml");
		if(!language.exists()) 
		{
			AfkRecord.log.info("Create %lang%.yml...".replace("%lang%", languageString));
			 try (InputStream in = plugin.getResourceAsStream("default.yml")) 
	    	 {       
	    		 Files.copy(in, language.toPath());
	         } catch (IOException e) 
	    	 {
	        	 e.printStackTrace();
	        	 return false;
	         }
		}
		lang = loadYamlTask(language, lang);
		writeFile(language, lang, plugin.getYamlManager().getLanguageKey());
		return true;
	}
	
	private Configuration loadYamlTask(File file, Configuration yaml)
	{
		Configuration y = null;
		try 
		{
			yaml = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			AfkRecord.log.info("File "+file.getName()+" loaded!");
		} catch (IOException e) 
		{
			AfkRecord.log.severe(
					"Could not load the %file% file! You need to regenerate the %file%! Error: ".replace("%file%", file.getName())
					+ e.getMessage());
			e.printStackTrace();
		}
		y = yaml;
		return y;
	}
	
	private boolean writeFile(File file, Configuration yml, LinkedHashMap<String, Language> keyMap)
	{
		for(String key : keyMap.keySet())
		{
			Language languageObject = keyMap.get(key);
			if(languageObject.languageValues.containsKey(plugin.getYamlManager().getLanguageType()) == true)
			{
				plugin.getYamlManager().setFileInput(yml, keyMap, key, plugin.getYamlManager().getLanguageType());
			} else if(languageObject.languageValues.containsKey(plugin.getYamlManager().getDefaultLanguageType()) == true)
			{
				plugin.getYamlManager().setFileInput(yml, keyMap, key, plugin.getYamlManager().getDefaultLanguageType());
			}
		}
		try
		{
			 ConfigurationProvider.getProvider(YamlConfiguration.class).save(yml, file);
			 AfkRecord.log.info("File "+file.getName()+" saved!");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
	}
}