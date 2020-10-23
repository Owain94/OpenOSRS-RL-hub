package pharros.probabilitycalculator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("probability_calculator")
public interface ProbabilityCalculatorConfig extends Config
{
	@Range(max = 10)
	@ConfigItem(
		keyName = "decimalPlaces",
		name = "Decimal Places",
		description = "The maximum number of decimal places to round the output up to"
	)
	default int getDecimalPlaces()
	{
		return 2;
	}
}
