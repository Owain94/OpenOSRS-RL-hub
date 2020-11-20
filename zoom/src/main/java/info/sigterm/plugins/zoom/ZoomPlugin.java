package info.sigterm.plugins.zoom;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Zoom Extender",
	description = "Increases the inner zoom limit further",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class ZoomPlugin extends Plugin
{
	private static final int INNER_ZOOM_LIMIT = 1400;

	@Inject
	private Client client;

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (event.getEventName().equals("innerZoomLimit"))
		{
			int[] intStack = client.getIntStack();
			int intStackSize = client.getIntStackSize();
			intStack[intStackSize - 1] = INNER_ZOOM_LIMIT;
		}
	}
}
