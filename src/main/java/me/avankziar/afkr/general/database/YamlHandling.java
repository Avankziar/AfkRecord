package main.java.me.avankziar.afkr.general.database;

public interface YamlHandling
{
	/**
	 * For The Command/Argument/BaseConstructor
	 * Return the String from the commands.yml
	 * @param s
	 * @return
	 */
	String getCommandString(String s);
	
	/**
	 * For The Command/Argument/BaseConstructor
	 * Return the String from the commands.yml, and if the normal return is null, return default
	 * @param s
	 * @return
	 */
	String getCommandString(String s, String defaults);
	
}
