package ejedev.chompyhunter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.NPC;
import java.time.temporal.ChronoUnit;
import java.time.Instant;

@Data
class Chompy {
    @Getter(AccessLevel.PACKAGE)
    private final NPC npc;
    @Getter(AccessLevel.PACKAGE)
    private Instant spawnTime;

    Chompy(NPC npc)
    {
        this.npc = npc;
        this.spawnTime = Instant.now().plus(60,
                ChronoUnit.SECONDS);;
    }
}
