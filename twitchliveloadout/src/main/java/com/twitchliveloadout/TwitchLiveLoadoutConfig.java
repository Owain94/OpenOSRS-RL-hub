/*
 * Copyright (c) 2020, Pepijn Verburg <pepijn.verburg@gmail.com>
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
package com.twitchliveloadout;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Title;

@ConfigGroup("twitchstreamer")
public interface TwitchLiveLoadoutConfig extends Config
{
	@ConfigTitleSection(
		keyName = "syncTitle",
		name = "Sync",
		description = "",
		position = 0,
		titleSection = "syncEnabled"
	)
	default Title syncTitle()
	{
		return new Title();
	}
	
	@ConfigItem(
			keyName = "syncEnabled",
			name = "Sync enabled",
			description = "Toggle off to disable all syncing, hide extension to viewers and clear data.",
			position = 1,
		titleSection = "syncTitle"
	)
	default boolean syncEnabled()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "twitchTitle",
			name = "Twitch Extension",
			description = "Authentication and extension configuration.",
			position = 2
	)
	default Title twitchTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "twitchToken",
			name = "Twitch Extension Token",
			description = "Your token can be found when configuring the Twitch Extension.",
			secret = true,
			position = 2,
			titleSection = "twitchTitle"
	)
	default String twitchToken()
	{
		return "";
	}

	@ConfigItem(
			keyName = "overlayTopPosition",
			name = "Overlay top position",
			description = "The position of the viewer Twitch Extension overlay in % of the viewport height. '0' falls back to default of viewer.",
			position = 4,
			titleSection = "twitchTitle"
	)
	default int overlayTopPosition()
	{
		return 35;
	}

	@ConfigItem(
			keyName = "syncDelay",
			name = "Sync delay (seconds)",
			description = "The amount of seconds to delay the sending of data to match your stream delay.",
			position = 6,
			titleSection = "twitchTitle"
	)
	default int syncDelay()
	{
		return 0;
	}

	@ConfigTitleSection(
		keyName = "itemsTitle",
			name = "Items",
			description = "Syncing of items in inventory, equipment and bank.",
			position = 4
	)
	default Title itemsTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "inventoryEnabled",
			name = "Sync inventory items",
			description = "Synchronize all inventory items.",
			position = 2,
			titleSection = "itemsTitle"
	)
	default boolean inventoryEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "equipmentEnabled",
			name = "Sync equipment items",
			description = "Synchronize all equipment items.",
			position = 4,
			titleSection = "itemsTitle"
	)
	default boolean equipmentEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bankEnabled",
			name = "Sync bank items",
			description = "Synchronize bank value and top items based on GE value and configured maximum amount.",
			position = 6,
			titleSection = "itemsTitle"
	)
	default boolean bankEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "bankItemAmount",
			name = "Max bank items",
			description = "Maximum amount of items synced with fixed upper limit of "+ ItemStateManager.MAX_BANK_ITEMS +".",
			position = 10,
			titleSection = "itemsTitle"
	)
	default int bankItemAmount()
	{
		return ItemStateManager.MAX_BANK_ITEMS;
	}

	@ConfigTitleSection(
		keyName = "combatTitle",
			name = "Combat",
			description = "Syncing of weapon damage, smite drains, poison damage, etc. per enemy.",
			position = 6
	)
	default Title combatTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "fightStatisticsEnabled",
			name = "Sync combat statistics",
			description = "Synchronize statistics about PvM and PvP, such as DPS, freezes, splashes, etc.",
			position = 2,
			titleSection = "combatTitle"
	)
	default boolean fightStatisticsEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fightStatisticsSpellsEnabled",
			name = "Track magic spells",
			description = "Enable tracking of freezes, entangles, blood spells and splashes.",
			position = 4,
			titleSection = "combatTitle"
	)
	default boolean fightStatisticsSpellsEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fightStatisticsOthersEnabled",
			name = "Track damage by others",
			description = "Enable tracking of hitsplats of other players.",
			position = 6,
			titleSection = "combatTitle"
	)
	default boolean fightStatisticsOthersEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fightStatisticsUnattackedEnabled",
			name = "Track un-attacked enemies",
			description = "Enable tracking of hitsplats on enemies you have not attacked, recommended in team settings such as Cox and ToB.",
			position = 8,
			titleSection = "combatTitle"
	)
	default boolean fightStatisticsUnattackedEnabled()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fightStatisticsMaxFightAmount",
			name = "Max combat fights",
			description = "Maximum amount of tracked fights with fixed upper limit of "+ FightStateManager.MAX_FIGHT_AMOUNT +".",
			position = 10,
			titleSection = "combatTitle"
	)
	default int fightStatisticsMaxFightAmount()
	{
		return FightStateManager.MAX_FIGHT_AMOUNT;
	}

	@ConfigItem(
			keyName = "fightStatisticsExpiryTime",
			name = "Fight expiry time (minutes)",
			description = "Reset a fight after the configured minutes of inactivity.",
			position = 12,
			titleSection = "combatTitle"
	)
	default int fightStatisticsExpiryTime()
	{
		return 180;
	}

	@ConfigItem(
			keyName = "fightStatisticsAutoIdling",
			name = "Auto idling of fight timer",
			description = "Stop fight timer when logged out or enemy is not visible.",
			position = 14,
			titleSection = "combatTitle"
	)
	default boolean fightStatisticsAutoIdling()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "skillsTitle",
			name = "Skills",
			description = "Syncing of skill experience, virtual levels, etc.",
			position = 8
	)
	default Title skillsTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "skillsEnabled",
			name = "Sync skill levels",
			description = "Synchronize skill experience, level boosts and combat level.",
			position = 2,
			titleSection = "skillsTitle"
	)
	default boolean skillsEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "virtualLevelsEnabled",
			name = "Virtual levels",
			description = "Use maximum level of 126 instead of 99.",
			position = 4,
			titleSection = "skillsTitle"
	)
	default boolean virtualLevelsEnabled()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "gerneralTitle",
			name = "General info",
			description = "Syncing of display name, player weight, etc.",
			position = 10
	)
	default Title gerneralTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "playerInfoEnabled",
			name = "Sync display name",
			description = "Synchronize basic player info such as display name.",
			position = 2,
			titleSection = "gerneralTitle"
	)
	default boolean playerInfoEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "weightEnabled",
			name = "Sync weight of carried items",
			description = "Synchronize the weight of the equipment and inventory items, including weight reduction.",
			position = 4,
			titleSection = "gerneralTitle"
	)
	default boolean weightEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "itemGoalsEnabled",
			name = "Sync item goals",
			description = "Synchronize the configured item wanted items, progress is automatic from inventory, gear and bank items.",
			position = 14,
			hidden = true,
			titleSection = "itemsTitle"
	)
	default boolean itemGoalsEnabled()
	{
		return true;
	}

	@ConfigTitleSection(
		keyName = "advancedTitle",
			name = "Advanced",
			description = "Settings for advanced usage",
			position = 10
	)
	default Title advancedTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "extensionClientId",
			name = "Twitch Extension ID",
			description = "This is the ID of the Twitch Extension you want to sync the data to. Defaults to 'OSRS Live Loadout'.",
			position = 2,
			titleSection = "advancedTitle"
	)
	default String extensionClientId()
	{
		return TwitchApi.DEFAULT_EXTENSION_CLIENT_ID;
	}
}
