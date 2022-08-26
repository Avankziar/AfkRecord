package main.java.de.avankziar.afkrecord.bungee;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.de.avankziar.afkrecord.bungee.database.MysqlHandler;
import main.java.de.avankziar.afkrecord.bungee.database.MysqlSetup;
import main.java.de.avankziar.afkrecord.bungee.database.YamlHandler;
import main.java.de.avankziar.afkrecord.bungee.database.YamlManager;
import main.java.de.avankziar.afkrecord.bungee.listener.EventAfkCheck;
import main.java.me.avankziar.ifh.bungee.InterfaceHub;
import main.java.me.avankziar.ifh.bungee.administration.Administration;
import main.java.me.avankziar.ifh.bungee.plugin.RegisteredServiceProvider;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class AfkRecord extends Plugin
{
	private static AfkRecord plugin;
	public static Logger log;
	public static String pluginName = "AfkRecord";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private static Administration administrationConsumer;
	
	public void onEnable() 
	{
		plugin = this;
		log = getLogger();
		log.info("  █████╗ ███████╗██╗  ██╗██████╗  | API-Version: "+plugin.getDescription().getVersion());
		log.info(" ██╔══██╗██╔════╝██║ ██╔╝██╔══██╗ | Author: "+plugin.getDescription().getAuthor());
		log.info(" ███████║█████╗  █████╔╝ ██████╔╝ | Plugin Website: https://www.spigotmc.org/resources/afkrecord.74626/");
		log.info(" ██╔══██║██╔══╝  ██╔═██╗ ██╔══██╗ | Depend Plugins: "+plugin.getDescription().getDepends().toString());
		log.info(" ██║  ██║██║     ██║  ██╗██║  ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepends().toString());
		log.info(" ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝ | Have Fun^^ ");
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(plugin);
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean check = plugin.getAdministration() != null && plugin.getAdministration().getHost(path) != null;
		if(check || yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(plugin);
		} else
		{
			disablePlugin();
			log.severe("MySQL is not enabled! "+pluginName+" is disabled!");
		}
		getProxy().registerChannel("afkr:afkrecordout");
		getProxy().registerChannel("afkrecord:afkrecordin");
		getProxy().getPluginManager().registerListener(plugin, new EventAfkCheck());
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(plugin);
		//HandlerList.unregisterAll();
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			if (mysqlSetup.getConnection() != null) 
			{
				//backgroundtask.onShutDownDataSave();
			}
		}
		
		log.info(pluginName + " is disabled!");
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}
	
	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public static AfkRecord getPlugin()
	{
		return plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void disablePlugin()
	{
		Plugin plugin = (Plugin) ProxyServer.getInstance().getPluginManager().getPlugin(pluginName);
	       
		try
		{
			plugin.onDisable();
			for (Handler handler : plugin.getLogger().getHandlers())
			{
				handler.close();
			}
		}
		catch (Throwable t) {
			getLogger().log(Level.SEVERE, "Exception disabling plugin " + plugin.getDescription().getName(), t);
		}
		ProxyServer.getInstance().getPluginManager().unregisterCommands(plugin);
		ProxyServer.getInstance().getPluginManager().unregisterListeners(plugin);
		ProxyServer.getInstance().getScheduler().cancel(plugin);
		plugin.getExecutorService().shutdownNow();
	}
	
	private void setupIFHAdministration()
	{ 
		Plugin plugin = BungeeCord.getInstance().getPluginManager().getPlugin("InterfaceHub");
        if (plugin == null) 
        {
            return;
        }
        InterfaceHub ifh = (InterfaceHub) plugin;
        try
		{
			RegisteredServiceProvider<Administration> rsp = ifh
	        		.getServicesManager()
	        		.getRegistration(Administration.class);
	        if (rsp == null) 
	        {
	            return;
	        }
	        administrationConsumer = rsp.getProvider();
	        if(administrationConsumer != null)
	        {
	    		log.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	        }
		} catch(NoClassDefFoundError e)
		{}
        return;
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
}