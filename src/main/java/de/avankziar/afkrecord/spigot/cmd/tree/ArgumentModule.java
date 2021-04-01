package main.java.de.avankziar.afkrecord.spigot.cmd.tree;

import java.io.IOException;

import org.bukkit.command.CommandSender;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public abstract class ArgumentModule
{
	public ArgumentConstructor argumentConstructor;

    public ArgumentModule(ArgumentConstructor argumentConstructor)
    {
       this.argumentConstructor = argumentConstructor;
       AfkRecord.getPlugin().getArgumentMap().put(argumentConstructor.getPath(), this);
    }
    
    //This method will process the command.
    public abstract void run(CommandSender sender, String[] args) throws IOException;

}
