package com.dklights;

public enum DKLightsEnum
{
	P0_N(0),
	P0_S(1),
	P1_N(2),
	P1_S(3),
	P2_N(4),
	P2_S(5),
	BAD_AREA(6);

	public final int value;

	private DKLightsEnum(int value)
	{
		this.value = value;
	}
}
