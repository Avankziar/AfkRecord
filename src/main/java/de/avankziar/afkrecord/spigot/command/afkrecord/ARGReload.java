package main.java.de.avankziar.afkrecord.spigot.command.afkrecord;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;
import main.java.de.avankziar.afkrecord.spigot.assistance.ChatApi;
import main.java.de.avankziar.afkrecord.spigot.command.CommandModule;

public class ARGReload extends CommandModule
{
	private AfkRecord plugin;
	
	public ARGReload(AfkRecord plugin)
	{
		super("spigotreload","afkrecord.cmd.afkrecord.reload",AfkRecord.afkrarguments,1,1,"spigotneuladen");
		this.plugin = plugin;
	}

	@Override
	public void run(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		String language = plugin.getUtility().getLanguage();
		if(plugin.reload())
		{
			///Yaml Datein wurden neugeladen.
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Reload.Success")));
			return;
		} else
		{
			///Es wurde ein Fehler gefunden! Siehe Konsole!
			player.spigot().sendMessage(ChatApi.tctl(plugin.getYamlHandler().getL().getString(language+".CmdAfkRecord.Reload.Error")));
			return;
		}
	}
}