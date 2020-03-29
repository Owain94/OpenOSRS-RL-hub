package net.runelite.client.plugins.healthnotifier;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Health Notifier",
	description = "Notifies you when the mob you are attacking is below certain health.",
	type = PluginType.UTILITY
)
@Slf4j
public class HealthNotifierPlugin extends Plugin
{
	@Inject
	private NPCManager npcManager;

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private HealthNotifierConfig healthNotificationConfig;

	private NPC currentNpc;
	private int lastHealth = 0;
	private boolean hasNotified = false;

	@Provides
	public HealthNotifierConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HealthNotifierConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		this.hasNotified = false;
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() == null)
		{
			return;
		}

		// If the NPC is being attacked by the player.
		if (event.getActor().getInteracting().equals(this.client.getLocalPlayer()) &&
			event.getActor().getName().equals(this.healthNotificationConfig.NPCName()) ||
			event.getActor().getInteracting().equals(this.client.getLocalPlayer()) &&
				this.healthNotificationConfig.NPCName().equals("")
		)
		{
			// If we didn't have a current NPC or we change our target.
			if (this.currentNpc == null || !this.currentNpc.equals(event.getActor()))
			{
				// Update this instance variables.
				this.currentNpc = (NPC) (event.getActor());
				this.lastHealth = this.npcManager.getHealth(this.currentNpc.getId());
				this.hasNotified = false;
			}

			this.lastHealth -= event.getHitsplat().getAmount();

			if (this.lastHealth <= this.healthNotificationConfig.specifiedHealth() && !this.hasNotified)
			{
				notifier.notify("The " + this.currentNpc.getName() + " you are attacking is below " +
					this.healthNotificationConfig.specifiedHealth() + " health!");
				this.hasNotified = true;
			}
		}
	}
}