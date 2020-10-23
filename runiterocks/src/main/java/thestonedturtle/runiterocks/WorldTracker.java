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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.http.api.worlds.World;

@Getter
public class WorldTracker
{
	@Getter(AccessLevel.NONE)
	private final Map<Rock, RuniteRock> rockMap = new HashMap<>();
	private final World world;

	public WorldTracker(final World world)
	{
		this.world = world;
	}

	@Nullable
	public RuniteRock updateRockState(final WorldPoint worldPoint, final GameObject gameObject)
	{
		final Rock rock = Rock.getByWorldPoint(worldPoint);
		if (rock == null)
		{
			return null;
		}

		final RuniteRock runeRock = rockMap.getOrDefault(rock, new RuniteRock(world, rock));
		runeRock.setAvailable(gameObject.getId());
		rockMap.put(rock, runeRock);

		return runeRock;
	}

	public void removeRock(final Rock rock)
	{
		rockMap.remove(rock);
	}

	public void clear()
	{
		rockMap.clear();
	}

	public Collection<RuniteRock> getRuniteRocks()
	{
		return rockMap.values();
	}
}
