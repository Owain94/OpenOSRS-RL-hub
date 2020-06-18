package net.runelite.client.plugins.crowdsourcing.woodcutting;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.crowdsourcing.skilling.SkillingEndReason;

@Data
@AllArgsConstructor
public class WoodcuttingData
{
	private final int level;
	private final int startTick;
	private final int endTick;
	private final List<Integer> chopTicks;
	private final List<Integer> nestTicks;
	private final int axe;
	private final int treeId;
	private final WorldPoint treeLocation;
	private final SkillingEndReason reason;
}