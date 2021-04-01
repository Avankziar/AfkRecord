package main.java.de.avankziar.afkrecord.spigot.cmd.tree;

import java.util.ArrayList;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class CommandConstructor extends BaseConstructor
{
    public ArrayList<ArgumentConstructor> subcommands;
    public ArrayList<String> tablist;

	public CommandConstructor(String path, boolean canConsoleAccess,
    		ArgumentConstructor...argumentConstructors)
    {
		super(AfkRecord.getPlugin().getYamlHandler().getCom().getString(path+".Name"),
				path,
				AfkRecord.getPlugin().getYamlHandler().getCom().getString(path+".Permission"),
				AfkRecord.getPlugin().getYamlHandler().getCom().getString(path+".Suggestion"),
				AfkRecord.getPlugin().getYamlHandler().getCom().getString(path+".CommandString"),
				AfkRecord.getPlugin().getYamlHandler().getCom().getString(path+".HelpInfo"),
				canConsoleAccess);
        this.subcommands = new ArrayList<>();
        this.tablist = new ArrayList<>();
        for(ArgumentConstructor ac : argumentConstructors)
        {
        	this.subcommands.add(ac);
        	this.tablist.add(ac.getName());
        }
        AfkRecord.getPlugin().getCommandTree().add(this);
    }
}