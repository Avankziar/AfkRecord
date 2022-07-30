package main.java.de.avankziar.afkrecord.spigot.listener.afkcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import main.java.de.avankziar.afkrecord.spigot.AfkRecord;

public class PlayerListener
{
	public class AsyncChatListener extends BaseListener
	{
		public AsyncChatListener(AfkRecord plugin, BaseListener.EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onAsyncChat(AsyncPlayerChatEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerArmorStandManipulateListener extends BaseListener
	{
		public PlayerArmorStandManipulateListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	
	public class PlayerBedEnterListener extends BaseListener
	{
		public PlayerBedEnterListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerBedEnter(PlayerBedEnterEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerBucketEmptyListener extends BaseListener
	{
		public PlayerBucketEmptyListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerBucketFillListener extends BaseListener
	{
		public PlayerBucketFillListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerBucketFill(PlayerBucketFillEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerCommandPreprocessListener extends BaseListener
	{
		private String afkcmd;
		
		public PlayerCommandPreprocessListener(AfkRecord plugin, EventType eType, String afkcmd)
		{
			super(plugin, eType);
			this.afkcmd = afkcmd;
		}
		
		@EventHandler
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
		{
			if(!event.getMessage().equalsIgnoreCase(afkcmd.strip()))
			{
				doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
			}
		}
	}
	
	public class PlayerDropItemListener extends BaseListener
	{
		public PlayerDropItemListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerDropItem(PlayerDropItemEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerEditBookListener extends BaseListener
	{
		public PlayerEditBookListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerEditBook(PlayerEditBookEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerEggThrowListener extends BaseListener
	{
		public PlayerEggThrowListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerEggThrow(PlayerEggThrowEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerExpChangeListener extends BaseListener
	{
		public PlayerExpChangeListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerExpChange(PlayerExpChangeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerFishListener extends BaseListener
	{
		public PlayerFishListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerFish(PlayerFishEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerGameModeChangeListener extends BaseListener
	{
		public PlayerGameModeChangeListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerGameModeChange(PlayerGameModeChangeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerHarvestBlockListener extends BaseListener
	{
		public PlayerHarvestBlockListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerInteractListener extends BaseListener
	{
		public PlayerInteractListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerItemBreakListener extends BaseListener
	{
		public PlayerItemBreakListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerItemBreak(PlayerItemBreakEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerItemConsumeListener extends BaseListener
	{
		public PlayerItemConsumeListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerItemConsume(PlayerItemConsumeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerItemDamageListener extends BaseListener
	{
		public PlayerItemDamageListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerItemDamage(PlayerItemDamageEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerLevelChangeListener extends BaseListener
	{
		public PlayerLevelChangeListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerLevelChange(PlayerLevelChangeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerMoveListener extends BaseListener
	{
		public PlayerMoveListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerMove(PlayerMoveEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerToggleFlightListener extends BaseListener
	{
		public PlayerToggleFlightListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerToggleFlight(PlayerToggleFlightEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerToggleSneakListener extends BaseListener
	{
		public PlayerToggleSneakListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerToggleSprintListener extends BaseListener
	{
		public PlayerToggleSprintListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerToggleSprint(PlayerToggleSprintEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
	
	public class PlayerUnleashEntityListener extends BaseListener
	{
		public PlayerUnleashEntityListener(AfkRecord plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId(), event.isAsynchronous());
		}
	}
}