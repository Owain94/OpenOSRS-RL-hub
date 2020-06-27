package net.runelite.client.plugins.crowdsourcing.movement;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuOpcode;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;

public class CrowdsourcingMovement
{
	@Inject
	private Client client;

	@Inject
	private CrowdsourcingManager manager;

	private WorldPoint lastPoint;
	private int ticksStill;
	private boolean lastIsInInstance;
	private MenuOptionClicked lastClick;

	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (menuOptionClicked.getMenuOpcode() != MenuOpcode.WALK
			&& !menuOptionClicked.getOption().equals("Message"))
		{
			lastClick = menuOptionClicked;
		}
	}

	public void onGameTick(GameTick tick)
	{
		LocalPoint local = LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation());
		WorldPoint nextPoint = WorldPoint.fromLocalInstance(client, local);
		boolean nextIsInInstance = client.isInInstancedRegion();
		if (lastPoint != null)
		{
			int distance = nextPoint.distanceTo(lastPoint);
			if (distance > 2 || nextIsInInstance != lastIsInInstance)
			{
				MovementData data = new MovementData(lastPoint, nextPoint, lastIsInInstance, nextIsInInstance, ticksStill, lastClick);
				manager.storeEvent(data);
			}
			if (distance > 0)
			{
				ticksStill = 0;
			}
		}
		ticksStill++;
		lastPoint = nextPoint;
		lastIsInInstance = nextIsInInstance;
	}
}