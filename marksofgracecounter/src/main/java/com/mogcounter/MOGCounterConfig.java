/*
 * Copyright (c) 2020, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.mogcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("mogcounter")
public interface MOGCounterConfig extends Config
{
	@ConfigItem(
		keyName = "showMarkCount",
		name = "Show Overlay",
		description = "Show/Hide the Marks of Grace Counter (It keeps counting)",
		position = 1
	)
	default boolean showMarkCount()
	{
		return true;
	}

	@Range
	(
		min = 1,
		max = 60
	)
	@ConfigItem(
		keyName = "markTimeout",
		name = "Hide Overlay",
		description = "Time until the Marks of Grace Counter hides/resets (Uses 'Last Spawn Time')",
		position = 2
	)
	@Units(Units.MINUTES)
	default int markTimeout()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "showMarksSpawned",
		name = "Show On Ground Counter",
		description = "Shows how many Marks are currently on the ground (Recommended for Ardougne Rooftops)",
		position = 3
	)
	default boolean showMarksSpawned()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showMarkLastSpawn",
		name = "Show Time Since Last Spawn",
		description = "Shows the time since the last Mark of Grace spawned",
		position = 4
	)
	default boolean showMarkLastSpawn()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMarksPerHour",
		name = "Show Spawns per Hour",
		description = "Shows the estimated amount of Mark spawns per hour (After getting 2 spawns)",
		position = 5
	)
	default boolean showMarksPerHour()
	{
		return true;
	}
}
