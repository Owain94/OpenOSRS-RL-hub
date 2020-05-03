package net.runelite.client.plugins.pvpperformancetracker;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public enum RingData
{
	SEERS_RING("Seers Ring", 6731),
	ARCHERS_RING("Archers Ring", 6733),
	BERSERKER_RING("Berserker Ring", 6737),
	SEERS_RING_I("Seers Ring (i)", 11770),
	ARCHERS_RING_I("Archers Ring (i)", 11771),
	BERSERKER_RING_I("Berserker Ring (i)", 11773),
	BRIMSTONE_RING("Brimstone Ring", 22975),
	NONE("None", -1);

	private final String name;
	private final int itemId;

	RingData(String name, int itemId)
	{
		this.name = name;
		this.itemId = itemId;
	}

	@Override
	public String toString()
	{
		return name;
	}
}