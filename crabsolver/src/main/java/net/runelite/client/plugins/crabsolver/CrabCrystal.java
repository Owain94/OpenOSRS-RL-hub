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
package net.runelite.client.plugins.crabsolver;

import com.google.common.collect.ImmutableMap;
import java.awt.Color;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;

@AllArgsConstructor
@Getter(AccessLevel.PACKAGE)
public enum CrabCrystal
{
	BLACK(ObjectID.BLACK_CRYSTAL, new Color(255, 255, 255, 122), null),
	YELLOW(ObjectID.YELLOW_CRYSTAL, new Color(0, 0, 255, 122), Skill.MAGIC),
	CYAN(ObjectID.CYAN_CRYSTAL, new Color(255, 0, 0, 122), Skill.ATTACK),
	MAGENTA(ObjectID.MAGENTA_CRYSTAL, new Color(0, 255, 0, 122), Skill.RANGED),
	;

	private final int objectID;
	private final Color solutionColor;
	private final Skill solutionSkill;

	private static final ImmutableMap<Integer, CrabCrystal> OBJECT_ID_MAP;

	static
	{
		final ImmutableMap.Builder<Integer, CrabCrystal> builder = ImmutableMap.builder();
		for (final CrabCrystal crystal : values())
		{
			builder.put(crystal.getObjectID(), crystal);
		}
		OBJECT_ID_MAP = builder.build();
	}

	@Nullable
	public static CrabCrystal getByObjectID(final int id)
	{
		return OBJECT_ID_MAP.get(id);
	}
}