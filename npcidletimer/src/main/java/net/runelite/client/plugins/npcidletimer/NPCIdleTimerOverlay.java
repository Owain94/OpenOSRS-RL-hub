package net.runelite.client.plugins.npcidletimer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class NPCIdleTimerOverlay extends Overlay
{

	private final Client client;
	private final NPCIdleTimerPlugin plugin;
	private final NPCIdleTimerConfig config;

	NumberFormat format = new DecimalFormat("#");

	int NPC_IDLE_RESPAWN_TIME = 300;

	@Inject
	NPCIdleTimerOverlay(Client client, NPCIdleTimerPlugin plugin, NPCIdleTimerConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (config.showOverlay())
		{
			plugin.getWanderingNPCs().forEach((id, npc) -> renderTimer(npc, graphics));
		}
		return null;
	}

	private void renderTimer(final WanderingNPC npc, final Graphics2D graphics)
	{
		double timeLeft = NPC_IDLE_RESPAWN_TIME - npc.getTimeWithoutMoving();

		if (timeLeft < 0)
		{
			timeLeft = 0;
		}

		final String timeLeftString = String.valueOf(format.format(timeLeft));

		final Point canvasPoint = npc.getNpc().getCanvasTextLocation(graphics, timeLeftString, npc.getNpc().getLogicalHeight() + config.timerHeight());

		if (canvasPoint != null && (config.maxDisplay() >= timeLeft))
		{
			OverlayUtil.renderTextLocation(graphics, canvasPoint, timeLeftString, config.timerColor());
		}
	}

}