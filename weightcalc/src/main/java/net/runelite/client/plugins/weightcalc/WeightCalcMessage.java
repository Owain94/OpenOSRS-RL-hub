package net.runelite.client.plugins.weightcalc;

import lombok.Getter;

class WeightCalcMessage
{
	@Getter
	private int itemId;

	@Getter
	private boolean withdrawMore;

	public WeightCalcMessage(int itemId, boolean withdrawMore)
	{
		this.itemId = itemId;
		this.withdrawMore = withdrawMore;
	}
}