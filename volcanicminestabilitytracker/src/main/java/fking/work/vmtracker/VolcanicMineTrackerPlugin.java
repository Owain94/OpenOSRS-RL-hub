package fking.work.vmtracker;

import net.runelite.api.Client;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Volcanic Mine Stability Tracker",
	description = "Tracks the mine stability changes and displays them on the hud.",
	enabledByDefault = false,
	type = PluginType.SKILLING
)
public class VolcanicMineTrackerPlugin extends Plugin {

    private static final int VARBIT_STABILITY = 5938;
    private static final int VARBIT_GAME_STATE = 5941;
    private static final int PROC_VOLCANIC_MINE_SET_OTHERINFO = 2022;

    private static final int HUD_COMPONENT = 611;
    private static final int HUD_STABILITY_COMPONENT = 13;

    private static final int GAME_STATE_IN_LOBBY = 1;
    private static final int GAME_STATE_IN_GAME = 2;

    private static final int STARTING_STABILITY = 50;

    private static final int STABILITY_CHANGE_HISTORY_SIZE = 2;
    private final int[] stabilityChangeHistory = new int[STABILITY_CHANGE_HISTORY_SIZE];

    @Inject
    private Client client;

    private int changeHistorySize = 0;
    private int lastMineStability;

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {

        if (event.getScriptId() != PROC_VOLCANIC_MINE_SET_OTHERINFO) {
            return;
        }
        int gameState = client.getVarbitValue(VARBIT_GAME_STATE);

        if (gameState == GAME_STATE_IN_LOBBY) {
            lastMineStability = STARTING_STABILITY;
            changeHistorySize = 0;
        }
        int stability = client.getVarbitValue(VARBIT_STABILITY);
        int delta = stability - lastMineStability;
        lastMineStability = stability;

        if (delta != 0) {
            pushStabilityChangeHistory(delta);
        }
        updateHudText();
    }

    private void updateHudText() {

        if (changeHistorySize <= 0) {
            return;
        }
        Widget widget = client.getWidget(HUD_COMPONENT, HUD_STABILITY_COMPONENT);

        if (widget != null) {
            String text = widget.getText();

            text += " (" + buildStabilityHistoryText() + ")";
            widget.setText(text);
        }
    }

    private String buildStabilityHistoryText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < changeHistorySize; i++) {
            int delta = stabilityChangeHistory[i];

            if (delta >= 0) {
                builder.append("<col=00ff00>").append(delta).append("</col>");
            } else {
                builder.append("<col=ff0000>").append(delta).append("</col>");
            }

            if (i != changeHistorySize - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private void pushStabilityChangeHistory(int delta) {
        System.arraycopy(stabilityChangeHistory, 0, stabilityChangeHistory, 1, stabilityChangeHistory.length - 1);
        stabilityChangeHistory[0] = delta;

        if (changeHistorySize < STABILITY_CHANGE_HISTORY_SIZE) {
            changeHistorySize++;
        }
    }
}
