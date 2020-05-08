package main.java.de.avankziar.afkrecord.bungee.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerListener implements Listener
{
	private AfkRecord plugin;
	
	public ServerListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	 public void onPluginMessage(PluginMessageEvent ev) 
	 {
		 if (ev.getTag().equals("afkrecord:afkrecordin")) 
		 {
			 ByteArrayInputStream streamin = new ByteArrayInputStream(ev.getData());
		     DataInputStream in = new DataInputStream(streamin);
		     String µ = "µ";
		     try 
		     {
		        String[] s = in.readUTF().split(µ);
		        String category = s[0];
		        String playerUUID = s[1];
				if(plugin.getProxy().getPlayer(UUID.fromString(playerUUID)) == null)
				{
					return;
				}
				if(category.equals("getafk"))
				{
					ProxiedPlayer player = plugin.getProxy().getPlayer(UUID.fromString(playerUUID));
					plugin.getUtility().getafk(player);
				}
		     } catch (IOException e) 
			    {
					e.printStackTrace();
				}
			    return;
		 }
	 }

}
