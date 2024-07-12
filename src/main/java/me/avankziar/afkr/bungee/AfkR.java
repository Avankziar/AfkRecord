package main.java.me.avankziar.afkr.bungee;

import java.sql.SQLException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.me.avankziar.afkr.bungee.database.MysqlHandler;
import main.java.me.avankziar.afkr.bungee.database.MysqlSetup;
import main.java.me.avankziar.afkr.bungee.ifh.PlayerTimesProvider;
import main.java.me.avankziar.afkr.bungee.listener.EventAfkCheck;
import main.java.me.avankziar.afkr.general.database.YamlManager;
import main.java.me.avankziar.afkr.spigot.database.YamlHandler;
import main.java.me.avankziar.ifh.bungee.IFH;
import main.java.me.avankziar.ifh.bungee.administration.Administration;
import main.java.me.avankziar.ifh.bungee.plugin.RegisteredServiceProvider;
import main.java.me.avankziar.ifh.bungee.plugin.ServicePriority;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class AfkR extends Plugin
{
	private static AfkR plugin;
	public static Logger logger;
	public static String pluginName = "AfkRecord";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private static Administration administrationConsumer;
	
	public void onEnable() 
	{
		plugin = this;
		logger = getLogger();
		logger.info("  █████╗ ███████╗██╗  ██╗██████╗  | API-Version: "+plugin.getDescription().getVersion());
		logger.info(" ██╔══██╗██╔════╝██║ ██╔╝██╔══██╗ | Author: "+plugin.getDescription().getAuthor());
		logger.info(" ███████║█████╗  █████╔╝ ██████╔╝ | Plugin Website: https://www.spigotmc.org/resources/afkrecord.74626/");
		logger.info(" ██╔══██║██╔══╝  ██╔═██╗ ██╔══██╗ | Depend Plugins: "+plugin.getDescription().getDepends().toString());
		logger.info(" ██║  ██║██║     ██║  ██╗██║  ██║ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepends().toString());
		logger.info(" ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝ | Have Fun^^ ");
		
		setupIFHAdministration();
		
		yamlHandler = new YamlHandler(YamlManager.Type.VELO, pluginName, logger, plugin.getDataFolder().toPath(),
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm ||  yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		} else
		{
			disablePlugin();
			logger.severe("MySQL is not enabled! "+pluginName+" is disabled!");
		}
		getProxy().registerChannel("afkr:afkrecordout");
		getProxy().registerChannel("afkrecord:afkrecordin");
		getProxy().getPluginManager().registerListener(plugin, new EventAfkCheck());
		setupIFHProvider();
	}
	
	public void onDisable()
	{
		getProxy().getScheduler().cancel(plugin);
		//HandlerList.unregisterAll();
		if(yamlHandler.getConfig().getBoolean("Mysql.Status", false))
		{
			try
			{
				if (mysqlSetup.getConnection() != null) 
				{
					//backgroundtask.onShutDownDataSave();
				}
			} catch (SQLException e)
			{}
		}
		logger.info(pluginName + " is disabled!");
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
	
	public static AfkR getPlugin()
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
		Plugin plugin = getProxy().getPluginManager().getPlugin("InterfaceHub");
        if (plugin == null) 
        {
            return;
        }
        IFH ifh = (IFH) plugin;
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
	    		logger.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	        }
		} catch(NoClassDefFoundError e)
		{}
        return;
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	private void setupIFHProvider()
	{
		Plugin ifhp = getProxy().getPluginManager().getPlugin("InterfaceHub");
        if (ifhp == null) 
        {
            return;
        }
        main.java.me.avankziar.ifh.bungee.IFH ifh = (IFH) ifhp;
        try
        {
    		PlayerTimesProvider cp = new PlayerTimesProvider(this);
            ifh.getServicesManager().register(
             		main.java.me.avankziar.ifh.general.interfaces.PlayerTimes.class,
             		cp, plugin, ServicePriority.Normal);
            logger.info(pluginName + " detected InterfaceHub >>> PlayerTimes.class is provided!");
    		
        } catch(NoClassDefFoundError e) {}
	}
}