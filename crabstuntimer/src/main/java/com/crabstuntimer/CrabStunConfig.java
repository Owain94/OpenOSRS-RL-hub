/*
 * Copyright (c) 2018, Seth <Sethtroll3@gmail.com>
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
package com.crabstuntimer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

import static com.crabstuntimer.TimerTextStyle.TICKS;

@ConfigGroup("crabstun")
public interface CrabStunConfig extends Config {
	@ConfigItem(
			keyName = "showTimer",
			name = "Show crab stun timer",
			description = "Configures whether or not the timer is displayed",
			position = 1
	)
	default boolean showTimer() {
		return true;
	}

	@ConfigItem(
			keyName = "showText",
			name = "Show crab stun timer text",
			description = "Configures whether or not the text is displayed",
			position = 2
	)
	default boolean showText() {
		return true;
	}

	@ConfigItem(
			keyName = "timerDiameter",
			name = "Size of timer",
			description = "Configures the diameter of the timer",
			position = 3
	)
	default int timerDiameter() {
		return 25;
	}

	@ConfigItem(
			keyName = "textType",
			name = "Choose text type",
			description = "Configures whether ticks or seconds are displayed",
			position = 4
	)
	default TimerTextStyle textType() {
		return TICKS;
	}

	@ConfigItem(
			keyName = "normalTimerColor",
			name = "Timer color",
			description = "Configures the color of the timer that is displayed",
			position = 5
	)
	default Color normalTimerColor() {
		return Color.YELLOW;
	}

	@ConfigItem(
			keyName = "randomTimerColor",
			name = "Random interval color",
			description = "Configures the color of the timer during the random interval",
			position = 6
	)
	default Color randomTimerColor() {
		return Color.CYAN;
	}

	@ConfigItem(
			keyName = "timerWarningColor",
			name = "Timer warning color",
			description = "Configures the color of the timer when the crab is almost unstunned",
			position = 7
	)
	default Color timerWarningColor() {
		return Color.RED;
	}

	@ConfigItem(
			keyName = "timerBorderColor",
			name = "Timer border color",
			description = "Configures the color of the timer that is displayed",
			position = 8
	)
	default Color timerBorderColor() {
		return Color.ORANGE;
	}

	@ConfigItem(
			keyName = "randomBorderColor",
			name = "Random border color",
			description = "Configures the color of the timer that is displayed",
			position = 9
	)
	default Color randomBorderColor() {
		return Color.LIGHT_GRAY;
	}


}

