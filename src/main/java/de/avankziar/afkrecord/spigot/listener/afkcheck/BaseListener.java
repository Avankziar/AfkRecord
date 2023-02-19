package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.Listener;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class BaseListener implements Listener
{	
	public enum EventType
	{
		//Player Events
		AsyncPlayerChat,
		PlayerArmorStandManipulate,
		PlayerBedEnter,
		PlayerBucketEmpty,
		PlayerBucketFill,
		PlayerCommandPreprocess,
		PlayerDropItem, 
		PlayerEditBook,
		PlayerEggThrow,
		PlayerExpChange(false),
		PlayerFish,
		PlayerGameModeChange,
		PlayerHarvestBlock(120),
		PlayerInteract,
		PlayerItemBreak,
		PlayerItemConsume,
		PlayerItemDamage(false),
		PlayerLevelChange,
		PlayerMove(false),
		PlayerToggleFlight,
		PlayerToggleSneak,
		PlayerToggleSprint,
		PlayerUnleashEntity,
		;
		
		private boolean isActive;
		private long cooldown;
		
		EventType()
		{
			this.isActive = true;
			this.cooldown = 60;
		}
		
		EventType(boolean isActive)
		{
			this.isActive = isActive;
			this.cooldown = 60;
		}
		
		EventType(long cooldown)
		{
			this.isActive = true;
			this.cooldown = 60;
		}
		
		EventType(boolean isActive, long cooldown)
		{
			this.isActive = isActive;
			this.cooldown = cooldown;
		}

		public boolean isActive()
		{
			return this.isActive;
		}
		
		public long getCooldown()
		{
			return this.cooldown;
		}
	}
	
	public AfkRecord plugin;
	private long eventCooldown = 0;
	private static HashMap<UUID, Long> cooldown = new HashMap<>();
	
	public BaseListener(AfkRecord plugin, BaseListener.EventType eType)
	{
		this.plugin = plugin;
		this.eventCooldown = AfkRecord.getPlugin().getYamlHandler().getConfig()
				.getLong("EventListener."+eType.toString()+".CooldownInSecond", 30L)*1000L;
	}
	
	public static boolean isEventActive(EventType eType)
	{
		return AfkRecord.getPlugin().getYamlHandler().getConfig().getBoolean("EventListener."+eType.toString()+".isActive", true);
	}
	
	public void doCheckAndSave(UUID uuid, boolean isAsync)
	{
		if(inCooldown(uuid))
		{
			return;
		}
		addCooldown(uuid);
		plugin.getPlayerTimes().saveRAM(uuid, true, false, false, isAsync);
	}
	
	public boolean inCooldown(UUID uuid)
	{
		if(!cooldown.containsKey(uuid))
		{
			return false;
		}
		if(cooldown.get(uuid) > System.currentTimeMillis())
		{
			return true;
		}
		return false;
	}
	
	public void addCooldown(UUID uuid)
	{
		cooldown.put(uuid, System.currentTimeMillis()+eventCooldown);
	}
}