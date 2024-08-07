package main.java.me.avankziar.afkr.spigot.listener.afkcheck;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.Listener;

import main.java.me.avankziar.afkr.spigot.AfkR;

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
	
	public AfkR plugin;
	private long eventCooldown = 0;
	private static HashMap<UUID, Long> cooldown = new HashMap<>();
	
	public BaseListener(AfkR plugin, BaseListener.EventType eType)
	{
		this.plugin = plugin;
		this.eventCooldown = AfkR.getPlugin().getYamlHandler().getConfig()
				.getLong("EventListener."+eType.toString()+".CooldownInSecond", 30L)*1000L;
	}
	
	public static boolean isEventActive(EventType eType)
	{
		return AfkR.getPlugin().getYamlHandler().getConfig().getBoolean("EventListener."+eType.toString()+".isActive", true);
	}
	
	public void doCheckAndSave(UUID uuid)
	{
		if(inCooldown(uuid))
		{
			return;
		}
		addCooldown(uuid);
		plugin.getPlayerTimes().saveRAM(uuid, true, false, false);
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