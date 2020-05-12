package net.runelite.client.plugins.runecafecashflow;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("RuneCafe CashFlow")
public interface CashFlowConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "apiKey",
		name = "API key",
		description = "The API key you got from rune.cafe."
	)
	default String apiKey()
	{
		return "";
	}
}