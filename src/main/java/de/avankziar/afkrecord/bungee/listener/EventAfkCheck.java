package main.java.de.avankziar.afkrecord.bungee.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class EventAfkCheck implements Listener
{	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onChat(ChatEvent event)
	{
		if(event.getMessage().startsWith("/afk"))
		{
			return;
		}
		if(event.isCancelled())
		{
			softsave((ProxiedPlayer) event.getSender());
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
