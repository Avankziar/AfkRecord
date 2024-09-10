package main.java.me.avankziar.afkr.general.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import main.java.me.avankziar.afkr.general.database.Language.ISO639_2B;
import main.java.me.avankziar.afkr.spigot.listener.afkcheck.BaseListener;

public class YamlManager
{
	public enum Type
	{
		BUNGEE, SPIGOT, VELO;
	}
	
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	private Type type;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager(Type type)
	{
		this.type = type;
		initConfig();
		if(type == Type.SPIGOT)
		{
			initCommands();
		}
		initLanguage();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigKey()
	{
		return configKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	/*
	 * The main methode to set all paths in the yamls.
	 */
	public void setFileInput(dev.dejvokep.boostedyaml.YamlDocument yml,
			LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType) throws org.spongepowered.configurate.serialize.SerializationException
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(key.startsWith("#"))
		{
			if(type == Type.BUNGEE)
			{
				//On Bungee dont work comments
				return;
			}
			//Comments
			String k = key.replace("#", "");
			if(yml.get(k) == null)
			{
				//return because no actual key are present
				return;
			}
			if(yml.getBlock(k) == null)
			{
				return;
			}
			if(yml.getBlock(k).getComments() != null && !yml.getBlock(k).getComments().isEmpty())
			{
				//Return, because the comments are already present, and there could be modified. F.e. could be comments from a admin.
				return;
			}
			if(keyMap.get(key).languageValues.get(languageType).length == 1)
			{
				if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
				{
					String s = ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "");
					yml.getBlock(k).setComments(Arrays.asList(s));
				}
			} else
			{
				List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
				ArrayList<String> stringList = new ArrayList<>();
				if(list instanceof List<?>)
				{
					for(Object o : list)
					{
						if(o instanceof String)
						{
							stringList.add(((String) o).replace("\r\n", ""));
						}
					}
				}
				yml.getBlock(k).setComments((List<String>) stringList);
			}
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, convertMiniMessageToBungee(((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "")));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(convertMiniMessageToBungee(((String) o).replace("\r\n", "")));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	private String convertMiniMessageToBungee(String s)
	{
		if(type != Type.BUNGEE)
		{
			//If Server is not Bungee, there is no need to convert.
			return s;
		}
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '<' && i+1 < s.length())
			{
				char cc = s.charAt(i+1);
				if(cc == '#' && i+8 < s.length())
				{
					//Hexcolors
					//     i12345678
					//f.e. <#00FF00>
					String rc = s.substring(i, i+8);
					b.append(rc.replace("<#", "&#").replace(">", ""));
					i += 8;
				} else
				{
					//Normal Colors
					String r = null;
					StringBuilder sub = new StringBuilder();
					sub.append(c).append(cc);
					i++;
					for(int j = i+1; j < s.length(); j++)
					{
						i++;
						char jc = s.charAt(j);
						if(jc == '>')
						{
							sub.append(jc);
							switch(sub.toString())
							{
							case "</color>":
							case "</black>":
							case "</dark_blue>":
							case "</dark_green>":
							case "</dark_aqua>":
							case "</dark_red>":
							case "</dark_purple>":
							case "</gold>":
							case "</gray>":
							case "</dark_gray>":
							case "</blue>":
							case "</green>":
							case "</aqua>":
							case "</red>":
							case "</light_purple>":
							case "</yellow>":
							case "</white>":
							case "</obf>":
							case "</obfuscated>":
							case "</b>":
							case "</bold>":
							case "</st>":
							case "</strikethrough>":
							case "</u>":
							case "</underlined>":
							case "</i>":
							case "</em>":
							case "</italic>":
								r = "";
								break;
							case "<black>":
								r = "&0";
								break;
							case "<dark_blue>":
								r = "&1";
								break;
							case "<dark_green>":
								r = "&2";
								break;
							case "<dark_aqua>":
								r = "&3";
								break;
							case "<dark_red>":
								r = "&4";
								break;
							case "<dark_purple>":
								r = "&5";
								break;
							case "<gold>":
								r = "&6";
								break;
							case "<gray>":
								r = "&7";
								break;
							case "<dark_gray>":
								r = "&8";
								break;
							case "<blue>":
								r = "&9";
								break;
							case "<green>":
								r = "&a";
								break;
							case "<aqua>":
								r = "&b";
								break;
							case "<red>":
								r = "&c";
								break;
							case "<light_purple>":
								r = "&d";
								break;
							case "<yellow>":
								r = "&e";
								break;
							case "<white>":
								r = "&f";
								break;
							case "<obf>":
							case "<obfuscated>":
								r = "&k";
								break;
							case "<b>":
							case "<bold>":
								r = "&l";
								break;
							case "<st>":
							case "<strikethrough>":
								r = "&m";
								break;
							case "<u>":
							case "<underlined>":
								r = "&n";
								break;
							case "<i>":
							case "<em>":
							case "<italic>":
								r = "&o";
								break;
							case "<reset>":
								r = "&r";
								break;
							case "<newline>":
								r = "~!~";
								break;
							}
							b.append(r);
							break;
						} else
						{
							//Search for the color.
							sub.append(jc);
						}
					}
				}
			} else
			{
				b.append(c);
			}
		}
		return b.toString();
	}
	
