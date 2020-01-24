package main.java.de.avankziar.afkrecord.spigot.interfaces;

public class TopList 
{
	private int place;
	private String name;
	private long time;
	
	public TopList(int place, String name, long time)
	{
		setPlace(place);
		setName(name);
		setTime(time);
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
