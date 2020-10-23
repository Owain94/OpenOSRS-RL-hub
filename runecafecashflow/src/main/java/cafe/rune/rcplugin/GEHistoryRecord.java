package cafe.rune.rcplugin;

import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.widgets.Widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GEHistoryRecord {
    private final static int OFFSET_WHOLE_ROW = 0,
            OFFSET_BUYSELL_ICON = 1,
            OFFSET_BUYSELL_TEXT = 2,
            OFFSET_ITEM_NAME_TEXT = 3,
            OFFSET_ITEM_ICON = 4,
            OFFSET_COINS_TEXT = 5;

    private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("\\s*(<col=[a-z0-9]+>)?(?<itemName>[^<]+)(</col>)?(<br>)?(x (?<qty>[\\d,]+))?");
    private static final Pattern COINS_PATTERN = Pattern.compile("(?<coins>[\\d,]+) coins(<br><col=[a-z0-9]+>= (?<coinsEach>[\\d,]+) each</col>)?");
    private final BoughtOrSold action;
    private final String itemName;
    private final int coins;
    private final int coinsEach;
    private final int itemId;
    private final int qty;

    public GEHistoryRecord(BoughtOrSold action, String itemName, int coins, int coinsEach, int itemId, int qty) {
        this.action = action;
        this.itemName = itemName;
        this.coins = coins;
        this.coinsEach = coinsEach;
        this.itemId = itemId;
        this.qty = qty;
    }

    public GEHistoryRecord(GrandExchangeOffer o) {
        this.action = BoughtOrSold.fromState(o.getState());
        this.coins = o.getSpent();
        this.coinsEach = o.getSpent() / o.getQuantitySold();
        this.qty = o.getQuantitySold();
        this.itemId = o.getItemId();
        this.itemName = "";
    }

    public GEHistoryRecord(Widget[] src, int offset) {
        this(
                src[offset + OFFSET_BUYSELL_TEXT].getText().equals("Sold:")
                        ? BoughtOrSold.SOLD : BoughtOrSold.BOUGHT,
                getCleanItemName(src[offset + OFFSET_ITEM_NAME_TEXT].getText()),
                getCleanCoinsCount(src[offset + OFFSET_COINS_TEXT].getText()),
                getCleanCoinsCountEach(src[offset + OFFSET_COINS_TEXT].getText()),
                src[offset + OFFSET_ITEM_ICON].getItemId(),
                src[offset + OFFSET_ITEM_ICON].getItemQuantity()
        );
    }

    private static String getCleanItemName(String itemNameText) {
        Matcher matcher = ITEM_NAME_PATTERN.matcher(itemNameText);
        if(matcher.matches()) {
            return matcher.group("itemName");
        } else {
            throw new IllegalArgumentException("I am a dummy and cannot parse this: " + itemNameText);
        }
    }

    private static int getCleanCoinsCount(String coinsText) {
        Matcher matcher = COINS_PATTERN.matcher(coinsText);
        if(matcher.matches()) {
            return Integer.parseInt(matcher.group("coins").replace(",", ""));
        } else {
            throw new IllegalArgumentException("I am a dummy and cannot parse this: " + coinsText);
        }
    }

    private static int getCleanCoinsCountEach(String coinsText) {
        Matcher matcher = COINS_PATTERN.matcher(coinsText);
        if(matcher.matches()) {
            if(matcher.group("coinsEach") != null) {
                return Integer.parseInt(matcher.group("coinsEach").replace(",", ""));
            } else {
                return Integer.parseInt(matcher.group("coins").replace(",", ""));
            }
        } else {
            throw new IllegalArgumentException("I am a dummy and cannot parse this: " + coinsText);
        }
    }

    public BoughtOrSold getAction() {
        return action;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCoins() {
        return coins;
    }

    public int getCoinsEach() {
        return coinsEach;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQty() {
        return qty;
    }

    @Override
    public String toString() {
        return "RawGEHistoryRecord{" +
                "action=" + action +
                ", itemName='" + itemName + '\'' +
                ", coins=" + coins +
                ", coinsEach=" + coinsEach +
                ", itemId=" + itemId +
                ", qty=" + qty +
                '}';
    }
}