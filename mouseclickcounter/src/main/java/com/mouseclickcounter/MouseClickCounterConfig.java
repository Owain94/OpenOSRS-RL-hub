/*
 * Copyright (c) 2020, Robert Espinoza
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
package com.mouseclickcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mouseclickcounter")
public interface MouseClickCounterConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "hide",
		name = "Hide Overlay",
		description = "Toggle the display of any click count"
	)
	default boolean hide() { return false; }

	@ConfigItem(
		position = 2,
		keyName = "showTotalClick",
		name = "Show total click totals",
		description = "Toggle the display of the total mouse clicks"
	)
	default boolean showTotalClick() { return true; }

	@ConfigItem(
		position = 3,
		keyName = "showLeftClick",
		name = "Show left click totals",
		description = "Toggle the display of the left mouse clicks"
	)
	default boolean showLeftClick() { return false; }

	@ConfigItem(
		position = 4,
		keyName = "showRightClick",
		name = "Show right click totals",
		description = "Toggle the display of the right mouse clicks"
	)
	default boolean showRightClick() { return false; }

	@ConfigItem(
		position = 5,
		keyName = "showMiddleClick",
		name = "Show middle click totals",
		description = "Toggle the display of the middle mouse clicks"
	)
	default boolean showMiddleClick() { return false; }

}

