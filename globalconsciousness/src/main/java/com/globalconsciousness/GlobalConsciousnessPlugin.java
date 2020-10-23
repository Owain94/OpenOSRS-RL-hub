package com.globalconsciousness;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
		name = "Global Consciousness",
		description = "Channel your inner Rendi to unlock the power of global consciousness and... just get the drop lul.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)

public class GlobalConsciousnessPlugin extends Plugin {

	static final String CONFIG_GROUP_KEY = "globalconsciousness";

	public String itemName = "";
	public int iconSpeed;
	public int iconScale;
	public int iconOpacity;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GlobalConsciousnessConfig globalConsciousnessConfig;

	@Provides
	GlobalConsciousnessConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(GlobalConsciousnessConfig.class);
	};

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		this.itemName = globalConsciousnessConfig.itemName();
		this.iconSpeed = globalConsciousnessConfig.iconSpeed();
		this.iconScale = globalConsciousnessConfig.iconScale();
		this.iconOpacity = globalConsciousnessConfig.iconOpacity();
	}

	@Inject
	private GlobalConsciousnessOverlay globalConsciousnessOverlay;

	@Override
	public void startUp() {
		overlayManager.add(globalConsciousnessOverlay);
		globalConsciousnessOverlay.x = 0;
		globalConsciousnessOverlay.y = 0;
		this.itemName = globalConsciousnessConfig.itemName();
		this.iconSpeed = globalConsciousnessConfig.iconSpeed();
		this.iconScale = globalConsciousnessConfig.iconScale();
		this.iconOpacity = globalConsciousnessConfig.iconOpacity();
	}

	@Override
	public void shutDown() {
		overlayManager.remove(globalConsciousnessOverlay);
	}
}
