/*
 * Copyright (c) 2020, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.mogcounter;

import com.google.common.collect.EvictingQueue;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

class MOGSession
{
	private static final Duration HOUR = Duration.ofHours(1);

	private int lastMarksSpawned;
	@Getter(AccessLevel.PACKAGE)
	private int marksSpawned;
	@Getter(AccessLevel.PACKAGE)
	private Instant lastMarkSpawnTime;
	@Getter(AccessLevel.PACKAGE)
	private int totalMarkSpawnEvents;
	@Getter(AccessLevel.PACKAGE)
	private int spawnsPerHour;

	private final Map<WorldPoint, Integer> markTiles = new HashMap<>();
	private final Set<WorldPoint> ignoreTiles = new HashSet<>();
	private boolean isDirty;
	private EvictingQueue<Duration> markSpawnTimes = EvictingQueue.create(20);

	void addMarkTile(WorldPoint point, int markCount)
	{
		if (!ignoreTiles.contains(point))
		{
			markTiles.put(point, markCount);
			isDirty = true;
		}
	}

	void addIgnoreTile(WorldPoint point)
	{
		ignoreTiles.add(point);
	}

	void removeMarkTile(WorldPoint point)
	{
		markTiles.remove(point);
		isDirty = true;
	}

	void removeIgnoreTile(WorldPoint point)
	{
		ignoreTiles.remove(point);
	}

	synchronized void checkMarkSpawned()
	{
		if (!isDirty)
		{
			return;
		}
		isDirty = false;

		marksSpawned = 0;
		for (int i : markTiles.values())
		{
			marksSpawned += i;
		}

		if (marksSpawned > lastMarksSpawned)
		{
			Instant now = Instant.now();
			if (lastMarkSpawnTime != null)
			{
				markSpawnTimes.add(Duration.between(lastMarkSpawnTime, now));
				calculateMarksPerHour();
			}
			lastMarkSpawnTime = now;
			totalMarkSpawnEvents++;
		}
		lastMarksSpawned = marksSpawned;
	}

	private void calculateMarksPerHour()
	{
		int sz = markSpawnTimes.size();
		if (sz > 0)
		{
			Duration sum = Duration.ZERO;
			for (Duration markTime : markSpawnTimes)
			{
				sum = sum.plus(markTime);
			}

			spawnsPerHour = (int) (HOUR.toMillis() / sum.dividedBy(sz).toMillis());
		}
		else
		{
			spawnsPerHour = 0;
		}
	}

	void clearCounters()
	{
		lastMarksSpawned = 0;
		lastMarkSpawnTime = null;
		markSpawnTimes.clear();
		marksSpawned = 0;
		totalMarkSpawnEvents = 0;
		spawnsPerHour = 0;
		markTiles.clear();
		ignoreTiles.clear();
		isDirty = false;
	}

	void clearSpawnedMarks()
	{
		marksSpawned = 0;
		lastMarksSpawned = 0;
		markTiles.clear();
		ignoreTiles.clear();
		isDirty = false;
	}
}
