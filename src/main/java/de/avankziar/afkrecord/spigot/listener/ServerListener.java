package main.java.de.avankziar.afkrecord.spigot.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.object.User;

public class ServerListener  implements PluginMessageListener
{
	private AfkRecord plugin;
	
	public ServerListener(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	public void onPluginMessageReceived(String channel, Player p, byte[] bytes) 
	{
		if(channel.equals("afkrecord:afkrecordout")) 
		{
        	ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(stream);
            try {
            	String[] s = in.readUTF().split("µ");
            	String Category = s[0];
            	String PlayerUUID = s[1];
            	if(Category.equals("softsave"))
            	{
            		if(plugin.getServer().getPlayer(UUID.fromString(PlayerUUID))==null)
            		{
            			return;
            		}
            		Player player = plugin.getServer().getPlayer(UUID.fromString(PlayerUUID));
            		if(User.getUser(player)==null)
            		{
            			return;
            		}
            		plugin.getUtility().softSave(player, true, true, false);
            	} else if(Category.equals("getafk"))
            	{
            		if(plugin.getServer().getPlayer(UUID.fromString(PlayerUUID))==null)
            		{
            			return;
            		}
            		Player player = plugin.getServer().getPlayer(UUID.fromString(PlayerUUID));
            		String list = s[2];
            		plugin.getCommandHelper().getafk(player,list);
            	}
            } catch (IOException e) 
            {
    			e.printStackTrace();
    		}
		}
	}
}
