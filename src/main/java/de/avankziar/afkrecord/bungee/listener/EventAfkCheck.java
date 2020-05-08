package main.java.de.avankziar.afkrecord.bungee.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import main.java.de.avankziar.afkrecord.bungee.AfkRecord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class EventAfkCheck implements Listener
{
	private AfkRecord plugin;
	public EventAfkCheck(AfkRecord plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onChat(ChatEvent event)
	{
		if(plugin.getYamlHandler().get().getBoolean("SccIsActive", false))
		{
			softsave((ProxiedPlayer) event.getSender());
			return;
		} else if(event.getMessage().startsWith("/"))
		{
			if(!event.getMessage().equalsIgnoreCase("/afk"))
			{
				softsave((ProxiedPlayer) event.getSender());
			}
			return;
		}
	}
	
	private void softsave(ProxiedPlayer player)
	{
		ServerInfo server = player.getServer().getInfo();
		String µ = "µ";
		String message = "softsave"+µ+player.getUniqueId().toString();
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        String msg = message;
        try {
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    server.sendData("afkrecord:afkrecordout", streamout.toByteArray());
	    return;
	}
}
