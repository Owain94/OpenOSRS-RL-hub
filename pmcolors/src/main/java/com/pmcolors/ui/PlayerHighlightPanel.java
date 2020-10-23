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
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PlayerHighlightPanel extends JPanel
{
    private static final Border PLAYER_NAME_BOTTOM_BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));

    private final JLabel playerNameIndicator = new JLabel();

    private final JLabel highlightColorIndicator = new JLabel();
    private final JLabel messageHighlightIndicator = new JLabel();
    private final JLabel nameHighlightIndicator = new JLabel();
    private final JLabel loginoutHighlightIndicator = new JLabel();

    private final JLabel deleteLabel = new JLabel();

    private PMColorsPlugin plugin;
    private PlayerHighlight playerHighlight;

    private static final ImageIcon DELETE_ICON;
    private static final ImageIcon DELETE_HOVER_ICON;

    private static final ImageIcon HIGHLIGHT_COLOR_ICON;
    private static final ImageIcon HIGHLIGHT_COLOR_HOVER_ICON;

    private static final ImageIcon HIGHLIGHT_MESSAGE_ICON;
    private static final ImageIcon HIGHLIGHT_MESSAGE_HOVER_ICON;
    private static final ImageIcon NO_HIGHLIGHT_MESSAGE_ICON;
    private static final ImageIcon NO_HIGHLIGHT_MESSAGE_HOVER_ICON;
    
    private static final ImageIcon HIGHLIGHT_NAME_ICON;
    private static final ImageIcon HIGHLIGHT_NAME_HOVER_ICON;
    private static final ImageIcon NO_HIGHLIGHT_NAME_ICON;
    private static final ImageIcon NO_HIGHLIGHT_NAME_HOVER_ICON;

    private static final ImageIcon HIGHLIGHT_LOGINOUT_ICON;
    private static final ImageIcon HIGHLIGHT_LOGINOUT_HOVER_ICON;
    private static final ImageIcon NO_HIGHLIGHT_LOGINOUT_ICON;
    private static final ImageIcon NO_HIGHLIGHT_LOGINOUT_HOVER_ICON;
    private Color selectedColor;

    static
    {
        final BufferedImage deleteImg = ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "/delete_icon.png");
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -100));

        final BufferedImage highlightIcon = ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "/highlight_color_icon.png");
        final BufferedImage highlightIconHover = ImageUtil.luminanceOffset(highlightIcon, -150);
        HIGHLIGHT_COLOR_ICON = new ImageIcon(highlightIcon);
        HIGHLIGHT_COLOR_HOVER_ICON = new ImageIcon(highlightIconHover);

        final BufferedImage highlightMessageIcon = ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "/chat_icon.png");
        final BufferedImage highlightMessageIconHover = ImageUtil.luminanceOffset(highlightMessageIcon, -150);
        HIGHLIGHT_MESSAGE_ICON = new ImageIcon(highlightMessageIcon);
        HIGHLIGHT_MESSAGE_HOVER_ICON = new ImageIcon(highlightMessageIconHover);

        NO_HIGHLIGHT_MESSAGE_ICON = new ImageIcon(highlightMessageIconHover);
        NO_HIGHLIGHT_MESSAGE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightMessageIconHover, -100));

        final BufferedImage highlightNameIcon = ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "/name_icon.png");
        final BufferedImage highlightNameIconHover = ImageUtil.luminanceOffset(highlightNameIcon, -150);
        HIGHLIGHT_NAME_ICON = new ImageIcon(highlightNameIcon);
        HIGHLIGHT_NAME_HOVER_ICON = new ImageIcon(highlightNameIconHover);

        NO_HIGHLIGHT_NAME_ICON = new ImageIcon(highlightNameIconHover);
        NO_HIGHLIGHT_NAME_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightNameIconHover, -100));

        final BufferedImage highlightLogInOutIcon = ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "/loginout_icon.png");
        final BufferedImage highlightLogInOutIconHover = ImageUtil.luminanceOffset(highlightLogInOutIcon, -150);
        HIGHLIGHT_LOGINOUT_ICON = new ImageIcon(highlightLogInOutIcon);
        HIGHLIGHT_LOGINOUT_HOVER_ICON = new ImageIcon(highlightLogInOutIconHover);

        NO_HIGHLIGHT_LOGINOUT_ICON = new ImageIcon(highlightLogInOutIconHover);
        NO_HIGHLIGHT_LOGINOUT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightLogInOutIconHover, -100));
    }

    PlayerHighlightPanel(PMColorsPlugin plugin, PlayerHighlight playerHighlight)
    {
        this.plugin = plugin;
        this.playerHighlight = playerHighlight;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        playerNameIndicator.setText(playerHighlight.getName());
        playerNameIndicator.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        playerNameIndicator.setPreferredSize(new Dimension(0, 24));
        playerNameIndicator.setForeground(Color.WHITE);
        playerNameIndicator.setBorder(new EmptyBorder(0, 8, 0, 0));

        highlightColorIndicator.setToolTipText("Edit highlight color");
        highlightColorIndicator.setIcon(HIGHLIGHT_COLOR_ICON);
        highlightColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, playerHighlight.getColor()));
        highlightColorIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                openPlayerColorPicker();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                highlightColorIndicator.setIcon(HIGHLIGHT_COLOR_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                highlightColorIndicator.setIcon(HIGHLIGHT_COLOR_ICON);
            }
        });


        messageHighlightIndicator.setToolTipText("Toggle highlighting the message content");
        messageHighlightIndicator.setIcon(playerHighlight.isHighlightMessage() ? HIGHLIGHT_MESSAGE_ICON : NO_HIGHLIGHT_MESSAGE_ICON);;
        messageHighlightIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                playerHighlight.setHighlightMessage(!playerHighlight.isHighlightMessage());
                plugin.updateConfig();
                messageHighlightIndicator.setIcon(playerHighlight.isHighlightMessage() ? HIGHLIGHT_MESSAGE_HOVER_ICON : NO_HIGHLIGHT_MESSAGE_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                messageHighlightIndicator.setIcon(playerHighlight.isHighlightMessage() ? HIGHLIGHT_MESSAGE_HOVER_ICON : NO_HIGHLIGHT_MESSAGE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                messageHighlightIndicator.setIcon(playerHighlight.isHighlightMessage() ? HIGHLIGHT_MESSAGE_ICON : NO_HIGHLIGHT_MESSAGE_ICON);;
            }
        });



        nameHighlightIndicator.setToolTipText("Toggle highlighting the username");
        nameHighlightIndicator.setIcon(playerHighlight.isHighlightUsername() ? HIGHLIGHT_NAME_ICON : NO_HIGHLIGHT_NAME_ICON);;
        nameHighlightIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                playerHighlight.setHighlightUsername(!playerHighlight.isHighlightUsername());
                plugin.updateConfig();
                nameHighlightIndicator.setIcon(playerHighlight.isHighlightUsername() ? HIGHLIGHT_NAME_HOVER_ICON : NO_HIGHLIGHT_NAME_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                nameHighlightIndicator.setIcon(playerHighlight.isHighlightUsername() ? HIGHLIGHT_NAME_HOVER_ICON : NO_HIGHLIGHT_NAME_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                nameHighlightIndicator.setIcon(playerHighlight.isHighlightUsername() ? HIGHLIGHT_NAME_ICON : NO_HIGHLIGHT_NAME_ICON);
            }
        });


        loginoutHighlightIndicator.setToolTipText("Toggle highlighting the log in/out message");
        loginoutHighlightIndicator.setIcon(playerHighlight.isHighlightLoggedInOut() ? HIGHLIGHT_LOGINOUT_ICON : NO_HIGHLIGHT_LOGINOUT_ICON);;
        loginoutHighlightIndicator.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                playerHighlight.setHighlightLoggedInOut(!playerHighlight.isHighlightLoggedInOut());
                plugin.updateConfig();
                loginoutHighlightIndicator.setIcon(playerHighlight.isHighlightLoggedInOut() ? HIGHLIGHT_LOGINOUT_HOVER_ICON : NO_HIGHLIGHT_LOGINOUT_HOVER_ICON);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                loginoutHighlightIndicator.setIcon(playerHighlight.isHighlightLoggedInOut() ? HIGHLIGHT_LOGINOUT_HOVER_ICON : NO_HIGHLIGHT_LOGINOUT_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                loginoutHighlightIndicator.setIcon(playerHighlight.isHighlightLoggedInOut() ? HIGHLIGHT_LOGINOUT_ICON : NO_HIGHLIGHT_LOGINOUT_ICON);
            }
        });
        
        deleteLabel.setIcon(DELETE_ICON);
        deleteLabel.setToolTipText("Delete player highlight");
        deleteLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                int confirm = JOptionPane.showConfirmDialog(PlayerHighlightPanel.this,
                        "Are you sure you want to permanently delete this player highlight?",
                        "Warning", JOptionPane.OK_CANCEL_OPTION);

                if (confirm == 0)
                {
                    plugin.deleteHighlight(playerHighlight);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent)
            {
                deleteLabel.setIcon(DELETE_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent)
            {
                deleteLabel.setIcon(DELETE_ICON);
            }
        });

        JPanel nameWrapper = new JPanel(new BorderLayout(3, 0));
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setBorder(PLAYER_NAME_BOTTOM_BORDER);

        nameWrapper.add(playerNameIndicator);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        leftActions.add(highlightColorIndicator);
        leftActions.add(messageHighlightIndicator);
        leftActions.add(nameHighlightIndicator);
        leftActions.add(loginoutHighlightIndicator);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        rightActions.add(deleteLabel);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
        bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);


        bottomContainer.add(leftActions, BorderLayout.WEST);
        bottomContainer.add(rightActions, BorderLayout.EAST);

        add(nameWrapper, BorderLayout.NORTH);
        add(bottomContainer, BorderLayout.CENTER);
    }
    private String colorToHex(Color color)
    {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }


    private void openPlayerColorPicker()
    {
        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                playerHighlight.getColor(),
                playerHighlight.getName() + " highlight color",
                true);
        colorPicker.setLocation(getLocationOnScreen());
        colorPicker.setOnColorChange(c ->
        {
            playerHighlight.setColor(c);
            highlightColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, playerHighlight.getColor()));
        });
        colorPicker.setOnClose(c -> plugin.updateConfig());
        colorPicker.setVisible(true);
    }
}
