package com.dklights;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

public class LampPoint
{

	@Getter
	private int bitPosition;

	@Getter
	private WorldPoint worldPoint;

	@Getter
	private String description;

	@Getter
	@Setter
	private DKLightsEnum area = null;

	@Getter
	@Setter
	private boolean isBroken = false;

	public LampPoint(int bitPosition, WorldPoint worldPoint, String description)
	{
		this.bitPosition = bitPosition;
		this.worldPoint = worldPoint;
		this.description = description;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (!(o instanceof LampPoint))
		{
			return false;
		}

		LampPoint l = (LampPoint) o;

		if (this.worldPoint.equals(l.worldPoint))
		{
			return true;
		}

		return false;
	}
}
