package com.profittracker;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

import java.text.DecimalFormat;
/**
 * The ProfitTrackerOverlay class is used to display profit values for the user
 */
public class ProfitTrackerOverlay extends Overlay {
    private long profitValue;
    private long startTimeMillies;
    private boolean inProfitTrackSession;

    private final ProfitTrackerConfig ptConfig;
    private final PanelComponent panelComponent = new PanelComponent();

    public static String FormatIntegerWithCommas(long value) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(value);
    }
    @Inject
    private ProfitTrackerOverlay(ProfitTrackerConfig config)
    {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        profitValue = 0L;
        ptConfig = config;
        startTimeMillies = 0;
        inProfitTrackSession = false;
    }

    /**
     * Render the item value overlay.
     * @param graphics the 2D graphics
     * @return the value of {@link PanelComponent#render(Graphics2D)} from this panel implementation.
     */
    @Override
    public Dimension render(Graphics2D graphics) {
        String titleText = "Profit Tracker:";
        long secondsElapsed;
        long profitRateValue;

        if (startTimeMillies > 0)
        {
            secondsElapsed = (System.currentTimeMillis() - startTimeMillies) / 1000;
        }
        else
        {
            // there was never any session
            secondsElapsed = 0;
        }

        profitRateValue = calculateProfitHourly(secondsElapsed, profitValue);

        // Not sure how this can occur, but it was recommended to do so
        panelComponent.getChildren().clear();

        // Build overlay title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(titleText)
                .color(Color.GREEN)
                .build());

        if (!inProfitTrackSession)
        {
            // not in session
            // notify user to reset plugin in order to start
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Reset plugin to start")
                    .color(Color.RED)
                    .build());

        }

        // Set the size of the overlay (width)
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(titleText) + 40,
                0));

        // elapsed time
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Time:")
                .right(formatTimeIntervalFromSec(secondsElapsed))
                .build());

        // Profit
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Profit:")
                .right(FormatIntegerWithCommas(profitValue))
                .build());

        // Profit Rate
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Rate:")
                .right(profitRateValue + "K/H")
                .build());

        return panelComponent.render(graphics);
    }

    /**
     * Updates profit value display
     * @param newValue the value to update the profitValue's {{@link #panelComponent}} with.
     */
    public void updateProfitValue(final long newValue) {
        SwingUtilities.invokeLater(() ->
            profitValue = newValue
        );
    }


    /**
     * Updates startTimeMillies display
     */
    public void updateStartTimeMillies(final long newValue) {
        SwingUtilities.invokeLater(() ->
                startTimeMillies = newValue
        );
    }

    public void startSession()
    {
        SwingUtilities.invokeLater(() ->
                inProfitTrackSession = true
        );
    }

    private static String formatTimeIntervalFromSec(final long totalSecElapsed)
    {
        /*
        elapsed seconds to format HH:MM:SS
         */
        final long sec = totalSecElapsed % 60;
        final long min = (totalSecElapsed / 60) % 60;
        final long hr = totalSecElapsed / 3600;

        return String.format("%02d:%02d:%02d", hr, min, sec);
    }

    static long calculateProfitHourly(long secondsElapsed, long profit)
    {
        long averageProfitThousandForHour;
        long averageProfitForSecond;

        if (secondsElapsed > 0)
        {
            averageProfitForSecond = (profit) / secondsElapsed;
        }
        else
        {
            // can't divide by zero, not enough time has passed
            averageProfitForSecond = 0;
        }

        averageProfitThousandForHour = averageProfitForSecond * 3600 / 1000;

        return averageProfitThousandForHour;
    }
}
