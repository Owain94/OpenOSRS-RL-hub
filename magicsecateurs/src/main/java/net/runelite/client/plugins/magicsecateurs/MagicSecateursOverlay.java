package net.runelite.client.plugins.magicsecateurs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.TitleComponent;


class MagicSecateursOverlay extends OverlayPanel
{
	private static final int INVENTORY_SIZE = 28;

	@Inject
	private Client client;

	@Inject
	private MagicSecateursOverlay(Client client)
	{
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
		panelComponent.setPreferredSize(new Dimension(150, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);

		if (itemContainer == null)
		{
			return null;
		}

		final Item[] items = itemContainer.getItems();

		for (int i = 0; i < INVENTORY_SIZE; i++)
		{
			if (i < items.length)
			{
				final Item item = items[i];
				if (item.getQuantity() > 0)
				{
					if (item.getId() == ItemID.MAGIC_SECATEURS)
					{
						final String text = "Equip Secateurs!";
						final int textWidth = graphics.getFontMetrics().stringWidth(text);
						final int textHeight = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
						final int width = (int) client.getRealDimensions().getWidth();
						java.awt.Point jPoint = new java.awt.Point((width / 2) - textWidth, textHeight + 75);
						panelComponent.getChildren().clear();
						panelComponent.getChildren().add(TitleComponent.builder().text(text).color(Color.RED).build());
						panelComponent.setPreferredLocation(jPoint);
						return panelComponent.render(graphics);
					}
				}
			}
		}

		return null;
	}
}