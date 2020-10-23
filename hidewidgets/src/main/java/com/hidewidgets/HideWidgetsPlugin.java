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

package com.hidewidgets;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.CanvasSizeChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "Hide Widgets",
        description = "Hides all widgets (Resizable only)",
        enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class HideWidgetsPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private HideWidgetsConfig config;

    @Inject
    private KeyManager keyManager;

    @Inject
    private HideWidgetsKeyboardListener hideWidgetsKeyboardListener;

    private Boolean hide = false;

    @Provides
    private HideWidgetsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HideWidgetsConfig.class);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired scriptPostFired)
    {
        // 903 seems to get called when something opens the inventory like when banking or when opening stores
        if (scriptPostFired.getScriptId() == ScriptID.TOPLEVEL_REDRAW || scriptPostFired.getScriptId() == 903)
        {
            if (hide)
                hideWidgets(true);
        }
    }

    @Subscribe
    public void onCanvasSizeChanged(CanvasSizeChanged canvasSizeChanged)
    {
        // hiding in fixed mode does not actually hide stuff and might break stuff so let's not do that
        if (!client.isResized())
            hideWidgets(false);
    }

    @Override
    protected void startUp() throws Exception
    {
        keyManager.registerKeyListener(hideWidgetsKeyboardListener);
        hide = false;
        hideWidgets(false);
    }

    @Override
    protected void shutDown() throws Exception
    {
        keyManager.unregisterKeyListener(hideWidgetsKeyboardListener);
        hideWidgets(false);
    }

    public void toggle()
    {
        log.debug("toggled hiding widgets");
        hide = !hide;
        hideWidgets(hide);
    }

    protected void hideWidgets(boolean hide)
    {
        // hiding in fixed mode does not actually hide stuff and might break stuff so let's not do that
        if (hide && !client.isResized())
        {
            hideWidgets(false);
        }
        else
        {
            Widget[] root = client.getWidgetRoots();
            for (Widget w : root)
            {
                if (w != null)
                {
                    // hiding the widget with content type 1337 prevents the game from rendering so let's not do that
                    if (w.getContentType() != 1337)
                        w.setHidden(hide);
                }
            }

            // hiding this completely breaks scrolling to zoom
            Widget zoom = client.getWidget(164, 2);
            if (zoom != null)
                zoom.setHidden(false);
        }

    }
}
