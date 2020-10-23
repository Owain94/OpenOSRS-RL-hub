/*
 * Copyright (c) 2020, PresNL
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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

package com.pmcolors;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("pmcolors")
public interface PMColorsConfig extends Config
{
	@ConfigItem(
			keyName = "highlightColor",
			name = "Default highlight color",
			description = "Configures the default color to highlight players withs",
			position = 14
	)
	default Color defaultHighlightColor() { return Color.ORANGE; }
	@ConfigItem(
		keyName = "highlightUsername",
		name = "Highlight username default",
		description = "Configures if you want to highlight the username by default",
		position = 14
	)
	default boolean highlightUsernameDefault() { return true; }
	@ConfigItem(
			keyName = "highlightMessage",
			name = "Highlight message default",
			description = "Configures if you want to highlight the message by default",
			position = 14
	)
	default boolean highlightMessageDefault() { return true; }
	@ConfigItem(
			keyName = "highlightLoggedInOut",
			name = "Highlight Logged in/out default",
			description = "Configures if you want to highlight the logged in/out message by default",
			position = 14
	)
	default boolean highlightLoggedInOutDefault() { return true; }
}
