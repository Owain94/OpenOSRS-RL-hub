package net.runelite.client.plugins.chompyhunter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Chompy Hunter",
	description = "A plugin to overlay chompy birds with a timer and colour based on remaining time till despawn.",
	tags = {"chompy", "bird", "hunt", "hunting", "chompies"},
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
public class ChompyHunterPlugin extends Plugin
{

	@Inject
	private OverlayManager overlayManager;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(overlayInfo);
		chompies.clear();
		ChompyKills = 0;
		StartTime = null;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(overlayInfo);
		chompies.clear();
		ChompyKills = 0;
		StartTime = null;

	}

	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, Chompy> chompies = new HashMap<>();

	@Getter(AccessLevel.PACKAGE)
	private int ChompyKills;

	@Getter(AccessLevel.PACKAGE)
	private Instant StartTime;

	@Inject
	private Client client;

	@Inject
	private ChompyHunterOverlay overlay;

	@Inject
	private ChompyHunterInfoOverlay overlayInfo;

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getMessage().equals("You scratch a notch on your bow for the chompy bird kill.") && chatMessage.getType() == ChatMessageType.SPAM)
		{
			if (StartTime == null)
			{
				StartTime = Instant.now();
			}
			ChompyKills++;
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();

		if (npc == null)
		{
			return;
		}

		String name = event.getNpc().getName();

		assert name != null;
		if (name.equals("Chompy bird") && !chompies.containsKey(npc.getIndex()))
		{
			chompies.put(npc.getIndex(), new Chompy(npc));
		}
	}

	@Subscribe
	private void onNpcDespawned(NpcDespawned event)
	{
		NPC npc = event.getNpc();
		String name = event.getNpc().getName();
		assert name != null;
		if (name.equals("Chompy bird") && chompies.containsKey(npc.getIndex()))
		{
			chompies.remove(event.getNpc().getIndex());
		}
	}
}