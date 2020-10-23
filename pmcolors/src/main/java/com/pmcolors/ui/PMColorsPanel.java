/*
* Copyright (c) 2020, PresNL
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this
* list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
* The Panel code has taken a lot of inspiration from the ScreenMarkers plugin by Psikoi so credits to him
*/

package com.pmcolors.ui;

import com.pmcolors.PMColorsPlugin;
import com.pmcolors.PlayerHighlight;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PMColorsPanel extends PluginPanel
{
    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_HOVER_ICON;

    private final JLabel addHighlightedPlayer = new JLabel(ADD_ICON);
    private final JLabel title = new JLabel();

    private final PluginErrorPanel noPlayersPanel = new PluginErrorPanel();
    private final JPanel playerView = new JPanel(new GridBagLayout());

    private PMColorsPlugin plugin;

    @Getter
    private AddPlayerPanel addPlayerPanel;
    static
    {
        final BufferedImage addIcon = ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "/add_icon.png");
        ADD_ICON = new ImageIcon(addIcon);
        ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));
    }

    public PMColorsPanel(PMColorsPlugin pmColorsPlugin)
    {
        this.plugin = pmColorsPlugin;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

        title.setText("PM Colors");
        title.setForeground(Color.WHITE);

        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(addHighlightedPlayer, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        playerView.setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        noPlayersPanel.setContent("PM Colors", "Assign colors to players in your private messages");
        noPlayersPanel.setVisible(false);

        playerView.add(noPlayersPanel, constraints);
        constraints.gridy++;

        addPlayerPanel = new AddPlayerPanel(plugin);
        addPlayerPanel.setVisible(false);

        playerView.add(addPlayerPanel, constraints);

        constraints.gridy++;

        addHighlightedPlayer.setToolTipText("Add player");
        addHighlightedPlayer.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                setCreation(true);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                addHighlightedPlayer.setIcon(ADD_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                addHighlightedPlayer.setIcon(ADD_ICON);
            }
        });

        centerPanel.add(playerView, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void rebuild()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        playerView.removeAll();

        for (final PlayerHighlight marker : plugin.getHighlightedPlayers())
        {
            playerView.add(new PlayerHighlightPanel(plugin, marker), constraints);
            constraints.gridy++;

            playerView.add(Box.createRigidArea(new Dimension(0, 5)), constraints);
            constraints.gridy++;
        }

        boolean empty = constraints.gridy == 0;
        noPlayersPanel.setVisible(empty);
        title.setVisible(!empty);

        playerView.add(noPlayersPanel, constraints);
        constraints.gridy++;

        playerView.add(addPlayerPanel, constraints);
        constraints.gridy++;

        repaint();
        revalidate();
    }


    /* Enables/Disables new marker creation mode */
    public void setCreation(boolean on)
    {
        if (on)
        {
            noPlayersPanel.setVisible(false);
            title.setVisible(true);
        }
        else
        {
            boolean empty = plugin.getHighlightedPlayers().isEmpty();
            noPlayersPanel.setVisible(empty);
            title.setVisible(!empty);
        }

        addPlayerPanel.setVisible(on);
        addHighlightedPlayer.setVisible(!on);
    }
}
