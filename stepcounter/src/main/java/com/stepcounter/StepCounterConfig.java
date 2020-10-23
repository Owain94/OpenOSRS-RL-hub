package com.stepcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Step Counter")
public interface StepCounterConfig extends Config
{

	@ConfigItem(position = 1,
	keyName = "StepGoal",
	name = "Step Goal",
	description = "This is how many steps you want to take today")
	default int StepGoal(){
		return 10000;
	}

	@ConfigItem(position = 2,
	keyName = "ShowSteps",
	name = "Show Steps",
	description = "Show the overlay for the step count")
	default boolean ShowSteps(){
		return true;
	}
}
