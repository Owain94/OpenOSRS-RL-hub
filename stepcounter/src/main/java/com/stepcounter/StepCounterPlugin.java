package com.stepcounter;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;


@Extension
@PluginDescriptor(
	name = "Step Counter",
	description = "Counts the steps you take and if you meet your goal",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class StepCounterPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private StepCounterConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private StepCounterOverlay overlay;

    private WorldPoint previousLocation;

    private int currentSteps;
    private int goal;
    private boolean firstLoad;
    private boolean congratulations;

    @Override
    protected void startUp() throws Exception {
        // Initialize variables
        previousLocation = new WorldPoint(0, 0, 0);
        currentSteps = 0;
        goal = config.StepGoal();

        overlayManager.add(overlay);

        firstLoad = true;
        congratulations = false;

    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        firstLoad = true;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        // When we load in, set everything up
        if (firstLoad) {
            firstLoad = false;
            congratulations = false;
            goal = config.StepGoal();
            currentSteps = 0;
        }
    }

    // I think you can tell what this does
    private boolean areTheSameLocation(WorldPoint previous, WorldPoint current) {
        return (previous.getX() == current.getX()) && (previous.getY() == current.getY());
    }

    // Calculate how far you travelled between the locations
    private int distanceDelta(WorldPoint previousLocation, WorldPoint currentLocation) {
        // Calculate the actual travelled distance and store it
        int potentialDistance = Math.abs(previousLocation.getX() - currentLocation.getX()) + Math.abs(previousLocation.getY() - currentLocation.getY());

        // If it's an unreasonable distance. I don't know what an unreasonable distance is, but this should
        // account for teleports and entering the client, then we say you didn't take any steps.
        if (potentialDistance > 100) {
            return 0;
        }

        return potentialDistance;
    }

    @Subscribe
    public void onClientTick(ClientTick clientTick) {

        // If you've changed the configuration
        if (goal != config.StepGoal()) {
            goal = config.StepGoal();

            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
                    "Step goal updated to " + goal + ".", null);
        }

        WorldPoint currentLocation;
        try {
            currentLocation = client.getLocalPlayer().getWorldLocation();
        }
        catch (NullPointerException e){
            return;
        }

        // Don't do anything if you haven't moved.
        if (!areTheSameLocation(previousLocation, currentLocation)) {

            int distanceTravelledThisTick = distanceDelta(previousLocation, currentLocation);

            // Save your new location for comparison next time
            previousLocation = currentLocation;

            // Add how far you walked to your steps
            currentSteps += distanceTravelledThisTick;

            // Congratulate if you deserve it :)
            if ((currentSteps >= goal) && !congratulations) {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You hit your step goal! Congratulations!", null);
                congratulations = true;
            }
        }
    }

    @Provides
    StepCounterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(StepCounterConfig.class);
    }


    public int getSteps() {
        return currentSteps;
    }

    public int getGoal() {
        return goal;
    }
}
