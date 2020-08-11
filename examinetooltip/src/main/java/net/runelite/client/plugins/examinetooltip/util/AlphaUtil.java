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
package net.runelite.client.plugins.examinetooltip.util;

import java.awt.Color;

public class AlphaUtil
{
	/**
	 * Applies the given alpha modifier to the transparency of the given color.
	 *
	 * @param color         The color to get the alpha modified version of.
	 * @param alphaModifier The alpha modifier.
	 * @return The alpha modified color or the passed Color object if alphaModifier == 1.0.
	 */
	public static Color getAlphaModdedColor(Color color, double alphaModifier)
	{
		if (alphaModifier == 1.0)
		{
			return color;
		}
		else
		{
			int newAlpha = (int) (color.getAlpha() * alphaModifier);
			// Clamp value to 0 - 255
			newAlpha = Math.max(0, Math.min(newAlpha, 255));

			return new Color(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				newAlpha);
		}
	}
}
