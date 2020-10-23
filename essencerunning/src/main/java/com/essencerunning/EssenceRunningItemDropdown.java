package com.essencerunning;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class EssenceRunningItemDropdown {

    @Getter
    @RequiredArgsConstructor
    public enum BindingNecklace {
        WEAR("Wear"),
        CHECK("Check"),
        USE("Use"),
        DESTROY("Destroy");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum CraftingCape {
        WEAR("Wear"),
        TELEPORT("Teleport"),
        USE("Use"),
        DROP("Drop");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum EarthTalisman {
        USE("Use"),
        LOCATE("Locate"),
        DROP("Drop");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum EssencePouch {
        FILL("Fill"),
        EMPTY("Empty"),
        CHECK("Check"),
        USE("Use"),
        DROP("Drop");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum PureEssence {
        USE("Use"),
        DROP("Drop");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum RingOfDueling {
        WEAR("Wear"),
        USE("Use"),
        RUB("Rub"),
        DROP("Drop");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum StaminaPotion {
        DRINK("Drink"),
        USE("Use"),
        EMPTY("Empty"),
        DROP("Drop");

        private final String option;
    }

    @Getter
    @RequiredArgsConstructor
    public enum HighlightEquipBindingNecklace {
        OFF("Off"),
        EQUIP("Equip");

        private final String option;

        @Override
        public String toString() {
            return this.option;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum HighlightTradeBindingNecklace {
        OFF("Off"),
        TWENTY_FIVE("25"),
        TWENTY_SIX("26 (Pure)");

        private final String option;

        @Override
        public String toString() {
            return this.option;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum ClanChatOverlayHeight {
        ZERO(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8);

        private final int option;

        @Override
        public String toString() {
            return Integer.toString(this.option);
        }
    }
}
