package net.runelite.client.plugins.weightcalc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(WeightCalcPlugin.CONFIG_GROUP_KEY)
public interface WeightCalcConfig extends Config
{
	String HALF_KG_KEYNAME = "0.500";
	String TENTH_KG_KEYNAME = "0.100";
	String HUNDREDTH_KG_KEYNAME = "0.010";
	String THOUSANDTH_KG_KEYNAME = "0.001";
	String SHOW_WEIGHTS_TOGGLE_KEYNAME = "showWeightsToggle";

	@ConfigItem(keyName = HALF_KG_KEYNAME, name = "0.500 kg", description = "Weighing item to use for 0.500 kg")
	default WeightCalcEnum.HalfKg halfKgConfig()
	{
		return WeightCalcEnum.HalfKg.SALMON;
	}

	@ConfigItem(keyName = TENTH_KG_KEYNAME, name = "0.100 kg", description = "Weighing item to use for 0.100 kg")
	default WeightCalcEnum.TenthKg tenthKgConfig()
	{
		return WeightCalcEnum.TenthKg.PIE_DISH;
	}

	@ConfigItem(keyName = HUNDREDTH_KG_KEYNAME, name = "0.010 kg", description = "Weighing item to use for 0.010 kg")
	default WeightCalcEnum.HundredthKg hundredthKgConfig()
	{
		return WeightCalcEnum.HundredthKg.BRASS_KEY;
	}

	@ConfigItem(keyName = THOUSANDTH_KG_KEYNAME, name = "0.001 kg", description = "Weighing item to use for 0.001 kg")
	default WeightCalcEnum.ThousandthKg thousandthKgConfig()
	{
		return WeightCalcEnum.ThousandthKg.OAK_ROOTS;
	}

	@ConfigItem(keyName = SHOW_WEIGHTS_TOGGLE_KEYNAME, name = "Visible weight range", description = "Display the possible range of weights as items are weighed")
	default boolean showWeightsRange()
	{
		return false;
	}
}