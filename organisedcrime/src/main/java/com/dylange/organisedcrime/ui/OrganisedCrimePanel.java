package com.dylange.organisedcrime.ui;

import com.dylange.organisedcrime.config.OrganisedCrimeConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class OrganisedCrimePanel extends PluginPanel {

    private final OrganisedCrimeConfig config;
    private final JPanel layoutPanel = new JPanel();
    private final Consumer<Integer> onWorldClicked;

    public OrganisedCrimePanel(OrganisedCrimeConfig config, Consumer<Integer> onWorldClicked) {
        this.config = config;
        this.onWorldClicked = onWorldClicked;

        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
        add(layoutPanel, BorderLayout.NORTH);
    }

    public void displayEmpty() {
        layoutPanel.removeAll();
        PluginErrorPanel errorPanel = new PluginErrorPanel();
        errorPanel.setContent(
                "Organised Crime Tracker",
                "View the information board to start tracking locations."
        );
        layoutPanel.add(errorPanel);
    }

    public void display(List<LocationViewState> viewState) {
        layoutPanel.removeAll();
        viewState.forEach(locationViewState -> {
            layoutPanel.add(new LocationPanel(locationViewState, onWorldClicked));
            layoutPanel.revalidate();
        });
    }

    public void refresh() {
        for (Component component : layoutPanel.getComponents()) {
            if (component instanceof LocationPanel) {
                ((LocationPanel) component).refreshWorldButtons();
            }
        }
    }
}
