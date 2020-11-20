package com.bram91.fishingnotifier;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Fishing Notifier",
	description = "Notifies when idling while fishing.",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class FishingNotifierPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private FishingNotifierConfig config;

	@Provides
	FishingNotifierConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FishingNotifierConfig.class);
	}

	FishingSpot previousTick;
	FishingSpot currentTick;
	public boolean checkNotify()
	{
		Actor actor = client.getLocalPlayer().getInteracting();
		if(actor != null)
		{
			if (actor instanceof NPC)
			{
				FishingSpot spot = FishingSpot.findSpot(((NPC)actor).getId());
				if (spot != null)
				{
					previousTick = currentTick;
					currentTick = spot;
				}
			}
		}
		else
		{
			previousTick = currentTick;
			currentTick = null;
			if(previousTick != null)
			{
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOADING:
			case LOGIN_SCREEN:
			case HOPPING:
				previousTick = null;
				currentTick = null;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		boolean notify = checkNotify();
		if(!notify)
		{
			return;
		}
		boolean shouldNotify = false;
		if (config.eelFishing() && (FishingSpot.SACRED_EEL.getName().equals(previousTick.getName()) || FishingSpot.INFERNAL_EEL.getName().equals(previousTick.getName())))
		{
			shouldNotify = true;
		}
		else if(config.barbFishing() && FishingSpot.BARB_FISH.getName().equals(previousTick.getName()))
		{
			shouldNotify = true;
		}
		else if(config.anglerFishing() && FishingSpot.ANGLERFISH.getName().equals((previousTick.getName())))
		{
			shouldNotify = true;
		}
		else if(config.karambwanFishing() && FishingSpot.KARAMBWAN.getName().equals((previousTick.getName())))
		{
			shouldNotify = true;
		}
		else if(config.monkFishing() && FishingSpot.MONKFISH.getName().equals((previousTick.getName())))
		{
			shouldNotify = true;
		}
		else if(config.otherFishing() &&(
				FishingSpot.LOBSTER.getName().equals(previousTick.getName()) ||
				FishingSpot.DARK_CRAB.getName().equals(previousTick.getName()) ||
				FishingSpot.SHRIMP.getName().equals(previousTick.getName()) ||
				FishingSpot.SHARK.getName().equals(previousTick.getName()) ||
				FishingSpot.MINNOW.getName().equals(previousTick.getName()) ||
				FishingSpot.KARAMBWANJI.getName().equals(previousTick.getName()) ||
				FishingSpot.SALMON.getName().equals(previousTick.getName())
		))
		{
			shouldNotify = true;
		}
		if(shouldNotify)
		{
			notifier.notify("[" + client.getLocalPlayer().getName() + "] is slacking!");
		}
	}

}
