package net.runelite.client.plugins.eclecticjars;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class EclecticOverlay extends Overlay
{
	private EclecticPlugin plugin;

	private PanelComponent panelComponent = new PanelComponent();

	@Inject
	public EclecticOverlay(EclecticPlugin plugin)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.plugin = plugin;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Eclectic Jars")
			.build());

		panelComponent.getChildren().add(LineComponent.builder().left("Jars Opened:")
			.right(Integer.toString(plugin.getJarsOpened())).build());
		panelComponent.getChildren().add(LineComponent.builder().left("GP Spent:")
			.right(Double.toString(plugin.getMoneySpent())).build());
		panelComponent.getChildren().add(LineComponent.builder().left("GP Gained:")
			.right(Double.toString(plugin.getMoneyGained())).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Profit:")
			.right(Double.toString(plugin.getProfit())).rightColor(plugin.getProfitColor()).build());

		return panelComponent.render(graphics);
	}
}
