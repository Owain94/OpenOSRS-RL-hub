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

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.worlds.World;

@Data
@Slf4j
public class RuniteRock
{
	private final World world;
	private final Rock rock;
	private boolean available = false;
	private Instant updatedAt = Instant.now();
	private Instant lastSeenAt = Instant.now();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean witnessedDepletion = false;

	public Instant getRespawnTime()
	{
		if (available)
		{
			return lastSeenAt;
		}

		return updatedAt.plus(rock.getRespawnDuration());
	}

	public void setAvailable(final int gameObjectId)
	{
		lastSeenAt = Instant.now();

		if (gameObjectId == rock.getActivateState())
		{
			if (available)
			{
				return;
			}

			available = true;
			updatedAt = Instant.now();
			witnessedDepletion = false;
		}
		else if (gameObjectId == rock.getDepletedState())
		{
			// If rocks depleted and wasn't previously available there's no change
			if (!available)
			{
				// If the respawn timer is in the past we need to assume it was updated when we couldn't see it.
				if (getRespawnTime().compareTo(Instant.now()) < 0)
				{
					updatedAt = Instant.now();
					witnessedDepletion = false;
				}
				return;
			}

			available = false;
			updatedAt = Instant.now();
			witnessedDepletion = true;
		}
		else
		{
			log.warn("Unexpected object id for Rock: {} | {}", gameObjectId, rock);
		}
	}

	public boolean matches(final RuniteRock other)
	{
		return this.getRock() == other.getRock() && this.getWorld().getId() == other.getWorld().getId();
	}

	public boolean hasWitnessedDepletion()
	{
		return witnessedDepletion;
	}
}
