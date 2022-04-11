package main.java.de.avankziar.afkrecord.bungee.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
		softsave((ProxiedPlayer) event.getSender());
	}
	
	private void softsave(ProxiedPlayer player)
	{
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
			out.writeUTF("afk-softsave");
			out.writeUTF(player.getUniqueId().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.getServer().getInfo().sendData("afkr:afkrecordout", streamout.toByteArray());
	    return;
	}
}
