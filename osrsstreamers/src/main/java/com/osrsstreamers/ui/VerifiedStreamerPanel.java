package com.osrsstreamers.ui;


import com.osrsstreamers.OsrsStreamersPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VerifiedStreamerPanel {

    private static final ImageIcon ARROW_RIGHT_ICON;

    static
    {
        ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(OsrsStreamersPlugin.class, "/util/arrow_right.png"));
    }

    public static JPanel buildVerifiedStreamerPanel(ImageIcon icon, String topText, String bottomText, String url) {
        JPanel container = new JPanel();
        container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        container.setLayout(new BorderLayout());
        container.setBorder(new EmptyBorder(5, 5, 5, 5));

        final Color hoverColor = ColorScheme.DARKER_GRAY_HOVER_COLOR;
        final Color pressedColor = ColorScheme.DARKER_GRAY_COLOR.brighter();

        //Will add icons online/offline status soon
        //JLabel iconLabel = new JLabel(icon);
        //container.add(iconLabel, BorderLayout.WEST);

        JPanel textContainer = new JPanel();
        textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textContainer.setLayout(new GridLayout(2, 1));
        textContainer.setBorder(new EmptyBorder(2, 5, 2, 5));

        container.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                container.setBackground(pressedColor);
                textContainer.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                LinkBrowser.browse(url);
                container.setBackground(hoverColor);
                textContainer.setBackground(hoverColor);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                container.setBackground(hoverColor);
                textContainer.setBackground(hoverColor);
                container.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                container.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                container.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JLabel topLine = new JLabel(topText);
        topLine.setForeground(Color.WHITE);
        topLine.setFont(FontManager.getRunescapeSmallFont());

        JLabel bottomLine = new JLabel(bottomText);
        bottomLine.setForeground(Color.GRAY);
        bottomLine.setFont(FontManager.getRunescapeSmallFont());

        textContainer.add(topLine);
        textContainer.add(bottomLine);

        container.add(textContainer, BorderLayout.CENTER);

        JLabel arrowLabel = new JLabel(ARROW_RIGHT_ICON);
        container.add(arrowLabel, BorderLayout.EAST);
        return container;
    }

}
