package com.weightcalc;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.math.BigDecimal;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemDefinition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

public class WeightCalcOverlayPanel extends OverlayPanel
{

	private final WeightCalcConfig config;
	private final WeightCalcPlugin plugin;

	@Inject
	private ItemManager itemManager;

	@Inject
	private WeightCalcOverlayPanel(WeightCalcPlugin plugin, WeightCalcConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	private void addTextToOverlayPanel(String text)
	{
		panelComponent.getChildren().add(LineComponent.builder().left(text).build());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		BigDecimal minWeight = WeightCalcPlugin.getMinWeight().setScale(3, RoundingMode.HALF_UP);
		BigDecimal maxWeight = WeightCalcPlugin.getMaxWeight().setScale(3, RoundingMode.HALF_UP);
		BigDecimal aloneWeight = WeightCalcPlugin.getAloneWeightBD();
		int state = plugin.getState();

		panelComponent.getChildren().clear();

		if (state == WeightCalcPlugin.STATE_EQUIPPED)
		{
			addTextToOverlayPanel("Deposit all equipped items and items in your inventory.");
		}
		else if (state == WeightCalcPlugin.STATE_EMPTY)
		{
			addTextToOverlayPanel("Add the item to weigh to your inventory.");
		}
		else if (state == WeightCalcPlugin.STATE_ITEM_UNKNOWN)
		{
			addTextToOverlayPanel("Empty your inventory.");
		}
		else if (state == WeightCalcPlugin.STATE_TOO_MANY_ITEMS)
		{
			addTextToOverlayPanel("Remove extra non-weighing items from your inventory.");
		}
		else if (state == WeightCalcPlugin.STATE_UNKNOWN)
		{
			addTextToOverlayPanel("Please restart the plugin and try again.");
		}
		else
		{
			Item currentItem = WeightCalcPlugin.getCurrentItem();
			ItemDefinition item = itemManager.getItemDefinition(currentItem.getId());

			addTextToOverlayPanel("Weighing: " + item.getName());
			addTextToOverlayPanel("");

			if (WeightCalcPlugin.getWm() != null)
			{
				ItemDefinition weighingItem = itemManager.getItemDefinition(WeightCalcPlugin.getWm().getItemId());
				String message = (WeightCalcPlugin.getWm().isWithdrawMore() ? "Withdraw " : "Deposit ") + weighingItem.getName();
				addTextToOverlayPanel(message);
			}
			if (minWeight.compareTo(maxWeight) == 0)
			{
				addTextToOverlayPanel("Final weight: " + (BigDecimal.ONE.subtract(maxWeight).add(aloneWeight).toString()));
			}
			// Might want to add this as a toggle for debugging.
			else if (config.showWeightsRange())
			{
				addTextToOverlayPanel("Possible weights: " + (BigDecimal.ONE.subtract(maxWeight).add(aloneWeight)) + " - " + (BigDecimal.ONE.subtract(minWeight).add(aloneWeight)));
			}
		}

		return super.render(graphics);
	}
}
