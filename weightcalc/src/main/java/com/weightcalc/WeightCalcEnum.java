package com.weightcalc;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;

public class WeightCalcEnum
{
	public interface WeighingItem
	{
		int id = -1;

		int getId();
	}

	public enum HalfKg implements WeighingItem
	{
		GNOMEBALL(ItemID.GNOMEBALL),
		SALMON(ItemID.SALMON);

		@Getter
		@Setter
		int id;

		HalfKg(int id)
		{
			this.setId(id);
		}
	}

	public enum TenthKg implements WeighingItem
	{
		GROUND_BAT_BONES(ItemID.GROUND_BAT_BONES),
		PIE_DISH(ItemID.PIE_DISH);

		@Getter
		@Setter
		int id;

		TenthKg(int id)
		{
			this.setId(id);
		}
	}

	public enum HundredthKg implements WeighingItem
	{
		BRASS_KEY(ItemID.BRASS_KEY),
		AMULET_OF_MAGIC(ItemID.AMULET_OF_MAGIC);

		@Getter
		@Setter
		int id;

		HundredthKg(int id)
		{
			this.setId(id);
		}
	}

	public enum ThousandthKg implements WeighingItem
	{
		ROCK(ItemID.ROCK_1480),
		AL_KHARID_FLYER(ItemID.AL_KHARID_FLYER),
		OAK_ROOTS(ItemID.OAK_ROOTS);

		@Getter
		@Setter
		int id;

		ThousandthKg(int id)
		{
			this.setId(id);
		}
	}
}
