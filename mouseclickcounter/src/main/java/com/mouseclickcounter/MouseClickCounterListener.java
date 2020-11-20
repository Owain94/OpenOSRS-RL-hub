/*
 * Copyright (c) 2020, Robert Espinoza
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
package com.mouseclickcounter;

import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.input.MouseAdapter;

public class MouseClickCounterListener extends MouseAdapter
{
	private int leftClickCounter;
	private int rightClickCounter;
	private int middleClickCounter;
	private int totalClickCounter;
	private final Client client;

	MouseClickCounterListener(Client client)
	{
		this.leftClickCounter = 0;
		this.rightClickCounter = 0;
		this.middleClickCounter = 0;
		this.totalClickCounter = 0;
		this.client = client;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent event)
	{
		if(client.getGameState() == GameState.LOGGED_IN)
		{

			if(SwingUtilities.isLeftMouseButton(event))
			{
				this.leftClickCounter++;
				this.totalClickCounter++;
			}

			else if(SwingUtilities.isRightMouseButton(event))
			{
				this.rightClickCounter++;
				this.totalClickCounter++;
			}

			else if(SwingUtilities.isMiddleMouseButton(event))
			{
				this.middleClickCounter++;
				this.totalClickCounter++;
			}

		}
		return event;
	}

	public int getLeftClickCounter() { return this.leftClickCounter; }

	public int getRightClickCounter() { return this.rightClickCounter; }

	public int getMiddleClickCounter() { return this.middleClickCounter; }

	public int getTotalClickCounter() { return this.totalClickCounter; }

}
