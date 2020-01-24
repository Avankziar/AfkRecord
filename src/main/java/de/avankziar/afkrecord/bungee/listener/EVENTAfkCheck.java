package main.java.de.avankziar.afkrecord.bungee.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EVENTAfkCheck implements Listener
{
	@EventHandler
	public void onChat(ChatEvent event)
	{
		ProxiedPlayer p = (ProxiedPlayer) event.getSender();
		ServerInfo server = p.getServer().getInfo();
		String µ = "µ";
		String message = "softsave"+µ+p.getUniqueId().toString();
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        String msg = message;
        try {
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    server.sendData("afkrecord:afkrecord", streamout.toByteArray());
	    return;
	}
}
