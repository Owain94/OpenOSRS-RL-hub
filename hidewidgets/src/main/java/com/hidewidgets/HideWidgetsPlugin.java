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
