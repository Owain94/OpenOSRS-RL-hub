package com.dklights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Dorgesh-Kaan Lights",
	description = "Makes it easier to find broken lamps in Dorgesh-Kaan.",
	type = PluginType.MINIGAME,
	enabledByDefault = false
)
public class DKLightsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DKLightsOverlayPanel overlayPanel;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private static int lamps;

	@Getter
	private DKLightsEnum currentArea;

	@Getter
	private WorldPoint currentPoint;

	private static DKLightsHelper helper;

	// This varbit tells you which lamps are broken based on plane and map square
	private static final int DK_LIGHTS = 4038;

	@Getter
	private static HashSet<LampPoint> brokenLamps = new HashSet<>();

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlayPanel);
		helper = new DKLightsHelper();
		helper.init();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlayPanel);
		client.clearHintArrow();
	}

	private static boolean tickFlag = true;

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		WorldPoint tempPoint = player.getWorldLocation();
		DKLightsEnum tempArea = helper.determineLocation(tempPoint);
		int tempLamps = client.getVarbitValue(DK_LIGHTS);

		// Because the varbit updates AFTER location change, we should wait a tick if the area
		// changes but the lamp varbit does not.
		// Otherwise, the new area may be updated with the bits from the previous area.
		if (tempArea != currentArea && tempLamps == lamps && tickFlag)
		{
			tickFlag = false;
			return;
		}
		tickFlag = true;

		// If we have changed areas or the lamps varb, we need to reload the overlay.
		if (tempArea != currentArea || tempLamps != lamps)
		{
			currentArea = tempArea;
			if (tempArea == DKLightsEnum.BAD_AREA)
			{
				return;
			}
			ArrayList<LampPoint> lampPoints = helper.getAreaLamps(tempLamps, currentArea);
			for (LampPoint l : lampPoints)
			{
				if (l.isBroken())
				{
					brokenLamps.add(l);
				}
				else
				{
					brokenLamps.remove(l);
				}
			}
		}

		// Point to the closest broken lamp after moving or fixing a lamp
		// Note that tempArea != currentArea => tempPoint != currentPoint
		if (tempPoint != currentPoint || tempLamps != lamps)
		{
			currentPoint = tempPoint;
			lamps = tempLamps;
			if (brokenLamps != null && brokenLamps.size() > 0)
			{
				ArrayList<LampPoint> sortedLamps = helper.sortBrokenLamps(brokenLamps, currentPoint);
				if (!sortedLamps.isEmpty())
				{
					LampPoint closestLamp = sortedLamps.get(0);
					client.clearHintArrow();
					client.setHintArrow(closestLamp.getWorldPoint());
				}
			}
			else
			{
				client.clearHintArrow();
			}
		}
	}
}
