package ejedev.chompyhunter;
import net.runelite.api.*;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY;
import static net.runelite.api.MenuOpcode.RUNELITE_OVERLAY_CONFIG;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class ChompyHunterInfoOverlay extends OverlayPanel {
    private final ChompyHunterPlugin plugin;
    private final Client client;
    static final String CHOMPY_RESET = "Reset";

    @Inject
    public ChompyHunterInfoOverlay(ChompyHunterPlugin plugin, Client client) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.client = client;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Chompy Overlay"));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, CHOMPY_RESET, "Chompy Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.getChompyKills() < 1) {
            return null;
        } else {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Chompy Hunting")
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Chompies Killed:")
                    .right(Integer.toString(plugin.getChompyKills()))
                    .build());
            if (plugin.getChompyKills() > 0) {
                float elapsed = (float) (((Duration.between(plugin.getStartTime(), Instant.now()).getSeconds()) /60.0) /60.0);
                int perHour = (int) (plugin.getChompyKills() / elapsed);
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Per hour:")
                        .right(Integer.toString(perHour))
                        .build());
            }
            return super.render(graphics);
        }
    }
}
