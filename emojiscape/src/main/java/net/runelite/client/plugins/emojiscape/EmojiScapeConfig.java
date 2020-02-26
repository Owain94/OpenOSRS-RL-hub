/*
 * Copyright (c) 2020, Hannah Ryan <HannahRyanster@gmail.com>
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

package net.runelite.client.plugins.emojiscape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("EmojiScape")
public interface EmojiScapeConfig extends Config
{
	@ConfigItem(
		keyName = "swapIconMode",
		name = "Icon Mode",
		description = "Choose whether the skill icon should replace the text, or appear after it",
		position = 1
	)
	default IconMode swapIconMode()
	{
		return IconMode.APPEND;
	}

	@ConfigItem(
		keyName = "skillIcons",
		name = "Skill Icons",
		description = "Enable icons for skills, with long and/or short triggers",
		position = 2
	)
	default TriggerMode skillIcons()
	{
		return TriggerMode.BOTH;
	}

	@ConfigItem(
		keyName = "prayerIcons",
		name = "Prayer Icons",
		description = "Enables icons for high level prayers (from Retribution to Augury)",
		position = 3
	)
	default boolean prayerIcons()
	{
		return true;
	}

	@ConfigItem(
		keyName = "miscIcons",
		name = "Misc Icons",
		description = "A mix of common map icons (bank, altar, shortcut) and other misc icons (listed on support repo)",
		position = 4
	)
	default TriggerMode miscIcons()
	{
		return TriggerMode.BOTH;
	}
}