package main.java.de.avankziar.afkrecord.spigot.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import main.java.de.avankziar.afkrecord.spigot.database.Language.ISO639_2B;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
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
	public void setFileInput(YamlConfiguration yml, LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
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
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void initConfig() //INFO:Config
	{
		configKeys.put("Language"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"ENG"}));
		configKeys.put("Mysql.Status"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("Mysql.Host"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"127.0.0.1"}));
		configKeys.put("Mysql.Port"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3306}));
		configKeys.put("Mysql.DatabaseName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mydatabase"}));
		configKeys.put("Mysql.SSLEnabled"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("Mysql.AutoReconnect"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("Mysql.VerifyServerCertificate"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("Mysql.User"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"admin"}));
		configKeys.put("Mysql.Password"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"not_0123456789"}));
		configKeys.put("Mysql.TableNameI"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecordPlayerData"}));
		configKeys.put("Mysql.TableNameII"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"afkrecordDateList"}));
		
		configKeys.put("General.SoftSaveInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				60}));
		configKeys.put("General.MysqlSaveInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				600}));
		configKeys.put("General.SaveInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				60}));
		configKeys.put("General.AfkCheckerInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				300}));
		configKeys.put("General.AfkAfterInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				900}));
		configKeys.put("General.TopListRefresh"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				300}));
		configKeys.put("EventListener.AsyncChat"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.CommandPreprocess"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.Fish"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.InteractEntity"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.ItemConsume"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.LevelChange"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.Move"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configKeys.put("EventListener.ToggleSneak"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configKeys.put("EventListener.ToggleSprint"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
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
		argumentInput("afkr_convert", "convert", basePermission,
				"/afkr convert", "/afkr convert ",
				"&c/afkr convert &f| Konvertiert alte Datenbankeinträge auf Build 8.",
				"&c/afkr convert &f| Converts old database entries to Build 8.");
		argumentInput("afkr_counttime", "counttime", basePermission,
				"/afkr counttime <tage> [Spieler]", "/afkr counttime ",
				"&c/afkr counttime <tage> [Spieler] &f| Zählt alle Zeiten in einem Zeitraum.",
				"&c/afkr counttime <days> [player] &f| Counts all times in a period.");
		argumentInput("afkr_counttimeperm", "counttimeperm", basePermission,
				"/afkr counttimeperm <tage> <Permission>", "/afkr counttime ",
				"&c/afkr counttimeperm <tage> <Permission> &f| Zählt alle Spieler mit dieser Permission auf, und zeigt ihre Zeiten.",
				"&c/afkr counttimeperm <days> <Permission> &f| Enumerates all players with this permission, and shows their times.");
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
				"/afkr top <onlinetime|alltime|afktime> [number]", "/afkr top ",
				"&c/afkr top <onlinetime|alltime|afktime> [Zahl] &f| Zwischenbefehl.",
				"&c/afkr top <onlinetime|alltime|afktime> [number] &f| Subcommand");
		argumentInput("afkr_top_onlinetime", "onlinetime", basePermission,
				"/afkr top onlinetime [number]", "/afkr top onlinetime",
				"&c/afkr top onlinetime [Zahl] &f| Zeigt die Topliste an, 10 Plätze pro Seite.",
				"&c/afkr top onlinetime [number] &f| Displays the top list, 10 places per page.");
		argumentInput("afkr_top_alltime", "alltime", basePermission,
				"/afkr top alltime [number]", "/afkr top alltime ",
				"&c/afkr top alltime [number] &f| Zeigt die Topliste an, 10 Plätze pro Seite.",
				"&c/afkr top alltime [number] &f| Displays the top list, 10 places per page.");
		argumentInput("afkr_top_afktime", "afktime", basePermission,
				"/afkr top afktime [number]", "/afkr top afktime ",
				"&c/afkr top afktime [number] &f| Zeigt die Topliste an, 10 Plätze pro Seite.",
				"&c/afkr top afktime [number] &f| Displays the top list, 10 places per page.");
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
		
		languageKeys.put("Time.Seconds",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&es",
						"&es"}));
		languageKeys.put("Time.Minutes",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&6min &f",
						"&6min &f"}));
		languageKeys.put("Time.Hours",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&ch &f",
						"&ch &f"}));
		languageKeys.put("Time.Days",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&4Tage &f",
						"&4days &f"}));
		languageKeys.put("Time.Years",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&dJahre &f",
						"&dyears &f"}));
		
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
