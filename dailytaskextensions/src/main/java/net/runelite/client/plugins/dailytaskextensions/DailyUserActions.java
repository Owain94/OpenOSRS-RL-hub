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
package net.runelite.client.plugins.dailytaskextensions;

import lombok.Getter;

public class DailyUserActions
{
	private int actionsPerformed;
	private int lastDay;

	@Getter
	private final int maxActions;

	public DailyUserActions(int maxActions, int actionsPerformed, int lastDay)
	{
		this.maxActions = maxActions;
		this.actionsPerformed = actionsPerformed;
		this.lastDay = lastDay;
	}

	public DailyUserActions(int maxActions)
	{
		this(maxActions, 0, 0);
	}

	public DailyUserActions(int maxActions, String configString)
	{
		this(maxActions);

		if (configString != null)
		{
			String[] parts = configString.split(":");
			if (parts.length == 2)
			{
				try
				{
					actionsPerformed = Integer.parseInt(parts[0]);
					lastDay = Integer.parseInt(parts[1]);
				}
				catch (NumberFormatException e)
				{
					actionsPerformed = 0;
					lastDay = 0;
				}
			}
		}
	}

	/**
	 * Sets the number of actions performed.
	 * <p>
	 * Actions cannot go over max actions and will be capped.
	 *
	 * @param actions The number of actions.
	 * @param today   The current day from Epoch.
	 * @return True if the action count or last day was changed, false otherwise.
	 */
	public boolean setCount(int actions, int today)
	{
		actions = Math.min(actions, maxActions);

		if (actions != actionsPerformed || today != lastDay)
		{
			actionsPerformed = actions;
			lastDay = today;
			return true;
		}

		return false;
	}

	/**
	 * Sets the number of actions performed to max.
	 *
	 * @param today The current day from Epoch.
	 * @return True if the action count or last day was changed, false otherwise.
	 */
	public boolean setCountToMax(int today)
	{
		return setCount(maxActions, today);
	}

	/**
	 * Adds a number of actions to the action count.
	 *
	 * @param actions The number of actions to add.
	 * @param today   The current day from Epoch.
	 * @return True if the action count or last day was changed, false otherwise.
	 */
	public boolean addCount(int actions, int today)
	{
		return setCount(actions + getCount(today), today);
	}

	/**
	 * Gets the action count.
	 *
	 * @return The action count, 0 if the last action was more than a day ago.
	 */
	public int getCount(int today)
	{
		if (today > lastDay)
		{
			return 0;
		}

		return actionsPerformed;
	}

	/**
	 * Gets the string representation of this object to be saved in the Runelite config.
	 */
	public String getConfigString()
	{
		return actionsPerformed + ":" + lastDay;
	}
}

