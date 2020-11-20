package com.profittracker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import net.runelite.api.events.*;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Profit Tracker",
	description = "Track profit while money making.",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class ProfitTrackerPlugin extends Plugin
{
    ProfitTrackerGoldDrops goldDropsObject;
    ProfitTrackerInventoryValue inventoryValueObject;

    // the profit will be calculated against this value
    private long prevInventoryValue;
    private long totalProfit;

    private long startTickMillis;

    private boolean skipTickForProfitCalculation;
    private boolean inventoryValueChanged;
    private boolean inProfitTrackSession;

    @Inject
    private Client client;

    @Inject
    private ProfitTrackerConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ProfitTrackerOverlay overlay;

    @Override
    protected void startUp() throws Exception
    {
        // Add the inventory overlay
        overlayManager.add(overlay);

        goldDropsObject = new ProfitTrackerGoldDrops(client, itemManager);

        inventoryValueObject = new ProfitTrackerInventoryValue(client, itemManager);

        initializeVariables();

        // start tracking only if plugin was re-started mid game
        if (client.getGameState() == GameState.LOGGED_IN)
        {
            startProfitTrackingSession();
        }

    }

    private void initializeVariables()
    {
        // value here doesn't matter, will be overwritten
        prevInventoryValue = -1;

        // profit begins at 0 of course
        totalProfit = 0;

        // this will be filled with actual information in startProfitTrackingSession
        startTickMillis = 0;

        // skip profit calculation for first tick, to initialize first inventory value
        skipTickForProfitCalculation = true;

        inventoryValueChanged = false;

        inProfitTrackSession = false;

    }

    private void startProfitTrackingSession()
    {
        /*
        Start tracking profit from now on
         */

        initializeVariables();

        // initialize timer
        startTickMillis = System.currentTimeMillis();

        overlay.updateStartTimeMillies(startTickMillis);

        overlay.startSession();

        inProfitTrackSession = true;
    }

    @Override
    protected void shutDown() throws Exception
    {
        // Remove the inventory overlay
        overlayManager.remove(overlay);

    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        /*
        Main plugin logic here

        1. If inventory changed,
            - calculate profit (inventory value difference)
            - generate gold drop (nice animation for showing gold earn or loss)

        2. Calculate profit rate and update in overlay

        */

        long tickProfit;

        if (!inProfitTrackSession)
        {
            return;
        }

        if (inventoryValueChanged)
        {
            tickProfit = calculateTickProfit();

            // accumulate profit
            totalProfit += tickProfit;

            overlay.updateProfitValue(totalProfit);

            // generate gold drop
            if (config.goldDrops() && tickProfit != 0)
            {
                goldDropsObject.requestGoldDrop(Math.toIntExact(tickProfit));
            }

            inventoryValueChanged = false;
        }

    }

    private long calculateTickProfit()
    {
        /*
        Calculate and return the profit for this tick
        if skipTickForProfitCalculation is set, meaning this tick was bank / deposit
        so return 0

         */
        long newInventoryValue;
        long newProfit;

        // calculate current inventory value
        newInventoryValue = inventoryValueObject.calculateInventoryAndEquipmentValue();

        if (!skipTickForProfitCalculation)
        {
            // calculate new profit
            newProfit = newInventoryValue - prevInventoryValue;

        }
        else
        {
            /* first time calculation / banking / equipping */

            skipTickForProfitCalculation = false;

            // no profit this tick
            newProfit = 0;
        }

        // update prevInventoryValue for future calculations anyway!
        prevInventoryValue = newInventoryValue;

        return newProfit;
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        /*
        this event tells us when inventory has changed
        and when banking/equipment event occured this tick
         */

        int containerId = event.getContainerId();

        if( containerId == InventoryID.INVENTORY.getId() ||
            containerId == InventoryID.EQUIPMENT.getId()) {
            // inventory has changed - need calculate profit in onGameTick
            inventoryValueChanged = true;

        }

        // in these events, inventory WILL be changed but we DON'T want to calculate profit!
        if(     containerId == InventoryID.BANK.getId()) {
            // this is a bank interaction.
            // Don't take this into account
            skipTickForProfitCalculation = true;

        }

    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        /* for ignoring deposit in deposit box */
        if (event.getIdentifier() == ObjectID.BANK_DEPOSIT_BOX) {
            // we've interacted with a deposit box. Don't take this tick into account for profit calculation
            skipTickForProfitCalculation = true;
        }


    }

    @Provides
    ProfitTrackerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ProfitTrackerConfig.class);
    }


    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired)
    {
        goldDropsObject.onScriptPreFired(scriptPreFired);
    }
}
