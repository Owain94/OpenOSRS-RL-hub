package com.decimalprices;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Decimal Prices",
	description = "Allows the use of decimals when entering a custom price",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class DecimalPrices extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private KeyManager keyManager;

	@Inject
	private DecimalPricesKeyListener inputListener;

	@Override
	protected void startUp() throws Exception
	{
		keyManager.registerKeyListener(inputListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		keyManager.unregisterKeyListener(inputListener);
	}

}
