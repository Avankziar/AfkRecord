package main.java.de.avankziar.afkrecord.bungee;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility 
{	
	public  String getDate()//FIN
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dt = sdf.format(now);
		return dt;
	}
}
