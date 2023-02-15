package main.java.de.avankziar.afkrecord.spigot.assistance;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimeHandler
{
	private final static long ss = 1000;
	private static long mm = 1000*60;
	private static long HH = 1000*60*60;
	private static long dd = 1000*60*60*24;
	private static long MM = 1000*60*60*24*30;
	private final static long yyyy = 1000*60*60*24*365;
	
	public static long getDateTime(String l)
	{
		return LocalDateTime.parse(l, DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss"))
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static String getDate(long l)
	{
		Date date = new Date(l);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
		sdf.setTimeZone(TimeZone.getDefault()); 
		return sdf.format(date);
	}
	
	public static long getDate(String l)
	{
		Instant instant = Instant.now();
		ZoneId systemZone = ZoneId.systemDefault();
		ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(instant);
		
		return LocalDate.parse(l, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
				.atTime(LocalTime.MIDNIGHT).toEpochSecond(currentOffsetForMyZone)*1000;
	}
	
	public static String getTime(long l)
	{
		return new Time(l).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}
	
	/*public static long getTime(String l)
	{
		
	}*/
	
	public static String getRepeatingTime(long l, String timeformat) // yyyy-dd-HH:mm
	{
		long ll = l;
		/*String year = "";
		long y = Math.floorDiv(ll, yyyy);
		year += String.valueOf(y);
		ll = ll - y*yyyy;*/
		
		/*String month = "";
		long M = Math.floorDiv(ll, MM);
		month += String.valueOf(M);
		ll = ll - M*MM;*/
		
		String day = "";
		long d = Math.floorDiv(ll, dd);
		day += String.valueOf(d);
		ll = ll - d*dd;
		
		String hour = "";
		long H = Math.floorDiv(ll, HH);
		if(H < 10)
		{
			hour += String.valueOf(0);
		}
		hour += String.valueOf(H);
		ll = ll - H*HH;
		
		long m = Math.floorDiv(ll, mm);
		String min = "";
		if(m < 10)
		{
			min += String.valueOf(0);
		}
		min += String.valueOf(m);
		ll = ll - m*mm;
		
		long s = Math.floorDiv(ll, ss);
		String sec = "";
		if(s < 10)
		{
			sec += String.valueOf(0);
		}
		sec += String.valueOf(s);
		String time = timeformat//.replace("yyyy", year)
								//.replace("MM", month)
								.replace("dd", day)
								.replace("HH", hour)
								.replace("mm", min)
								.replace("ss", sec);
		return time;
	}
	
	public static long getRepeatingTime(String l) //yyyy-MM-dd-HH:mm
	{
		String[] a = l.split("-");
		if(!MatchApi.isInteger(a[0]))
		{
			return 0;
		}
		int y = Integer.parseInt(a[0]);
		int M = Integer.parseInt(a[1]);
		int d = Integer.parseInt(a[2]);
		String[] b = a[3].split(":");
		if(!MatchApi.isInteger(b[0]))
		{
			return 0;
		}
		if(!MatchApi.isInteger(b[1]))
		{
			return 0;
		}
		int H = Integer.parseInt(b[0]);
		int m = Integer.parseInt(b[1]);
		long time = y*yyyy+M*MM+d*dd + H*HH + m*mm;
		return time;
	}
}