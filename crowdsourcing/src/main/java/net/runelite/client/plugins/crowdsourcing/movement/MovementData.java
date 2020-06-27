package net.runelite.client.plugins.crowdsourcing.movement;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuOptionClicked;

@Data
@AllArgsConstructor
public class MovementData
{
	private final WorldPoint start;
	private final WorldPoint end;
	private final boolean fromInstance;
	private final boolean toInstance;
	private final int ticks;
	private MenuOptionClicked lastClick;
}