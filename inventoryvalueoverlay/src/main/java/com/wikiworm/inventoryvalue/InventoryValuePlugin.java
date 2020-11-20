/*
 *  BSD 2-Clause License
 *
 *  Copyright (c) 2020, wikiworm (Brandon Ripley)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wikiworm.inventoryvalue;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Arrays;
import java.util.stream.LongStream;
import org.pf4j.Extension;

/**
 * The InventoryValuePlugin class is used as the injection point for calculating a user logged into RuneLite client's
 * inventory value.
 */
@Extension
@PluginDescriptor(
        name = "Inventory Value Overlay",
	description = "Displays value of items in inventory as overlay.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class InventoryValuePlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private InventoryValueConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private InventoryValueOverlay overlay;

    @Override
    protected void startUp() throws Exception
    {
        // Add the inventory overlay
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        // Remove the inventory overlay
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {

    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if(event.getContainerId() == InventoryID.INVENTORY.getId()) {
            long inventoryValue = 0;
            ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
            if(container != null) {
                Item[] items = container.getItems();
                inventoryValue = Arrays.stream(items).parallel().flatMapToLong(item -> {
                    return LongStream.of(calculateItemValue(item));
                }).sum();
                // Update the panel
                overlay.updateInventoryValue(inventoryValue);
            }
        }
    }

    public long calculateItemValue(Item item) {
        int itemId = item.getId();
        ItemDefinition itemComp = itemManager.getItemDefinition(itemId);
        String itemName = itemComp.getName();
        int itemValue;
        // if ignore coins is set, calculate item value as 0
        if (itemId == ItemID.COINS_995 && config.ignoreCoins())
            itemValue = 0;
        else // multiply quantity by HA value or GE value
            itemValue = item.getQuantity() * (config.useHighAlchemyValue() ? itemComp.getHaPrice() : itemManager.getItemPrice(item.getId()));

        return itemValue;
    }
    @Provides
    InventoryValueConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(InventoryValueConfig.class);
    }
}
