/*
 * Copyright (c) 2020, Truth Forger <https://github.com/Blackberry0Pie>
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
package bbp.trimmer;

import bbp.trimmer.configs.AbyssalWhipMode;
import bbp.trimmer.configs.DarkBowMode;
import bbp.trimmer.configs.GracefulMode;
import bbp.trimmer.configs.GraniteMaulMode;
import bbp.trimmer.configs.RuneArmourMode;
import bbp.trimmer.configs.RuneScimitarMode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("armourtrimmer")
public interface ArmourTrimmerConfig extends Config
{
	@ConfigItem(
		position = -3,
		keyName = "sendMessage",
		name = "Welcome message",
		description = "Send the user a message when they login"
	)
	default boolean sendMessage()
	{
		return true;
	}

	@ConfigItem(
		position = -2,
		keyName = "trimGold",
		name = "Gold Trim",
		description = "Trims items in gold trim instead of normal trim"
	)
	default boolean trimGold()
	{
		return true;
	}

	@ConfigItem(
		keyName = "gracefulMode",
		name = "Graceful Mode",
		description = "Which graceful type is used"
	)
	default GracefulMode gracefulMode()
	{
		return GracefulMode.NONE;
	}

	@ConfigItem(
		keyName = "darkBowMode",
		name = "Dark Bow Mode",
		description = "Which dark bow type is used"
	)
	default DarkBowMode darkBowMode()
	{
		return DarkBowMode.NONE;
	}

	@ConfigItem(
		keyName = "abyssalWhipMode",
		name = "Abyssal Whip Mode",
		description = "Which abyssal whip type is used"
	)
	default AbyssalWhipMode abyssalWhipMode()
	{
		return AbyssalWhipMode.NONE;
	}

	@ConfigItem(
		keyName = "runeArmourMode",
		name = "Rune Armour Mode",
		description = "Which rune armour type is used"
	)
	default RuneArmourMode runeArmourMode()
	{
		return RuneArmourMode.NONE;
	}

	@ConfigItem(
			keyName = "runeScimitarMode",
			name = "Rune Scimitar Mode",
			description = "Which rune scimitar type is used"
	)
	default RuneScimitarMode runeScimitarMode()
	{
		return RuneScimitarMode.NONE;
	}

	@ConfigItem(
		keyName = "graniteMaulMode",
		name = "Granite Maul Mode",
		description = "Which granite maul type is used"
	)
	default GraniteMaulMode graniteMaulMode()
	{
		return GraniteMaulMode.NONE;
	}
}
