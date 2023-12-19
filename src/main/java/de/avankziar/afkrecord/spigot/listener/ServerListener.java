package main.java.de.avankziar.afkrecord.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

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
