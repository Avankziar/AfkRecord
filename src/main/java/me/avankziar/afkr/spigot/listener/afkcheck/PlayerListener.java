package main.java.me.avankziar.afkr.spigot.listener.afkcheck;

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

import main.java.me.avankziar.afkr.spigot.AfkR;

public class PlayerListener
{
	public class AsyncChatListener extends BaseListener
	{
		public AsyncChatListener(AfkR plugin, BaseListener.EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onAsyncChat(AsyncPlayerChatEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerArmorStandManipulateListener extends BaseListener
	{
		public PlayerArmorStandManipulateListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	
	public class PlayerBedEnterListener extends BaseListener
	{
		public PlayerBedEnterListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerBedEnter(PlayerBedEnterEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerBucketEmptyListener extends BaseListener
	{
		public PlayerBucketEmptyListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerBucketFillListener extends BaseListener
	{
		public PlayerBucketFillListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerBucketFill(PlayerBucketFillEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerCommandPreprocessListener extends BaseListener
	{
		private String afkcmd;
		
		public PlayerCommandPreprocessListener(AfkR plugin, EventType eType, String afkcmd)
		{
			super(plugin, eType);
			this.afkcmd = afkcmd;
		}
		
		@EventHandler
		public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
		{
			if(!event.getMessage().equalsIgnoreCase(afkcmd.strip()))
			{
				doCheckAndSave(event.getPlayer().getUniqueId());
			}
		}
	}
	
	public class PlayerDropItemListener extends BaseListener
	{
		public PlayerDropItemListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerDropItem(PlayerDropItemEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerEditBookListener extends BaseListener
	{
		public PlayerEditBookListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerEditBook(PlayerEditBookEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerEggThrowListener extends BaseListener
	{
		public PlayerEggThrowListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerEggThrow(PlayerEggThrowEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerExpChangeListener extends BaseListener
	{
		public PlayerExpChangeListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerExpChange(PlayerExpChangeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerFishListener extends BaseListener
	{
		public PlayerFishListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerFish(PlayerFishEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerGameModeChangeListener extends BaseListener
	{
		public PlayerGameModeChangeListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerGameModeChange(PlayerGameModeChangeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerHarvestBlockListener extends BaseListener
	{
		public PlayerHarvestBlockListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerHarvestBlock(PlayerHarvestBlockEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerInteractListener extends BaseListener
	{
		public PlayerInteractListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerItemBreakListener extends BaseListener
	{
		public PlayerItemBreakListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerItemBreak(PlayerItemBreakEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerItemConsumeListener extends BaseListener
	{
		public PlayerItemConsumeListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerItemConsume(PlayerItemConsumeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerItemDamageListener extends BaseListener
	{
		public PlayerItemDamageListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerItemDamage(PlayerItemDamageEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerLevelChangeListener extends BaseListener
	{
		public PlayerLevelChangeListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerLevelChange(PlayerLevelChangeEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerMoveListener extends BaseListener
	{
		public PlayerMoveListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerMove(PlayerMoveEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerToggleFlightListener extends BaseListener
	{
		public PlayerToggleFlightListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerToggleFlight(PlayerToggleFlightEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerToggleSneakListener extends BaseListener
	{
		public PlayerToggleSneakListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerToggleSprintListener extends BaseListener
	{
		public PlayerToggleSprintListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerToggleSprint(PlayerToggleSprintEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
	
	public class PlayerUnleashEntityListener extends BaseListener
	{
		public PlayerUnleashEntityListener(AfkR plugin, EventType eType)
		{
			super(plugin, eType);
		}

		@EventHandler
		public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event)
		{
			doCheckAndSave(event.getPlayer().getUniqueId());
		}
	}
}