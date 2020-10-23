/*
 * Copyright (c) 2020, Lotto <https://github.com/devLotto>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.monkeymetrics;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(MonkeyMetricsPlugin.CONFIG_KEY)
public interface MonkeyMetricsConfig extends Config
{
	@ConfigItem(
		keyName = "showAttackMetrics",
		name = "Attack Metrics",
		description = "Shows information about the last attack, including the amount of hitsplats and total damage dealt.",
		position = 0
	)
	default boolean showMetrics()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showNecklaceInfoBox",
		name = "Necklace Activation",
		description = "Shows an infobox counting down until the Bonecrusher necklace is activated.",
		position = 1
	)
	default boolean showNecklaceInfoBox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showNpcStacks",
		name = "NPC Stacks",
		description = "Shows how many NPCs are stacked together on a single tile.",
		position = 2
	)
	default boolean showNpcStacks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "minimumNpcStackSize",
		name = "Min. NPC Stack Size",
		description = "The minimum amount of NPCs that need to be on a single tile for the count to be displayed.",
		position = 3
	)
	default int minimumNpcStackSize()
	{
		return 2;
	}
}
