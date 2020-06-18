package net.runelite.client.plugins.weightcalc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

@Slf4j
public class WeightCalcWidgetItemOverlay extends WidgetItemOverlay
{
	private final WeightCalcConfig config;
	private final WeightCalcPlugin plugin;

	@Inject
	private WeightCalcWidgetItemOverlay(WeightCalcPlugin plugin, WeightCalcConfig config)
	{
		this.plugin = plugin;
		this.config = config;
		showOnInventory();
		showOnEquipment();
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		graphics.setFont(FontManager.getRunescapeSmallFont());


		WeightCalcMessage wm = WeightCalcPlugin.getWm();
		if (wm != null && itemId == wm.getItemId())
		{
			final Rectangle bounds = itemWidget.getCanvasBounds();
			final TextComponent textComponent = new TextComponent();
			if (wm.isWithdrawMore())
			{
				textComponent.setColor(new Color(0x00FF00));
			}
			else
			{
				textComponent.setColor(new Color(0xFF0000));
			}
			textComponent.setText("****");
			textComponent.setPosition(new Point(bounds.x - 1, bounds.y - 1 + bounds.height));
			textComponent.render(graphics);
		}
	}
}