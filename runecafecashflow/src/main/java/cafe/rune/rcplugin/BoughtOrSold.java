package cafe.rune.rcplugin;

import net.runelite.api.GrandExchangeOfferState;

public enum BoughtOrSold {
    BOUGHT,
    SOLD;

    public static BoughtOrSold fromState(GrandExchangeOfferState s) {
        switch(s) {
            case BOUGHT:
            case BUYING:
            case CANCELLED_BUY:
                return BOUGHT;
            case SOLD:
            case SELLING:
            case CANCELLED_SELL:
                return SOLD;
            default:
                throw new IllegalArgumentException("Could not determine if " + s + " is a bought or sold action.");
        }
    }
}