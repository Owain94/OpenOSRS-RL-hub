package net.runelite.client.plugins.flipper.controllers;

import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.flipper.helpers.TradePersister;
import net.runelite.client.plugins.flipper.models.Flip;
import net.runelite.client.plugins.flipper.views.margins.MarginsPanel;

public class MarginsController
{
	@Getter
	@Setter
	private List<Flip> margins;
	private MarginsPanel marginsPanel;

	public MarginsController(ItemManager itemManager) throws IOException
	{
		this.marginsPanel = new MarginsPanel(itemManager);
		this.loadMargins();
	}

	public void addMargin(Flip margin)
	{
		this.margins.add(margin);
		this.marginsPanel.rebuildPanel(margins);
	}

	public MarginsPanel getPanel()
	{
		return this.marginsPanel;
	}

	private void loadMargins() throws IOException
	{
		this.margins = TradePersister.loadMargins();
		this.marginsPanel.rebuildPanel(margins);
	}

	public void saveMargins()
	{
		TradePersister.saveMargins(margins);
	}
}