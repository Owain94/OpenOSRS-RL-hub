package com.essencerunning;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum EssenceRunningItem {

    BINDING_NECKLACE("binding necklace"),

    EARTH_TALISMAN("earth talisman", 2),

    ENERGY_POTION_1("energy potion(1)"),
    ENERGY_POTION_2("energy potion(2)"),
    ENERGY_POTION_3("energy potion(3)"),
    ENERGY_POTION_4("energy potion(4)"),

    RING_OF_DUELING_1("ring of dueling(1)"),
    RING_OF_DUELING_2("ring of dueling(2)"),
    RING_OF_DUELING_3("ring of dueling(3)"),
    RING_OF_DUELING_4("ring of dueling(4)"),
    RING_OF_DUELING_5("ring of dueling(5)"),
    RING_OF_DUELING_6("ring of dueling(6)"),
    RING_OF_DUELING_7("ring of dueling(7)"),
    RING_OF_DUELING_8("ring of dueling(8)"),

    STAMINA_POTION_1("stamina potion(1)"),
    STAMINA_POTION_2("stamina potion(2)"),
    STAMINA_POTION_3("stamina potion(3)"),
    STAMINA_POTION_4("stamina potion(4)"),
    ;

    private final String name;
    private final int withdrawQuantity;

    private static final Map<String, EssenceRunningItem> map = new HashMap<>(values().length);

    static {
        for (EssenceRunningItem item : values()) {
            map.put(item.getName(), item);
        }
    }

    EssenceRunningItem(final String name) {
        this(name, 1);
    }

    public static EssenceRunningItem of(final String name) {
        return map.get(name);
    }
}
