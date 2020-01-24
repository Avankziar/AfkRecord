package main.java.de.avankziar.afkrecord.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class ServerListener  implements PluginMessageListener
{
	private AfkRecord plugin;
	
	public ServerListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	public void onPluginMessageReceived(String channel, Player p, byte[] bytes) 
	{
		if(channel.equals("afkrecord:afkrecord")) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            try {
            	String[] s = in.readUTF().split("µ");
            	String Category = s[0];
            	String PlayerUUID = s[1];
            	if(Category.equals("softsave"))
            	{
            		if(plugin.getServer().getPlayer(PlayerUUID)==null)
            		{
            			return;
            		}
            		Player player = plugin.getServer().getPlayer(PlayerUUID);
            		plugin.getUtility().softSave(player, true, true, false);
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
}
