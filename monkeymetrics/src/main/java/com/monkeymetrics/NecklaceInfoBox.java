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

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class NecklaceInfoBox extends InfoBox
{
	// Game ticks until Bonecrusher necklace is activated after equipping.
	private static final int NECKLACE_TIMER = 15;

	private final Client client;
	private final int endTick;

	public NecklaceInfoBox(BufferedImage image, Plugin plugin, Client client)
	{
		super(image, plugin);
		setTooltip("Time remaining until Bonecrusher necklace is activated");
		this.client = client;
		this.endTick = client.getTickCount() + NECKLACE_TIMER + 1;
	}

	@Override
	public String getText()
	{
		return String.valueOf(getTicksLeft());
	}

	public boolean isDone()
	{
		return getTicksLeft() <= 0;
	}

	private int getTicksLeft()
	{
		return endTick - client.getTickCount();
	}

	@Override
	public Color getTextColor()
	{
		return null;
	}
}
