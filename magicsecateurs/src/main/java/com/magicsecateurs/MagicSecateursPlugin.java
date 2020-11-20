package com.magicsecateurs;

import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Magic Secateurs",
	description = "Highlight when magic secateurs aren't equipped on a farming run",
	tags = {"overlay", "skilling"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)

public class MagicSecateursPlugin extends Plugin
{
	@Inject
	private MagicSecateursOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}
}