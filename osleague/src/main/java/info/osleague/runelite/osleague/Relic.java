package info.osleague.runelite.osleague;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

public enum Relic
{
	ENDLESS_HARVEST("Endless Harvest", 2700, 0, 0),
	PRODUCTION_MASTER("Production Master", 2701, 0, 1),
	SKILLING_PRODIGY("Skilling Prodigy", 2702, 0, 2),
	FAIRYS_FLIGHT("Fairy's Flight", 2703, 1, 0),
	ETERNAL_JEWELLER("Eternal Jeweller", 2704, 1, 1),
	LAST_RECALL("Last Recall", 2705, 1, 2),
	QUICK_SHOT("Quick Shot", 2706, 2, 0),
	FLUID_STRIKES("Fluid Strikes", 2707, 2, 1),
	DOUBLE_CAST("Double Cast", 2708, 2, 2),
	//unknown 2709
	TREASURE_SEEKER("Treasure Seeker", 2710, 3, 0),
	UNNATURAL_SELECTION("Unnatural Selection", 2711, 3, 1),
	THE_BOTANIST("The Botanist", 2712, 4, 0),
	INFERNAL_GATHERING("Infernal Gathering", 2713, 4, 1),
	EQUILIBRIUM("Equilibrium", 2714, 4, 2),
	DRAINING_STRIKES("Draining Strikes", 2715, 5, 0),
	EXPLODING_ATTACKS("Exploding Attacks", 2716, 5, 1),
	WEAPON_SPECIALIST("Weapon Specialist", 2717, 5, 2);

	private final String name;
	private final int spriteId;
	@Getter
	private final int tierId;
	@Getter
	private final int relicId;

	Relic(String name, int spriteId, int tierId, int relicId)
	{
		this.name = name;
		this.spriteId = spriteId;
		this.tierId = tierId;
		this.relicId = relicId;
	}

	private static final Map<Integer, Relic> SPRITES;

	static
	{
		ImmutableMap.Builder<Integer, Relic> spriteBuilder = new ImmutableMap.Builder<>();

		for (Relic relic : values())
		{
			spriteBuilder.put(relic.spriteId, relic);
		}

		SPRITES = spriteBuilder.build();
	}

	static Relic getRelicBySprite(int id)
	{
		return SPRITES.get(id);
	}
}
