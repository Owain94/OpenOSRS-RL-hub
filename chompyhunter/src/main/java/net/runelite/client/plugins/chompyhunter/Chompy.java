package net.runelite.client.plugins.chompyhunter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import net.runelite.api.NPC;

@Data
class Chompy
{
	@Getter(AccessLevel.PACKAGE)
	private final NPC npc;
	@Getter(AccessLevel.PACKAGE)
	private Instant spawnTime;

	Chompy(NPC npc)
	{
		this.npc = npc;
		this.spawnTime = Instant.now().plus(60,
			ChronoUnit.SECONDS);
	}
}