	private void addComments(LinkedHashMap<String, Language> mapKeys, String path, Object[] o)
	{
		mapKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, o));
	}
	
	private void addConfig(String path, Object[] c, Object[] o)
	{
		configKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER}, c));
		addComments(configKeys, "#"+path, o);
	}
	
	public void initConfig() //INFO:Config
	{
		addConfig("useIFHAdministration",
				new Object[] {
				true},
				new Object[] {
				"Boolean um auf das IFH Interface Administration zugreifen soll.",
				"Wenn 'true' eingegeben ist, aber IFH Administration ist nicht vorhanden, so werden automatisch die eigenen Configwerte genommen.",
				"Boolean to access the IFH Interface Administration.",
				"If 'true' is entered, but IFH Administration is not available, the own config values are automatically used."});
		addConfig("IFHAdministrationPath", 
				new Object[] {
				"afkr"},
				new Object[] {
				"",
				"Diese Funktion sorgt dafür, dass das Plugin auf das IFH Interface Administration zugreifen kann.",
				"Das IFH Interface Administration ist eine Zentrale für die Daten von Sprache, Servername und Mysqldaten.",
				"Diese Zentralisierung erlaubt für einfache Änderung/Anpassungen genau dieser Daten.",
				"Sollte das Plugin darauf zugreifen, werden die Werte in der eigenen Config dafür ignoriert.",
				"",
				"This function ensures that the plugin can access the IFH Interface Administration.",
				"The IFH Interface Administration is a central point for the language, server name and mysql data.",
				"This centralization allows for simple changes/adjustments to precisely this data.",
				"If the plugin accesses it, the values in its own config are ignored."});
		addConfig("Language",
				new Object[] {
				"ENG"},
				new Object[] {
				"",
				"Die eingestellte Sprache. Von Haus aus sind 'ENG=Englisch' und 'GER=Deutsch' mit dabei.",
				"Falls andere Sprachen gewünsch sind, kann man unter den folgenden Links nachschauen, welchs Kürzel für welche Sprache gedacht ist.",
				"Siehe hier nach, sowie den Link, welche dort auch für Wikipedia steht.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java",
				"",
				"The set language. By default, ENG=English and GER=German are included.",
				"If other languages are required, you can check the following links to see which abbreviation is intended for which language.",
				"See here, as well as the link, which is also there for Wikipedia.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java"});
		addConfig("Mysql.Status",
				new Object[] {
				false},
				new Object[] {
				"",
				"'Status' ist ein simple Sicherheitsfunktion, damit nicht unnötige Fehler in der Konsole geworfen werden.",
				"Stelle diesen Wert auf 'true', wenn alle Daten korrekt eingetragen wurden.",
				"",
				"'Status' is a simple security function so that unnecessary errors are not thrown in the console.",
				"Set this value to 'true' if all data has been entered correctly."});
		addComments(configKeys, "#Mysql", 
				new Object[] {
				"",
				"Mysql ist ein relationales Open-Source-SQL-Databaseverwaltungssystem, das von Oracle entwickelt und unterstützt wird.",
				"'My' ist ein Namenkürzel und 'SQL' steht für Structured Query Language. Eine Programmsprache mit der man Daten auf einer relationalen Datenbank zugreifen und diese verwalten kann.",
				"Link https://www.mysql.com/de/",
				"Wenn du IFH Administration nutzt, kann du diesen Bereich ignorieren.",
				"",
				"Mysql is an open source relational SQL database management system developed and supported by Oracle.",
				"'My' is a name abbreviation and 'SQL' stands for Structured Query Language. A program language that can be used to access and manage data in a relational database.",
				"Link https://www.mysql.com",
				"If you use IFH Administration, you can ignore this section."});
		addConfig("Mysql.Host",
				new Object[] {
				"127.0.0.1"},
				new Object[] {
				"",
				"Der Host, oder auch die IP. Sie kann aus einer Zahlenkombination oder aus einer Adresse bestehen.",
				"Für den Lokalhost, ist es möglich entweder 127.0.0.1 oder 'localhost' einzugeben. Bedenke, manchmal kann es vorkommen,",
				"das bei gehosteten Server die ServerIp oder Lokalhost möglich ist.",
				"",
				"The host, or IP. It can consist of a number combination or an address.",
				"For the local host, it is possible to enter either 127.0.0.1 or >localhost<.",
				"Please note that sometimes the serverIp or localhost is possible for hosted servers."});
		addConfig("Mysql.Port",
				new Object[] {
				3306},
				new Object[] {
				"",
				"Ein Port oder eine Portnummer ist in Rechnernetzen eine Netzwerkadresse,",
				"mit der das Betriebssystem die Datenpakete eines Transportprotokolls zu einem Prozess zuordnet.",
				"Ein Port für Mysql ist standart gemäß 3306.",
				"",
				"In computer networks, a port or port number ",
				"is a network address with which the operating system assigns the data packets of a transport protocol to a process.",
				"A port for Mysql is standard according to 3306."});
		addConfig("Mysql.DatabaseName",
				new Object[] {
				"mydatabase"},
				new Object[] {
				"",
				"Name der Datenbank in Mysql.",
				"",
				"Name of the database in Mysql."});
		addConfig("Mysql.SSLEnabled",
				new Object[] {
				false},
				new Object[] {
				"",
				"SSL ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"SSL is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.AutoReconnect",
				new Object[] {
				true},
				new Object[] {
				"",
				"AutoReconnect ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"AutoReconnect is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.VerifyServerCertificate",
				new Object[] {
				false},
				new Object[] {
				"",
				"VerifyServerCertificate ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"VerifyServerCertificate is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.User",
				new Object[] {
				"admin"},
				new Object[] {
				"",
				"Der User, welcher auf die Mysql zugreifen soll.",
				"",
				"The user who should access the Mysql."});
		addConfig("Mysql.Password",
				new Object[] {
				"not_0123456789"},
				new Object[] {
				"",
				"Das Passwort des Users, womit er Zugang zu Mysql bekommt.",
				"",
				"The user's password, with which he gets access to Mysql."});
		if(type == Type.SPIGOT)
		{
			configKeys.put("General.RAMSave.InSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					120}));
			configKeys.put("General.MySQLSave.InSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					240}));
			configKeys.put("General.AfkChecker.InSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					60}));
			configKeys.put("General.AfkChecker.AfkAfterLastActivityInSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					600}));
			configKeys.put("General.AfkKicker.IsActive"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					true}));
			configKeys.put("General.AfkKicker.InSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					60}));
			configKeys.put("General.AfkKicker.KickAfterLastActivityInSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					600}));
			configKeys.put("General.AfkKicker.ExcludedWorlds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"worldnamedummy",
					"worldnamedumme2"}));
			configKeys.put("General.AfkTeleport.IsActive"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					false}));
			configKeys.put("General.AfkTeleport.UseCommand"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"console;warp afk",
					"player;warp afk"}));
			configKeys.put("General.AfkTeleport.InSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					60}));
			configKeys.put("General.AfkTeleport.DoCommandAfterLastActivityInSeconds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					600}));
			configKeys.put("General.AfkTeleport.ExcludedWorlds"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"worldnamedummy",
					"worldnamedumme2"}));
			//--------Above finish
			for(BaseListener.EventType et : new ArrayList<BaseListener.EventType>(EnumSet.allOf(BaseListener.EventType.class)))
			{
				configKeys.put("EventListener."+et.toString()+".isActive"
						, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						et.isActive()}));
				configKeys.put("EventListener."+et.toString()+".CooldownInSecond"
						, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						et.getCooldown()}));
			}
			configKeys.put("Identifier.Click"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"click"}));
			configKeys.put("Identifier.Hover"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"hover"}));
			configKeys.put("Seperator.BetweenFunction"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"~"}));
			configKeys.put("Seperator.WhithinFuction"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"@"}));
			configKeys.put("Seperator.Space"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"+"}));
			configKeys.put("Seperator.HoverNewLine"
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"~!~"}));
		} else if(type != Type.SPIGOT)
		{
			addConfig("Proxy.Scheduler.CheckIfOfflineAndAfk.Active",
					new Object[] {
					true},
					new Object[] {
					"",
					"Wenn 'true' wird ein im Hintergrundlaufender Task periodisch checken,",
					"ob Spieler welche noch Afk sind, welche aber schon offline sind. Und diese austragen.",
					"",
					"",
					""});
			addConfig("Proxy.Scheduler.CheckIfOfflineAndAfk.LoopInMinutes",
					new Object[] {
					5},
					new Object[] {
					"",
					"Wie oft in Minuten der Task sich wiederholen soll.",
					"",
					""});
		}
	}
	
	@SuppressWarnings("unused") //INFO:Commands
	public void initCommands()
	{
		comBypass();
		String path = "";
		commandsInput("afkr", "afkr", "afkrecord.cmd.afkr", 
				"/afkr [pagenumber]", "/afkr ",
				"&c/afkr &f| Infoseite für alle Befehle.",
				"&c/afkr &f| Info page for all commands.");
		String basePermission = "afkrecord.cmd.afkr";
		argumentInput("afkr_bypass", "bypass", basePermission,
				"/afkr bypass", "/afkr bypass ",
				"&c/afkr bypass &f| Toggelt ob man immer als &enicht afk &fgezählt wird.",
				"&c/afkr bypass &f| Toggle whether one is always counted as &enot afk&f.");
		argumentInput("afkr_counttime", "counttime", basePermission,
				"/afkr counttime <tage> [Spieler]", "/afkr counttime ",
				"&c/afkr counttime <tage> [Spieler] &f| Zählt alle Zeiten in einem Zeitraum.",
				"&c/afkr counttime <days> [player] &f| Counts all times in a period.");
		argumentInput("afkr_permcounttime", "permcounttime", basePermission,
				"/afkr permcounttime <tage> <Permission>", "/afkr permcounttime ",
				"&c/afkr permcounttime <tage> <Permission> &f| Zählt alle Spieler mit dieser Permission auf, und zeigt ihre Zeiten.",
				"&c/afkr permcounttime <days> <Permission> &f| Enumerates all players with this permission, and shows their times.");
		argumentInput("afkr_getafk", "getafk", basePermission,
				"/afkr getafk", "/afkr getafk ",
				"&c/afkr getafk &f| Zeigt alle afk Spieler auf dem Netzwerk an.",
				"&c/afkr getafk &f| Show all afk player on the network.");
		argumentInput("afkr_gettime", "gettime", basePermission,
				"/afkr gettime <tage> [Spieler]", "/afkr gettime ",
				"&c/afkr gettime <zahl> [Spieler] &f| Zeigt für die letzten Einträge die Zeiten an.",
				"&c/afkr gettime <number> [player] &f| Shows the entry of the last days.");
		argumentInput("afkr_time", "time", basePermission,
				"/afkr time [Spieler]", "/afkr time ",
				"&c/afkr time [Spieler] &f| Zeigt die Gesamtwerte des Spielers an.",
				"&c/afkr time [player] &f| Shows the total values of the player.");
		argumentInput("afkr_top", "top", basePermission,
				"/afkr top <onlinetime|alltime|afktime> [pagenumber]", "/afkr top ",
				"&c/afkr top <onlinetime|alltime|afktime> [Seitenzahl] &f| Zwischenbefehl.",
				"&c/afkr top <onlinetime|alltime|afktime> [pagenumber] &f| Subcommand");
		argumentInput("afkr_top_onlinetime", "onlinetime", basePermission,
				"/afkr top onlinetime [pagenumber] [last x days]", "/afkr top onlinetime",
				"&c/afkr top onlinetime [Seitenzahl] [letzte x Tage] &f| Zeigt die Topliste an, 10 Plätze pro Seite.",
				"&c/afkr top onlinetime [pagenumber] [last x days] &f| Displays the top list, 10 places per page.");
		argumentInput("afkr_top_alltime", "alltime", basePermission,
				"/afkr top alltime [pagenumber] [last x days]", "/afkr top alltime ",
				"&c/afkr top alltime [Seitenzahl] [letzte x Tage] &f| Zeigt die Topliste an, 10 Plätze pro Seite.",
				"&c/afkr top alltime [pagenumber] [last x days] &f| Displays the top list, 10 places per page.");
		argumentInput("afkr_top_afktime", "afktime", basePermission,
				"/afkr top afktime [pagenumber] [last x days]", "/afkr top afktime ",
				"&c/afkr top afktime [Seitenzahl] [letzte x Tage] &f| Zeigt die Topliste an, 10 Plätze pro Seite.",
				"&c/afkr top afktime [pagenumber] [last x days] &f| Displays the top list, 10 places per page.");
		argumentInput("afkr_vacation", "vacation", basePermission,
				"/afkr vacation [<player> || <vacation in days as number> || <<dd.MM.yyyy> <HH:mm>>]", "/afkr vacation ",
				"&c/afkr vacation [<player> || <vacation in days as number> || <<dd.MM.yyyy> <HH:mm>>] &f| Zeigt an ob und bis wielange der Spieler in Urlaub ist oder setzt die Zeit, bis wann man in Urlaub ist.",
				"&c/afkr vacation [<player> || <vacation in days as number> || <<dd.MM.yyyy> <HH:mm>>] &f| Shows if and until how long the player is on vacation or sets the time until when you are on vacation.");
		argumentInput("afkr_add", "add", basePermission,
				"/afkr add <playername> <onlinetime|alltime|afktime> <dd:HH:mm:ss Format>", "/afkr add ",
				"&c/afkr add <playername> <onlinetime|alltime|afktime> <dd:HH:mm:ss Format> &f| Addiert/Subtrahiert Zeit vom Spielerkonto.",
				"&c/afkr add <playername> <onlinetime|alltime|afktime> <dd:HH:mm:ss Format> &f| Addiert/Subtrahiert Zeit vom Spielerkonto.");
		commandsInput("afk", "afk", "afkrecord.cmd.afk", 
				"/afk", "/afk ",
				"&c/afk &f| Toggelt den Afk-Zustand.",
				"&c/afk &f| Toggles the afk-status.");
	}
	
	private void comBypass() //INFO:ComBypass
	{
		String path = "Bypass.";
		commandsKeys.put(path+"CountTimeOther"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecord.cmd.afkrecord.counttime.other"}));
		commandsKeys.put(path+"GetTimeOther"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecord.cmd.afkrecord.gettime.other"}));
		commandsKeys.put(path+"TimeOther"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecord.cmd.afkrecord.time.other"}));
		commandsKeys.put(path+"VacationOther"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecord.cmd.afkrecord.vacation.other"}));
		commandsKeys.put(path+"TopLastdays"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecord.cmd.afkrecord.top.last.days"}));
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString,
			String helpInfoGerman, String helpInfoEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString,
			String helpInfoGerman, String helpInfoEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		if(type != Type.SPIGOT)
		{
			languageKeys.put("TimeFormat.Seperator", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							" ",
							" "}));
			languageKeys.put("TimeFormat.Year", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							"%value% &dJahre&f",
							"%value% &dYears&f"}));
			languageKeys.put("TimeFormat.Day", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							"%value% &4Tage&f",
							"%value% &4days&f"}));
			languageKeys.put("TimeFormat.Hour", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							"%value% &ch&f",
							"%value% &ch&f"}));
			languageKeys.put("TimeFormat.Minute", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							"%value% &6min&f",
							"%value% &6min&f"}));
			languageKeys.put("TimeFormat.Second", 
					new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
							"%value% &es&f",
							"%value% &es&f"}));
		}
		languageKeys.put("NoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast dafür keine Rechte!",
						"&cYou have no rights for this!"}));
		languageKeys.put("PlayerNotExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler existiert nicht!",
						"&cThe player does not exist!"}));
		languageKeys.put("IllegalArgument",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas angegebene Argument ist keine Zahl!",
						"&cThe argument given is not a number!"}));
		languageKeys.put("IllegalNumber",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie angegebene Zahl ist nicht in einer bestimmten Größe!",
						"&cThe specified number is not in a specific size!"}));
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDeine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
						"&cYour input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("NoVault",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cVault ist nicht installiert!",
						"&cVault is not installt!"}));
		languageKeys.put("GeneralHover", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick mich!",
						"&eClick me!"}));
		
		languageKeys.put("TimeFormat.Seperator", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						" ",
						" "}));
		languageKeys.put("TimeFormat.Year", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% &dJahre&f",
						"%value% &dYears&f"}));
		languageKeys.put("TimeFormat.Day", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% &4Tage&f",
						"%value% &4days&f"}));
		languageKeys.put("TimeFormat.Hour", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% &ch&f",
						"%value% &ch&f"}));
		languageKeys.put("TimeFormat.Minute", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% &6min&f",
						"%value% &6min&f"}));
		languageKeys.put("TimeFormat.Second", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% &es&f",
						"%value% &es&f"}));
		languageKeys.put("Time.Years", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&dJahre&f ",
						"&dYears&f "}));
		languageKeys.put("Time.Days", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4Tage&f ",
						"&4days&f "}));
		languageKeys.put("Time.Hours", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ch&f ",
						"&ch&f "}));
		languageKeys.put("Time.Minutes", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6min&f ",
						"&6min&f "}));
		languageKeys.put("Time.Seconds", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&es&f",
						"&es&f"}));
		
		languageKeys.put("AfkKicker.Kick", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bDu wurdest wegen &cafk &bnach &f%time% &bgekickt!",
						"&cYou got kicked for &cafk &bafter &f%time%&b!"}));
		
		languageKeys.put("Placement.Top0001",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4",
						"&4"}));
		languageKeys.put("Placement.Top0002",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4",
						"&4"}));
		languageKeys.put("Placement.Top0003",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6",
						"&6"}));
		languageKeys.put("Placement.Top0005",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&5",
						"&5"}));
		languageKeys.put("Placement.Top0010",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&d",
						"&d"}));
		languageKeys.put("Placement.Top0025",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&9",
						"&9"}));
		languageKeys.put("Placement.Top0050",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&3",
						"&3"}));
		languageKeys.put("Placement.Top0100",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&2",
						"&2"}));
		languageKeys.put("Placement.Top0250",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a",
						"&a"}));
		languageKeys.put("Placement.Top0500",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e",
						"&e"}));
		languageKeys.put("Placement.Top1000",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&8",
						"&8"}));
		languageKeys.put("Placement.Top2500",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7",
						"&7"}));
		languageKeys.put("Placement.Top5000",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&l",
						"&l"}));
		languageKeys.put("Placement.Top9999",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&o",
						"&o"}));
		languageKeys.put("Placement.Above",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&f",
						"&f"}));
		
		languageKeys.put("CmdAfkRecord.BaseInfo.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e==========&fAfkRecord&e==========",
						"&e==========&fAfkRecord&e=========="}));
		languageKeys.put("CmdAfkRecord.BaseInfo.Past",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e<== &nvorherige Seite",
						"&e<== &npast page "}));
		languageKeys.put("CmdAfkRecord.BaseInfo.Next",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e&nnächste Seite &e==>",
						"&e&nnext page &e==>"}));
		
		languageKeys.put("CmdAfkRecord.Bypass.YouBypass",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eNun werden intern deine Zeit immer als aktive Zeit berechnet. &cAchtung! &eDu kannst trotzdem nach außenhin als Afk gelten!",
						"&eNow internally your time will always be calculated as active time. &cAttention! &eYou can still be considered as afk externally!"}));
		languageKeys.put("CmdAfkRecord.Bypass.YouDontBypass",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eNun wird wieder alles normal berechnet. Du bypasst das Afk nun nicht mehr!",
						"&eNow everything is calculated normally again. You are no longer bypassing the afk!"}));
		languageKeys.put("CmdAfkRecord.Bypass.YouAredBypass",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist in der Afk-Umgehung! Bitte lass die Umgehung fallen um in den /afk zu gehen!",
						"&cYou are in the afk bypass! Please drop the bypass to go into the /afk!"}));
		
		languageKeys.put("CmdAfkRecord.Convert.PleaseConfirm",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &eDer Konvertierungsprozess wird bei &f%count% &eDatensätzen etwa &f%time% &elang dauern. Pro Sekunde werden etwa 60 Datensätze bearbeitet. Bitte bestätige den start mit einem &fbestätigen &eam ende des Befehls!",
						"&cAttention! &eTThe conversion process will take about &f%time% &elong for &f%count% &edatasets. About 60 data records are processed per second. Please confirm the start with a &fconfirm &eam end of the command!"}));
		languageKeys.put("CmdAfkRecord.Convert.Start",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cKonvertierungsprozess startet. Ende des Prozess etwa: %time%",
						"Conversion process starts. End of the process about: %time%"}));
		
		languageKeys.put("CmdAfkRecord.CountTime.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fSpielzeiten von &5%player% &fder letzten %days% &fTage&e=====",
						"&e=====&fPlaytime from &5%player% &fthe past few %days% &fdays&e====="}));
		languageKeys.put("CmdAfkRecord.CountTime.ActiveTime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bAktive Spielzeit: &f%ontime%",
						"&bActivetime: &f%ontime%"}));
		languageKeys.put("CmdAfkRecord.CountTime.AfkTime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bAfkzeit:           &f%afktime%",
						"&bAfktime: &f%afktime%"}));
		languageKeys.put("CmdAfkRecord.CountTime.Alltime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bGesamte Zeit:    &f%alltime%",
						"&bTotal time: &f%alltime%"}));
		languageKeys.put("CmdAfkRecord.CountTime.LastActive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bLetzte Aktivität: &f%time%",
						"&bLast activity: &f%time%"}));
		
		languageKeys.put("CmdAfkRecord.CountTimePerm.InProgress",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBefehl ist schon in Arbeit! Bitte warten Sie, bis der andere Spieler die Ausgabe erhalten hat!",
						"&cCommand is already in progress! Please wait until the other player has received the output!"}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.WIP",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cAchtung! &eBefehl wird für die Permission &f%perm% &eund die Anzahl an Tage &f%days% &ebearbeitet. Bitte warten Sie.",
						"&cAttention. &eCommand is processed for the permission &f%perm% &eand the number of days &f%days% &e. Please wait."}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=&fSpielzeiten von Spieler mit &5%perm% &fPerm der letzten %days% &fTage&e=",
						"&e=&fPlaytime from player with &5%perm% &fperm of the past few %days% &fdays&e="}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Hover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&aAktive Spielzeit: &f%activitytime%",
						"&6Gesamte Zeit:    &f%alltime%",
						"&cAfkzeit:           &f%afktime%",
						"&bLetzte Aktivität: &f%lastactivity%",
						"&eKlicke für die Zeiten in den Chat.",
						
						"&aActivetime: &f%activitytime%",
						"&6Total time: &f%alltime%",
						"&cAfktime: &f%afktime%",
						"&bLast activity: &f%lastactivity%",
						"&eClick for the times in the chat."}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Colors.Under1H",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4",
						"&4"}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Colors.Under6H",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c",
						"&c"}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Colors.Under12H",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e",
						"&e"}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Colors.Under24H",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a",
						"&a"}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Colors.Under7D",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&2",
						"&2"}));
		languageKeys.put("CmdAfkRecord.CountTimePerm.Colors.Over7D",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&d",
						"&d"}));
		
		languageKeys.put("CmdAfkRecord.GetAfk.NoPlayerAfk",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKein Spieler ist afk!",
						"&eNo player is afk!"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerAfk",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eAfk Spieler: ",
						"&eAfk Player: "}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.Under15Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&8",
						"&8"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.15Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7",
						"&7"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.30Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&f",
						"&f"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.45Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e",
						"&e"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.60Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6",
						"&6"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.90Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c",
						"&c"}));
		languageKeys.put("CmdAfkRecord.GetAfk.PlayerColor.120Min",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4",
						"&4"}));
		
		languageKeys.put("CmdAfkRecord.GetTime.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fSpielzeiten &5%player%&e=====",
						"&e=====&fPlaytime &5%player%&e====="}));
		languageKeys.put("CmdAfkRecord.GetTime.Line",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&f%date%&7: &6Gesamt &f%alltime% | &aAktive &f%ontime% | &cAfk &f%afktime%",
						"&f%date%&7: &6Totaltime &f%alltime% | &aActive &f%ontime% | &cAfk &f%afktime%"}));
		
		languageKeys.put("CmdAfkRecord.Time.Headline",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fSpielzeiten &5%player%&e=====",
						"&e=====&fPlayetime &5%player%&e====="}));
		languageKeys.put("CmdAfkRecord.Time.ActiveTime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bAktive Spielzeit: &f%ontime% &f| &aPlatz &5%onplace%",
						"&bActivetime: &f%ontime% &f| &aPlace &5%onplace%"}));
		languageKeys.put("CmdAfkRecord.Time.AfkTime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bAfkzeit:           &f%afktime% &f| &aPlatz &5%afkplace%",
						"&bAfktime: &f%afktime% &f| &aPlace &5%afkplace%"}));
		languageKeys.put("CmdAfkRecord.Time.Alltime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bGesamte Zeit:    &f%alltime% &f| &aPlatz &5%allplace%",
						"&bTotal time: &f%alltime% &f| &aPlace &5%allplace%"}));
		languageKeys.put("CmdAfkRecord.Time.LastActive",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bLetzte Aktivität: &f%time%",
						"&Last activity: &f%time%"}));
		languageKeys.put("CmdAfkRecord.Time.HoverAcT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlicke hier für die Topliste der aktiven Zeit",
						"&eClick here for the top list of active time"}));
		languageKeys.put("CmdAfkRecord.Time.HoverAfT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlicke hier für die Topliste der Afkzeit",
						"&eClick here for the top list of the Afk time"}));
		languageKeys.put("CmdAfkRecord.Time.HoverAlT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlicke hier für die Topliste der Gesamtzeit",
						"&eClick here for the top list of total time"}));
		
		languageKeys.put("CmdAfkRecord.Top.HeadlineAcT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fTopListe Aktive Spielzeit&e=====",
						"&e=====&fTopListe Active Playtime&e====="}));
		languageKeys.put("CmdAfkRecord.Top.HeadlineAlT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fTopListe Gesamtzeit&e=====",
						"&e=====&fTopListe Total Time&e====="}));
		languageKeys.put("CmdAfkRecord.Top.HeadlineAfT",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fTopListe Afkzeit&e=====",
						"&e=====&fTopListe Afktime&e====="}));
		languageKeys.put("CmdAfkRecord.Top.PlaceAndTime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ePlatz &c%place%&f: &6%player% &f| &eZeit: &f%time%",
						"&ePlace &c%place%&f: &6%player% &f| &eTime: &f%time%"}));
		
		languageKeys.put("CmdAfkRecord.Top.HeadlineAcTLastDays",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fTopListe Aktive SpielzeitLetzte %days% Tage&e=====",
						"&e=====&fTopListe Active PlaytimeLast %days% days&e====="}));
		languageKeys.put("CmdAfkRecord.Top.HeadlineAlTLastDays",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fTopListe GesamtzeitLetzte %days% Tage&e=====",
						"&e=====&fTopListe Total TimeLast %days% days&e====="}));
		languageKeys.put("CmdAfkRecord.Top.HeadlineAfTLastDays",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&fTopListe Afkzeit Letzte %days% Tage&e=====",
						"&e=====&fTopListe Afktime Last %days% days&e====="}));
		
		languageKeys.put("CmdAfkRecord.Vacation.NotInVacation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu bist nicht im Urlaub!",
						"&eYou are not in vacation!"}));
		languageKeys.put("CmdAfkRecord.Vacation.YourAreInVacation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu bist bis zum &f%time% &ein Urlaub!",
						"&eYou are on vacation for the &f%time%&e!"}));
		languageKeys.put("CmdAfkRecord.Vacation.ThePlayerNotInVacation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &eist nicht im Urlaub!",
						"&eThe player &f%player% &eis not on vacation!"}));
		languageKeys.put("CmdAfkRecord.Vacation.ThePlayerIsInVacation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &eist zum &f%time% &eim Urlaub!",
						"&eThe player &f%player% &eis on vacation at &f%time%&e!"}));
		languageKeys.put("CmdAfkRecord.Vacation.WrongFormat",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cFalsche Formateingabe! Bitte an das Format &fdd.MM.yyyy HH:mm &chalten!",
						"&cWrong format input! Please switch to the format &fdd.MM.yyyy HH:mm&c!"}));
		languageKeys.put("CmdAfkRecord.Vacation.NoVacationInThePast",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu kannst keinen Urlaub für die Vergangenheit eintragen!",
						"&cYou cannot enter a vacation for the past!"}));
		languageKeys.put("CmdAfkRecord.Vacation.SetVacation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6Du bist nun bist zum &f%time% &6im Urlaub!",
						"&6You are now on vacation for the &f%time%&6!"}));
		languageKeys.put("CmdAfkRecord.Vacation.NowNotInVacation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDu bist nun nicht mehr im Urlaub!",
						"&6You are now no longer on vacation!"}));
		languageKeys.put("CmdAfkRecord.Add.TimeFormat",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutzte etweder eine Zahl als Millisekunde oder im Zeitformat dd:HH:mm:ss!",
						"&cPlease use either a number in milliseconds or in the time format dd:HH:mm:ss!"}));
		languageKeys.put("CmdAfkRecord.Add.Alltime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eEs wurden dem Spieler %player% %time% Gesamtzeit hinzugefügt.",
						"&eAdded %time% alltime to the player %player%."}));
		languageKeys.put("CmdAfkRecord.Add.Afktime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eEs wurden dem Spieler %player% %time% Afkzeit hinzugefügt.",
						"&eAdded %time% afktime to the player %player%."}));
		languageKeys.put("CmdAfkRecord.Add.Onlinetime",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eEs wurden dem Spieler %player% %time% Aktivezeit hinzugefügt.",
						"&eAdded %time% activetime to the player %player%."}));
		
		languageKeys.put("CmdAfk.SetAfk",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7[%time%] &eDu bist nun &cafk&e!",
						"&7[%time%] &eYou are &cafk &enow!"}));
		languageKeys.put("CmdAfk.SetAntiAfk",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7[%time%] &eDu bist &anicht mehr afk&e!",
						"&7[%time%] &eYou are &anot afk anymore&e!"}));
		languageKeys.put("CmdAfk.NoMoreAfk",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7[%time%] &eDu bist nicht mehr afk!",
						"&7[%time%] &eYou are no longer afk!"}));
		
		/*languageKeys.put(""
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				"",
				""}))*/
	}
}
