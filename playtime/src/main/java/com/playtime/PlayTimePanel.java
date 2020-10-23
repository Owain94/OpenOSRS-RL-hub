package com.playtime;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
public class PlayTimePanel extends PluginPanel {
    private final static Color BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
    private final static Color BUTTON_HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;

    private final PlayTimePlugin plugin;
    private JLabel totalTime = new JLabel();
    private JLabel sessionTime = new JLabel();
    private JLabel dayTime = new JLabel();
    private JLabel weekTime = new JLabel();
    private JLabel weekAverage = new JLabel();
    private JLabel monthTime = new JLabel();
    private JLabel monthAverage = new JLabel();

    private boolean shown = false;

    public PlayTimePanel(final PlayTimePlugin plugin)
    {
        super(false);
        this.plugin = plugin;

        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.setLayout(new BorderLayout());
    }

    public void showView()
    {
        updateTimes();
        if (shown) {
            return;
        }
        shown = true;
        final PluginErrorPanel errorPanel = new PluginErrorPanel();
        errorPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
        errorPanel.setContent("Play Time", "Time played");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(sessionTime);
        panel.add(dayTime);
        panel.add(weekTime);
        panel.add(weekAverage);
        panel.add(monthTime);
        panel.add(monthAverage);
        panel.add(totalTime);

        JPanel panel2 = new JPanel();
        JButton button = new JButton("Reset session counter");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plugin.resetCounter();
            }
        });
        panel2.add(button);

        this.add(errorPanel, BorderLayout.NORTH);
        this.add(wrapContainer(panel), BorderLayout.CENTER);
        this.add(panel2, BorderLayout.SOUTH);

        this.revalidate();
        this.repaint();
    }

    public void updateTimes() {
        if (plugin.getSessionTicks() == 0) {
            sessionTime.setText("Login for times to be displayed");
            dayTime.setText("");
            weekTime.setText("");
            weekAverage.setText("");
            monthTime.setText("");
            monthAverage.setText("");
            totalTime.setText("");
            return;
        }
        if (plugin.getConfig().showSeconds() || plugin.getSessionTicks() % (100) == 0) {
            PlayTimeRecord rec = plugin.getCurrentRecord();
            sessionTime.setText("Session: " + (rec != null ? getTimeStampFromTicks(plugin.getSessionTicks()) : "?"));
            dayTime.setText("Today: " + (rec != null ? getTimeStampFromTicks(rec.getTime()) : "?"));
            if (rec == null) {
                weekTime.setText("This week: ?");
                monthTime.setText("This month: ?");
                totalTime.setText("Total: ?");

                if (plugin.getConfig().showAverages()) {
                    weekAverage.setText("This week average: ?");
                    monthAverage.setText("This month average: ?");
                }
            } else {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDate startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                LocalDate endDate = LocalDate.now();
                long weekTicks = ticksBetweenDates(startDate, endDate);

                startDate = LocalDate.now().withDayOfMonth(1);
                long monthTicks = ticksBetweenDates(startDate, endDate);

                weekTime.setText("This week: " + getTimeStampFromTicks(weekTicks));
                monthTime.setText("This month: " + getTimeStampFromTicks(monthTicks));
                totalTime.setText("Total: " + getTimeStampFromTicks(plugin.getTotalTicks()));

                if (plugin.getConfig().showAverages()) {
                    startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                    weekAverage.setText("This week average: " + getTimeStampFromTicks(weekTicks / DAYS.between(startDate, endDate)));
                    startDate = LocalDate.now().withDayOfMonth(1);
                    monthAverage.setText("This month average: " + getTimeStampFromTicks(monthTicks / DAYS.between(startDate, endDate)));
                }
            }
        }
    }

    private long ticksBetweenDates(LocalDate startDate, LocalDate endDate) {
        long days = DAYS.between(startDate, endDate);
        ArrayList<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <= days; i++) {
            LocalDate d = startDate.plusDays(i);
            dates.add(d);
        }
        long ticks = 0;
        for (int i = 0; i <= days; i++) {
            PlayTimeRecord r = plugin.records.get(dates.get(i).format(plugin.DATE_FORMAT));
            if (r != null) {
                ticks += r.getTime();
            }
        }
        return ticks;
    }

    private String getTimeStampFromTicks(long time) {
        long days = (long)Math.floor((double)time / (100 * 60 * 24));
        time -= days * (100 * 60 * 24);
        long hours = (long)Math.floor((double)time / (100 * 60));
        time -= hours * 100 * 60;
        long min = (long)Math.floor((double)time / 100);
        time -= min * 100;
        if (plugin.getConfig().showSeconds()) {
            return String.format("%dd, %dh, %dm, %ds", days, hours, min, (long)(time * 0.6));
        }
        return String.format("%dd, %dh, %dm", days, hours, min);
    }

    private JScrollPane wrapContainer(final JPanel container)
    {
        final JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add(container, BorderLayout.NORTH);
        wrapped.setBackground(BACKGROUND_COLOR);

        final JScrollPane scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroller.setBackground(BACKGROUND_COLOR);

        return scroller;
    }
}
