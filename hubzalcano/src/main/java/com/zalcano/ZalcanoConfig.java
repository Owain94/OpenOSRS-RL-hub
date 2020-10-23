/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package com.zalcano;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Zalcano")
public interface ZalcanoConfig extends Config
{
	@ConfigItem(
			position = 1,
			keyName = "PlayerCount",
			name = "Show amount of players in the room",
			description = "Shows amount of players in the Zalcano room. (Players standing at the gate are excluded)"
	)
	default boolean showPlayerCount()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "Health",
			name = "Show Zalcano health & Phase based on health",
			description = "Shows current Zalcano health."
	)
	default boolean showHealth()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "Damage",
		name = "Show damage dealt by player",
		description = "Shows damage dealt by the player and minimum reward potential"
	)
	default boolean showDamageDealt()
	{
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "ToolSeed",
			name = "Show chance of getting Tool Seed",
			description = "Shows the % chance to obtain a tool seed with your participation. Assuming 3 down kills"
	)
	default boolean showToolSeedCalculations()
	{
		return true;
	}
}
