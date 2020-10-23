/*
 * Copyright (c) 2020, MMagicala <https://github.com/MMagicala>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

package io.github.mmagicala.gnomeRestaurant.itemOrder;

import io.github.mmagicala.gnomeRestaurant.CookingItem;
import io.github.mmagicala.gnomeRestaurant.HeatTiming;
import io.github.mmagicala.gnomeRestaurant.MinigameStage;
import java.util.ArrayList;
import lombok.Getter;

// HBP: Heat before pouring
public class HeatedCocktailOrder extends CocktailOrder
{
	@Getter
	private HeatTiming heatTiming;

	@Getter
	private int pouredMixId, secondPouredMixId;

	public HeatedCocktailOrder(HeatTiming heatTiming, int shakerMixId, int pouredMixId, int secondPouredMixId, int itemId, ArrayList<CookingItem> ingredients)
	{
		super(shakerMixId, itemId, ingredients);

		this.heatTiming = heatTiming;
		this.pouredMixId = pouredMixId;
		this.secondPouredMixId = secondPouredMixId;
	}
}
