package main.java.me.avankziar.afkr.general.commands.tree;

import java.util.ArrayList;

public class CommandConstructor extends BaseConstructor
{
	public ArrayList<ArgumentConstructor> subcommands;
    public ArrayList<String> tablist;

	public CommandConstructor(String path, boolean canConsoleAccess,
    		ArgumentConstructor...argumentConstructors)
    {
		super(getYamlHandling().getCommandString(path+".Name"),
				path,
				getYamlHandling().getCommandString(path+".Permission"),
				getYamlHandling().getCommandString(path+".Suggestion"),
				getYamlHandling().getCommandString(path+".CommandString"),
				getYamlHandling().getCommandString(path+".HelpInfo"),
				canConsoleAccess);
        this.subcommands = new ArrayList<>();
        this.tablist = new ArrayList<>();
        for(ArgumentConstructor ac : argumentConstructors)
        {
        	this.subcommands.add(ac);
        	this.tablist.add(ac.getName());
        }
        getCommandTree().add(this);
    }
}