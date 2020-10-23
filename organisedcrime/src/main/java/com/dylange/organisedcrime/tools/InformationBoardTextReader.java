package com.dylange.organisedcrime.tools;

import com.dylange.organisedcrime.models.GangInfo;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import javax.annotation.Nullable;

import static com.dylange.organisedcrime.tools.WidgetConstants.GROUP_ID_INFORMATION_BOARD;

public class InformationBoardTextReader {
    private static final int INDEX_START_INFO_BOARD_TEXT = 1;
    private static final int INDEX_END_INFO_BOARD_TEXT = 11;
    private static final String START_TIME_TEXT = "The meeting is expected to";

    @Nullable
    public static GangInfo getDisplayedGangInfo(Client client) {
        final StringBuilder locationStringBuilder = new StringBuilder();
        final StringBuilder timeStringBuilder = new StringBuilder();

        boolean parsedLocation = false;
        for (int i = INDEX_START_INFO_BOARD_TEXT; i < INDEX_END_INFO_BOARD_TEXT; i++) {
            Widget textItem = client.getWidget(GROUP_ID_INFORMATION_BOARD, i);
            String text = textItem.getText();
            if (text != null) {
                if (text.startsWith(START_TIME_TEXT)) parsedLocation = true;
                if (parsedLocation) {
                    timeStringBuilder.append(text);
                    timeStringBuilder.append(" ");
                } else {
                    locationStringBuilder.append(text);
                    locationStringBuilder.append(" ");
                }
            }
        }

        final String locationText = locationStringBuilder.toString().trim();
        final String timeText = timeStringBuilder.toString().trim();
        if (locationText.isEmpty() || timeText.isEmpty()) {
            return null;
        } else {
            return new GangInfo(
                    locationText,
                    timeText,
                    client.getWorld()
            );
        }
    }
}
