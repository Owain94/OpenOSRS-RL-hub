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

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Mouse Click Counter",
	description = "Tracks all types of mouse clicks in the active session.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class MouseClickCounterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MouseClickCounterConfig config;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MouseClickCounterOverlay overlay;

	private MouseClickCounterListener mouseListener;

	@Override
	protected void startUp() throws Exception
	{
		mouseListener = new MouseClickCounterListener(client);
		mouseManager.registerMouseListener(mouseListener);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		mouseManager.unregisterMouseListener(mouseListener);
		mouseListener = null;
		overlayManager.remove(overlay);
	}

	@Provides
	MouseClickCounterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MouseClickCounterConfig.class);
	}

	public int getLeftClickCounter() { return mouseListener.getLeftClickCounter(); }

	public int getRightClickCounter() { return mouseListener.getRightClickCounter(); }

	public int getMiddleClickCounter() { return mouseListener.getMiddleClickCounter(); }

	public int getTotalClickCounter() { return mouseListener.getTotalClickCounter(); }


}
