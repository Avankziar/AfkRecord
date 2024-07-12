package main.java.me.avankziar.afkr.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import main.java.me.avankziar.afkr.spigot.AfkR;

public class ServerListener  implements PluginMessageListener
{
	private AfkR plugin;
	
	public ServerListener(AfkR plugin)
	{
		this.plugin = plugin;
	}
	public void onPluginMessageReceived(String channel, Player p, byte[] bytes) 
	{
		if(channel.equals("afkr:afkrecordout")) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            String task = null;
            try 
            {
            	task = in.readUTF();
            	if(task.equals("afk-softsave"))
            	{
		        	String playerUUID = in.readUTF();
		        	boolean active = in.readBoolean();
		    		plugin.getPlayerTimes().saveRAM(UUID.fromString(playerUUID), active, false, false);
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
}
