package com.osrsstreamers.ui;

import com.osrsstreamers.OsrsStreamersPlugin;
import com.osrsstreamers.handler.Streamer;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import java.util.stream.Collectors;

public class OsrsStreamersPluginPanel extends PluginPanel {

    OsrsStreamersPlugin plugin;

    private IconTextField searchBar;

    private JPanel streamersContainer;

    private final ImageIcon TWITCH_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(getClass(), "/icon.png"));

    private final ImageIcon ADD_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(getClass(), "/add_icon.png"));

    private static final String STREAMER_VALIDATION_URL = "https://github.com/rhoiyds/osrs-streamers#becoming-a-verified-streamer";

    private static final String TWITCH_DOMAIN = "https://twitch.tv/";

    public OsrsStreamersPluginPanel(OsrsStreamersPlugin osrsStreamersPlugin) {
        this.plugin = osrsStreamersPlugin;
        init();
    }

    void init()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        streamersContainer = new JPanel();
        streamersContainer.setBorder(new EmptyBorder(10, 0, 0, 0));
        streamersContainer.setLayout(new GridLayout(0, 1, 0, 10));
        JLabel title = new JLabel();
        title.setText("OSRS Streamers");
        title.setForeground(Color.WHITE);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        this.searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.setMinimumSize(new Dimension(0, 30));
        searchBar.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                updateStreamersList();
            }
        });
        searchBar.addClearListener(this::updateStreamersList);

        JLabel addHereLabel = new JLabel();
        addHereLabel.setText("Add streamer to list");

        JLabel addHereButton = new JLabel(ADD_ICON);
        addHereButton.setToolTipText("Add yourself or your favorite streamer");
        addHereButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addHereButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                LinkBrowser.browse(STREAMER_VALIDATION_URL);
            }

        });

        JPanel addPanel = new JPanel(new BorderLayout());
        addPanel.setBorder(new EmptyBorder(1, 0, 10, 0));
        addPanel.add(addHereLabel, BorderLayout.WEST);
        addPanel.add(addHereButton, BorderLayout.EAST);

        final JPanel northPanel = new JPanel(new GridBagLayout());
        northPanel.add(title, constraints);
        constraints.gridy++;
        northPanel.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
        constraints.gridy++;
        northPanel.add(searchBar, constraints);
        constraints.gridy++;
        northPanel.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
        constraints.gridy++;
        northPanel.add(addPanel, constraints);

        add(northPanel, BorderLayout.NORTH);
        add(streamersContainer, BorderLayout.CENTER);
        this.updateStreamersList();

    }


    private void updateStreamersList() {
        streamersContainer.removeAll();
        List<Streamer> filteredStreamers =
        this.plugin.streamerHandler.verifiedStreamers.streamers.stream().filter(streamer -> {
            if (Objects.isNull(this.searchBar.getText())) {
                return true;
            }
            return streamer.twitchName.toLowerCase().contains(this.searchBar.getText().toLowerCase());
        }).collect(Collectors.toList());

        filteredStreamers.forEach(streamer -> streamersContainer.add(
                VerifiedStreamerPanel.buildVerifiedStreamerPanel(TWITCH_ICON, streamer.twitchName, String.join(", ", streamer.characterNames), TWITCH_DOMAIN + streamer.twitchName)));

        if (filteredStreamers.isEmpty()) {
            JPanel errorWrapper = new JPanel(new BorderLayout());
            errorWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
            PluginErrorPanel errorPanel = new PluginErrorPanel();
            errorWrapper.add(errorPanel, BorderLayout.NORTH);

            errorPanel.setBorder(new EmptyBorder(50, 20, 20, 20));
            errorPanel.setContent("No streamer found", "Add yourself or your favorite streamer using the plus button above.");
            streamersContainer.add(errorWrapper, errorPanel);
        }

        repaint();
        revalidate();
    }
}
