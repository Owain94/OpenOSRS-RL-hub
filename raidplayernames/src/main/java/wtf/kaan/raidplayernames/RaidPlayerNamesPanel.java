package wtf.kaan.raidplayernames;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class RaidPlayerNamesPanel extends PluginPanel {

    private List<RaidPlayerNamesBox> boxes = new ArrayList<>();

    RaidPlayerNamesPanel(RaidPlayerNamesPlugin plugin) {
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);

    }

    public void addPanel(List<String> people) {
        if(people.size() > 1) {
            add(new RaidPlayerNamesBox(this, people));
            validate();
            repaint();
        }
    }

}
