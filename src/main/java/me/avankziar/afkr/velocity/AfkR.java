package main.java.me.avankziar.afkr.velocity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelRegistrar;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import main.java.me.avankziar.afkr.general.database.YamlHandler;
import main.java.me.avankziar.afkr.general.database.YamlManager;
import main.java.me.avankziar.afkr.velocity.assistance.BackgroundTask;
import main.java.me.avankziar.afkr.velocity.database.MysqlHandler;
import main.java.me.avankziar.afkr.velocity.database.MysqlSetup;
import main.java.me.avankziar.afkr.velocity.ifh.PlayerTimesProvider;
import main.java.me.avankziar.afkr.velocity.listener.EventAfkCheck;
import me.avankziar.ifh.velocity.IFH;
import me.avankziar.ifh.velocity.administration.Administration;
import me.avankziar.ifh.velocity.plugin.RegisteredServiceProvider;
import me.avankziar.ifh.velocity.plugin.ServicePriority;

@Plugin(
		id = "afkrecord", 
		name = "AfkRecord", 
		version = "10-7-0",
		url = "https://www.spigotmc.org/resources/authors/avankziar.332028/",
		dependencies = {
				@Dependency(id = "interfacehub", optional = false),
				@Dependency(id = "rootadministration", optional = true)
		},
		description = "Afk Tracker Plugin",
		authors = {"Avankziar"}
)
public class AfkR
{
	private static AfkR plugin;
    private final ProxyServer server;
    private Logger logger = null;
    private Path dataDirectory;
	public static String pluginname = "AfkRecord";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private static Administration administrationConsumer;
    
    
    @Inject
    public AfkR(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) 
    {
    	AfkR.plugin = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) 
    {
    	logger = Logger.getLogger(pluginname);
    	PluginDescription pd = server.getPluginManager().getPlugin(pluginname.toLowerCase()).get().getDescription();
        List<String> dependencies = new ArrayList<>();
        pd.getDependencies().stream().allMatch(x -> dependencies.add(x.getId()));
        //https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=AFKR
		logger.info("  █████╗ ███████╗██╗  ██╗██████╗  | Id: "+pd.getId());
		logger.info(" ██╔══██╗██╔════╝██║ ██╔╝██╔══██╗ | Version: "+pd.getVersion().get());
		logger.info(" ███████║█████╗  █████╔╝ ██████╔╝ | Author: ["+String.join(", ", pd.getAuthors())+"]");
		logger.info(" ██╔══██║██╔══╝  ██╔═██╗ ██╔══██╗ | Description: "+(pd.getDescription().isPresent() ? pd.getDescription().get() : "/"));
		logger.info(" ██║  ██║██║     ██║  ██╗██║  ██║ | Plugin Website:"+pd.getUrl().get().toString());
		logger.info(" ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝ | Dependencies Plugins: ["+String.join(", ", dependencies)+"]");
        
		setupIFHAdministration();
		
        yamlHandler = new YamlHandler(YamlManager.Type.VELO, pluginname, logger, dataDirectory,
        		(plugin.getAdministration() == null ? null : plugin.getAdministration().getLanguage()));
        setYamlManager(yamlHandler.getYamlManager());
        
        String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlSetup = new MysqlSetup(plugin, adm, path);
			mysqlHandler = new MysqlHandler(plugin);
		}
		
        registerChannels();
        setupIFHProvider();
        new BackgroundTask(plugin);
    }
    
    public static AfkR getPlugin()
    {
    	return AfkR.plugin;
    }
    
    public ProxyServer getProxy()
    {
    	return server;
    }
    
    public Logger getLogger()
    {
    	return logger;
    }
    
    public Path getDataDirectory()
    {
    	return dataDirectory;
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
    
	private void setupIFHProvider()
	{
		Optional<PluginContainer> ifhp = getProxy().getPluginManager().getPlugin("interfacehub");
		Optional<PluginContainer> plugin = getProxy().getPluginManager().getPlugin(pluginname.toLowerCase());
        if (ifhp.isEmpty()) 
        {
        	logger.info(pluginname + " dont find InterfaceHub!");
            return;
        }
        me.avankziar.ifh.velocity.IFH ifh = IFH.getPlugin();
        try
        {
        	PlayerTimesProvider cp = new PlayerTimesProvider(this);
            ifh.getServicesManager().register(
            		me.avankziar.ifh.general.interfaces.PlayerTimes.class,
             		cp, plugin.get(), ServicePriority.Normal);
            logger.info(pluginname + " detected InterfaceHub >>> PlayerTimes.class is provided!");
    		
        } catch(NoClassDefFoundError e) {}
	}
	
	private void setupIFHAdministration()
	{ 
		Optional<PluginContainer> ifhp = plugin.getProxy().getPluginManager().getPlugin("interfacehub");
        if (ifhp.isEmpty()) 
        {
        	logger.info(pluginname + " dont find InterfaceHub!");
            return;
        }
        me.avankziar.ifh.velocity.IFH ifh = IFH.getPlugin();
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
    		logger.info(pluginname + " detected InterfaceHub >>> Administration.class is consumed!");
        }
        return;
	}
	
	public Administration getAdministration()
	{
		return administrationConsumer;
	}
	
	private void registerChannels()
    {
    	server.getEventManager().register(plugin, new EventAfkCheck());
    	ChannelRegistrar cr = server.getChannelRegistrar();
    	cr.register(MinecraftChannelIdentifier.from("afkr:afkrecordout"));
        cr.register(MinecraftChannelIdentifier.from("afkrecord:afkrecordin"));
    }
}