package com.dylange.organisedcrime.ui;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

@Slf4j
public class LocationPanel extends JPanel {

    private final LocationViewState viewState;
    private final Consumer<Integer> onWorldClicked;
    private final Color backgroundColour = ColorScheme.DARKER_GRAY_COLOR.darker();

    private final JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
    private final JPanel descriptionContainer = new JPanel();
    private final JPanel worldsContainer = new JPanel();

    private final JTextArea descriptionLabel = new JTextArea();

    public LocationPanel(LocationViewState viewState, Consumer<Integer> onWorldClicked) {
        this.viewState = viewState;
        this.onWorldClicked = onWorldClicked;
        setLayout(new BorderLayout(0, 1));
        setBackground(backgroundColour);
        separator.setBackground(ColorScheme.MEDIUM_GRAY_COLOR);

        descriptionContainer.setLayout(new GridBagLayout());
        descriptionContainer.setBorder(new EmptyBorder(8, 8, 8, 8));
        descriptionContainer.setBackground(backgroundColour);

        int numRows = viewState.getExpectedTimeToWorld().keySet().size();
        worldsContainer.setLayout(new GridLayout(numRows, 1, 0, 2));
        worldsContainer.setBorder(new EmptyBorder(8, 8, 8, 8));
        worldsContainer.setBackground(backgroundColour);

        add(separator, BorderLayout.NORTH);
        add(descriptionContainer, BorderLayout.CENTER);
        add(worldsContainer, BorderLayout.SOUTH);

        descriptionLabel.setText(viewState.getDescription());
        descriptionLabel.setEditable(false);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setFont(FontManager.getRunescapeFont());
        GridBagConstraints descriptionConstraints = new GridBagConstraints();
        descriptionConstraints.fill = GridBagConstraints.HORIZONTAL;
        descriptionConstraints.gridy = 0;
        descriptionContainer.add(descriptionLabel, descriptionConstraints);

        BufferedImage locationIcon = ImageUtil.getResourceStreamFromClass(getClass(), viewState.getImage());
        JLabel locationImage = new JLabel(new ImageIcon(new ImageIcon(locationIcon).getImage().getScaledInstance(214, 214, Image.SCALE_DEFAULT)));
        GridBagConstraints locationImageConstraints = new GridBagConstraints();
        locationImageConstraints.fill = GridBagConstraints.HORIZONTAL;
        locationImageConstraints.gridy = 1;
        locationImageConstraints.anchor = GridBagConstraints.CENTER;
        descriptionContainer.add(locationImage, locationImageConstraints);
        drawWorldsButtons();
    }

    public void refreshWorldButtons() {
        SwingUtilities.invokeLater(this::drawWorldsButtons);
    }

    private void drawWorldsButtons() {
        if (worldsContainer == null || viewState == null) return;
        worldsContainer.removeAll();
        viewState.getExpectedTimeToWorld().forEach((expectedTime, world) -> {
            String buttonText = String.format("W%d %s", world, expectedTime.toString());
            JButton worldButton = new JButton(buttonText);
            worldButton.addActionListener(e -> onWorldClicked.accept(world));
            worldsContainer.add(worldButton);
        });
        worldsContainer.revalidate();
    }
}
