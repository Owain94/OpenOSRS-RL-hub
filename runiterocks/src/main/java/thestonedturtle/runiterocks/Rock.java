/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package thestonedturtle.runiterocks;

import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public enum Rock
{
	LAVA_MINE_1("LM1", "Lava Maze Mine (46 wildy)", new WorldPoint(3059, 3885, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	LAVA_MINE_2("LM2", "Lava Maze Mine (46 wildy)", new WorldPoint(3060, 3884, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391),

	MINING_GUILD_1("MG1", "Mining Guild", new WorldPoint(3056, 9721, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390, 0.5),
	MINING_GUILD_2("MG2", "Mining Guild", new WorldPoint(3054, 9725, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391, 0.5),

	// Outside llyeta
	ISAFDAR_1("ELF1", "Isafdar (Outside Llyeta)", new WorldPoint(2280, 3160, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	ISAFDAR_2("ELF2", "Isafdar (Outside Llyeta)", new WorldPoint(2278, 3156, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391),

	HEROES_GUILD_1("HG1", "Heroe's Guild", new WorldPoint(2941, 9884, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	HEROES_GUILD_2("HG2", "Heroe's Guild", new WorldPoint(2937, 9882, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391),

	FROZEN_WASTE_PLATEAU_1("FWP1", "Frozen Waste Plateau (50+ wildy)", new WorldPoint(2948, 3914, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	FROZEN_WASTE_PLATEAU_2("FWP2", "Frozen Waste Plateau (50+ wildy)",new WorldPoint(2964, 3933, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	FROZEN_WASTE_PLATEAU_3("FWP3", "Frozen Waste Plateau (50+ wildy)",new WorldPoint(2976, 3937, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391),

	LAVA_MAZE_DUNGEON("LMD", "Lava Maze Dungeon", new WorldPoint(3046, 10265, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),

	FOSSIL_ISLAND_1("FI1", "Fossil Island", new WorldPoint(3781, 3817, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	FOSSIL_ISLAND_2("FI2", "Fossil Island", new WorldPoint(3779, 3814, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391),

	CENTRAL_FREMENNIK_ISLES("CFI", "Central Fremennik isles (NE Neitiznot)", new WorldPoint(2375, 3850, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),

	// Dark Beasts
	MOURNER_TUNNELS("MT", "Mourner Tunnels", new WorldPoint(1993, 4664, 0), ObjectID.ROCKS_11377, ObjectID.ROCKS_11391),

	MYTHS_GUILD_1("MYTH1", "Myths' Guild", new WorldPoint(1937, 9020, 1), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	MYTHS_GUILD_2("MYTH2", "Myths' Guild", new WorldPoint(1939, 9019, 1), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),

	// Tzhaar city (reqs firecape)
	SOUTH_MOR_UI_REK_1("MUR1", "South Mor UI Rek (Tzhaar city)", new WorldPoint(2501, 5066, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	SOUTH_MOR_UI_REK_2("MUR2", "South Mor UI Rek (Tzhaar city)", new WorldPoint(2498, 5065, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),
	SOUTH_MOR_UI_REK_3("MUR3", "South Mor UI Rek (Tzhaar city)", new WorldPoint(2504, 5059, 0), ObjectID.ROCKS_11376, ObjectID.ROCKS_11390),

	// Prifddinas mining area
	TRAHAEARN_1("PRIF1", "Trahaearn (Prifddinas mine)", new WorldPoint(3284, 12459, 0), ObjectID.ROCKS_36209, ObjectID.ROCKS_36202),
	TRAHAEARN_2("PRIF2", "Trahaearn (Prifddinas mine)", new WorldPoint(3287, 12455, 0), ObjectID.ROCKS_36209, ObjectID.ROCKS_36202),
	TRAHAEARN_3("PRIF3", "Trahaearn (Prifddinas mine)", new WorldPoint(3291, 12441, 0), ObjectID.ROCKS_36209, ObjectID.ROCKS_36202),
	TRAHAEARN_4("PRIF4", "Trahaearn (Prifddinas mine)", new WorldPoint(3301, 12438, 0), ObjectID.ROCKS_36209, ObjectID.ROCKS_36202),
	;

	public static final Duration RESPAWN_TIME = Duration.ofMinutes(12);
	private static final ImmutableMap<WorldPoint, Rock> ROCK_LOCATIONS;
	static
	{
		final ImmutableMap.Builder<WorldPoint, Rock> set = ImmutableMap.builder();
		for (Rock rock : values())
		{
			set.put(rock.getWorldPoint(), rock);
		}
		 ROCK_LOCATIONS = set.build();
	}

	private final String name;
	private final String location;
	private final WorldPoint worldPoint;
	private final int activateState;
	private final int depletedState;
	@Getter(AccessLevel.NONE)
	private double respawnRate = 1.0;

	public Duration getRespawnDuration()
	{
		if (respawnRate == 1.0)
		{
			return RESPAWN_TIME;
		}

		return Duration.ofMillis(Math.round(RESPAWN_TIME.toMillis() * respawnRate));
	}

	@Nullable
	public static Rock getByWorldPoint(WorldPoint point)
	{
		return ROCK_LOCATIONS.get(point);
	}
}
