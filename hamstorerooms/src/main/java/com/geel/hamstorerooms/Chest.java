package com.geel.hamstorerooms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
enum Chest {
    STEEL(ObjectID.SMALL_CHEST, ItemID.STEEL_KEY),
    BRONZE(ObjectID.SMALL_CHEST_15723, ItemID.BRONZE_KEY_8867),
    SILVER(ObjectID.SMALL_CHEST_15724, ItemID.SILVER_KEY),
    IRON(ObjectID.SMALL_CHEST_15726, ItemID.IRON_KEY_8869);

    @Getter
    private final int Object;

    @Getter
    private final int Key;

    private final static Map<Integer, Chest> objToChest = new HashMap<Integer, Chest>() {
        {
            put(Chest.STEEL.Object, Chest.STEEL);
            put(Chest.BRONZE.Object, Chest.BRONZE);
            put(Chest.SILVER.Object, Chest.SILVER);
            put(Chest.IRON.Object, Chest.IRON);
        }
    };

    static Chest fromObjectID(int objectID) {
        return objToChest.get(objectID);
    }
}
