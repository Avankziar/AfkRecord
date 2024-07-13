package main.java.me.avankziar.afkr.velocity.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class EventAfkCheck
{
	@Subscribe
	public void onChat(PlayerChatEvent event)
	{
		if(event.getMessage().startsWith("/afk"))
		{
			return;
		}
		softsave(event.getPlayer());
	}
	
	private void softsave(Player player)
	{
		ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(streamout);
        try {
			out.writeUTF("afk-softsave");
			out.writeUTF(player.getUniqueId().toString());
			out.writeBoolean(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        player.getCurrentServer().get().sendPluginMessage(
        		MinecraftChannelIdentifier.from("afkr:afkrecordout"), streamout.toByteArray());
	    return;
	}
}
