package wtf.kaan.raidplayernames;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class RaidPlayerNamesBox extends JPanel {

        private RaidPlayerNamesPanel panel;

    RaidPlayerNamesBox(RaidPlayerNamesPanel panel, List<String> players) {
        this.panel = panel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BorderLayout());
        topContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JTextArea textArea = new JTextArea();
        textArea.setText(String.join("\n", players));
        topContainer.add(textArea, BorderLayout.NORTH);
        add(topContainer, BorderLayout.NORTH);
    }
}
