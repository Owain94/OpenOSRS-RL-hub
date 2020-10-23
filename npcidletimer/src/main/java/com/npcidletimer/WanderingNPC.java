package com.npcidletimer;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

public class WanderingNPC
{
	@Getter
	private final int npcIndex;

	@Getter
	private final String npcName;

	@Getter
	@Setter
	private NPC npc;

	@Getter
	@Setter
	private Instant stoppedMovingTick;

	@Getter
	@Setter
	private long timeWithoutMoving;

	@Getter
	@Setter
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