/*
 * Copyright (c) 2020, Hannah Ryan <HannahRyanster@gmail.com>
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

package net.runelite.client.plugins.emojiscape;

import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.util.ImageUtil;

@Slf4j
enum RSEmoji
{
	AGILITY("agility", "agi"),
	ATTACK("attack", "att"),
	COMBAT("combat", "cmb"),
	MELEE("melee", "melee"),
	CONSTRUCTION("construction", "con"),
	COOKING("cooking", "cook"),
	CRAFTING("crafting", "craft"),
	DEFENCE("defence", "def"),
	FARMING("farming", "farm"),
	FIREMAKING("firemaking", "fm"),
	FISHING("fishing", "fish"),
	FLETCHING("fletching", "fletch"),
	HERBLORE("herblore", "herb"),
	HITPOINTS("hitpoints", "hp"),
	HUNTER("hunter", "hunt"),
	MAGIC("magic", "mage"),
	MINING("mining", "mine"),
	PRAYER("prayer", "pray"),
	RANGED("ranged", "range"),
	RUNECRAFT("runecraft", "rc"),
	SLAYER("slayer", "slay"),
	SMITHING("smithing", "smith"),
	STRENGTH("strength", "str"),
	THIEVING("thieving", "thief"),
	WOODCUTTING("woodcutting", "wc"),
	AUGURY("augury", "augury"),
	CHIVALRY("chivalry", "chivalry"),
	PIETY("piety", "piety"),
	PRESERVE("preserve", "preserve"),
	REDEMPTION("redemption", "redemption"),
	RETRIBUTION("retribution", "retribution"),
	RIGOUR("rigour", "rigour"),
	SMITE("smite", "smite"),
	BANK("bank", "bank"),
	ALTAR("altar", "altar"),
	SHORTCUT("shortcut", "shortcut"),
	QUEST("quest", "qp"),
	DIARY("diary", "diary"),
	MINIGAME("minigame", "minigame"),
	FAVOUR("favour", "favour"),
	ARCEUUS("arceuus", "arc"),
	HOSIDIUS("hosidius", "hosi"),
	LOVAKENGJ("lovakengj", "lova"),
	PISCARILIUS("piscarilius", "pisc"),
	SHAYZIEN("shayzien", "shayz"),
	COINS("coins", "gp"),
	EXCHANGE("exchange", "ge"),
	IRONMAN("ironman", "im"),
	HARDCORE("hardcore", "hcim"),
	ULTIMATE("ultimate", "uim"),
	JMOD("jmod", "jmod"),
	PMOD("pmod", "pmod"),
	;

	private static final Map<String, RSEmoji> skillLongEmojiMap;
	private static final Map<String, RSEmoji> skillShortEmojiMap;

	private final String longTrigger;
	private final String shortTrigger;

	static
	{
		ImmutableMap.Builder<String, RSEmoji> builder = new ImmutableMap.Builder<>();

		for (final RSEmoji RSEmoji : values())
		{
			builder.put(RSEmoji.longTrigger, RSEmoji);
		}

		skillLongEmojiMap = builder.build();
	}

	static
	{
		ImmutableMap.Builder<String, RSEmoji> builder = new ImmutableMap.Builder<>();

		for (final RSEmoji RSEmoji : values())
		{
			builder.put(RSEmoji.shortTrigger, RSEmoji);
		}

		skillShortEmojiMap = builder.build();
	}

	RSEmoji(String longTrigger, String shortTrigger)
	{
		this.longTrigger = longTrigger;
		this.shortTrigger = shortTrigger;
	}

	BufferedImage loadImage()
	{
		return ImageUtil.getResourceStreamFromClass(EmojiScapePlugin.class, this.name().toLowerCase() + ".png");
	}

	public static RSEmoji getRSEmoji(String longTrigger)
	{
		return skillLongEmojiMap.get(longTrigger);
	}

	public static RSEmoji getShortRSEmoji(String shortTrigger)
	{
		return skillShortEmojiMap.get(shortTrigger);
	}

}