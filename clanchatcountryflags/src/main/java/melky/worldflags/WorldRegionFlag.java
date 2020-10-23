/*
 * Copyright (c) 2020, melky <https://github.com/melkypie>
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
package melky.worldflags;

import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.util.Map;
import net.runelite.client.util.ImageUtil;

enum WorldRegionFlag
{
	// Follow ISO 3166-1 alpha-2 for country codes
	FLAG_US(0),
	FLAG_GB(1),
	FLAG_AU(3),
	FLAG_DE(7);

	private static final Map<Integer, WorldRegionFlag> worldRegionMap;

	private final int regionId;

	static
	{
		ImmutableMap.Builder<Integer, WorldRegionFlag> builder = new ImmutableMap.Builder<>();

		for (final WorldRegionFlag worldRegion : values())
		{
			builder.put(worldRegion.regionId, worldRegion);
		}
		worldRegionMap = builder.build();
	}

	WorldRegionFlag(int regionId)
	{
		this.regionId = regionId;
	}

	BufferedImage loadImage()
	{
		return ImageUtil.getResourceStreamFromClass(getClass(), "/" + this.name().toLowerCase() + ".png");
	}

	static WorldRegionFlag getByRegionId(int regionId)
	{
		return worldRegionMap.get(regionId);
	}
}
