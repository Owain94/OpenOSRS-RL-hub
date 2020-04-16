package net.runelite.client.plugins.npcidletimer;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

public class WanderingNPC
{
	@Getter(AccessLevel.PACKAGE)
	private final int npcIndex;

	@Getter(AccessLevel.PACKAGE)
	private final String npcName;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private NPC npc;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Instant stoppedMovingTick;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private long timeWithoutMoving;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private WorldPoint currentLocation;

	WanderingNPC(NPC npc)
	{
		this.npc = npc;
		this.npcName = npc.getName();
		this.npcIndex = npc.getIndex();
		this.stoppedMovingTick = Instant.now();
		this.timeWithoutMoving = 0;
		this.currentLocation = npc.getWorldLocation();
	}
}