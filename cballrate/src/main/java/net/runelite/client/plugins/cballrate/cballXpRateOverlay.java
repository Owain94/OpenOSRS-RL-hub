package net.runelite.client.plugins.cballrate;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class cballXpRateOverlay extends OverlayPanel
{
	private final cballXpRatePlugin plugin;

	@Inject
	public cballXpRateOverlay(cballXpRatePlugin plugin)
	{
		super(plugin);

		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Slayer XP/Cball:")
			.right(Double.toString(plugin.getSlayerXPperCball()))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Ranged XP/Cball:")
			.right(Double.toString(plugin.getRangedXPperCball()))
			.build());

		return super.render(graphics);
	}
}
