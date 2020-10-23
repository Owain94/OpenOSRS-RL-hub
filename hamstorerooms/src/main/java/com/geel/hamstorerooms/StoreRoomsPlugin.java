package com.geel.hamstorerooms;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "H.A.M Store Rooms",
        description = "Highlights chests which you have keys for in the H.A.M Store Rooms",
        tags = {"thieving", "ironman", "ham", "h.a.m", "store", "rooms", "storerooms"},
	enabledByDefault = false,
	type = PluginType.MINIGAME
)
@Slf4j
public class StoreRoomsPlugin extends Plugin {
    private static final int HAM_STOREROOM_REGION_ID = 10321;
    private static final int OPEN_CHEST_ID = ObjectID.SMALL_CHEST_15725;

    @Getter
    private final Map<TileObject, Chest> chests = new HashMap<>();

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private StoreRoomsOverlay overlay;

    @Inject
    private Client client;

    private Chest lastChestDespawned = null;
    private LocalPoint lastChestDespawnPoint = null;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        resetParams();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        switch (event.getGameState()) {
            case HOPPING:
            case LOGIN_SCREEN:
            case LOADING:
                resetParams();
                break;
            case LOGGED_IN:
                if (!isInStoreRooms()) {
                    resetParams();
                }
                break;
        }
    }


    private void resetParams() {
        chests.clear();
    }

    public boolean isInStoreRooms() {
        Player local = client.getLocalPlayer();
        if (local == null) {
            return false;
        }

        WorldPoint location = local.getWorldLocation();
        int region = location.getRegionID();
        return location.getPlane() == 0 && region == HAM_STOREROOM_REGION_ID;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        TileObject newObject = event.getGameObject();

        //If the new object is an open chest, then it SHOULD be replacing a chest we just saw despawn
        if (newObject.getId() == OPEN_CHEST_ID && lastChestDespawned != null) {
            //There's no LocalPoint.equals() function for some silly reason.
            if (lastChestDespawnPoint.distanceTo(newObject.getLocalLocation()) == 0) {
                chests.put(newObject, lastChestDespawned);

                lastChestDespawned = null;
                lastChestDespawnPoint = null;
            }
        }

        Chest foundChest = Chest.fromObjectID(newObject.getId());
        if (foundChest == null)
            return;

        chests.put(newObject, foundChest);
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        TileObject oldObject = event.getGameObject();
        Chest existingChest = chests.get(oldObject);

        //If there was no Chest associated with this object, we don't care about it.
        if (existingChest == null)
            return;

        chests.remove(oldObject);

        //If an open chest is despawning, we don't care.
        if (oldObject.getId() == OPEN_CHEST_ID)
            return;

        //Store the despawned Chest so we know what to attach to the newly-spawned open chest
        lastChestDespawned = existingChest;

        //Store the despawn point to try to avoid concurrent chest fuckery
        lastChestDespawnPoint = oldObject.getLocalLocation();
    }
}
