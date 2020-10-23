/*
 * Copyright (c) 2020, Truth Forger <https://github.com/Blackberry0Pie>
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
package bbp.trimmer;

import bbp.trimmer.configs.AbyssalWhipMode;
import bbp.trimmer.configs.DarkBowMode;
import bbp.trimmer.configs.GracefulMode;
import bbp.trimmer.configs.GraniteMaulMode;
import bbp.trimmer.configs.RuneArmourMode;
import bbp.trimmer.configs.RuneScimitarMode;
import com.google.common.collect.ImmutableMap;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ArmourTrimmerOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final ArmourTrimmerConfig config;
	private static final Map<Integer, Integer[]> ITEM_REMAP = new ImmutableMap.Builder<Integer, Integer[]>().
			put(ItemID.BRONZE_FULL_HELM, new Integer[] {ItemID.BRONZE_FULL_HELM_T, ItemID.BRONZE_FULL_HELM_G}).
			put(ItemID.BRONZE_PLATEBODY, new Integer[] {ItemID.BRONZE_PLATEBODY_T, ItemID.BRONZE_PLATEBODY_G}).
			put(ItemID.BRONZE_PLATELEGS, new Integer[] {ItemID.BRONZE_PLATELEGS_T, ItemID.BRONZE_PLATELEGS_G}).
			put(ItemID.BRONZE_PLATESKIRT, new Integer[] {ItemID.BRONZE_PLATESKIRT_T, ItemID.BRONZE_PLATESKIRT_G}).
			put(ItemID.BRONZE_KITESHIELD, new Integer[] {ItemID.BRONZE_KITESHIELD_T, ItemID.BRONZE_KITESHIELD_G}).

			put(ItemID.IRON_FULL_HELM, new Integer[] {ItemID.IRON_FULL_HELM_T, ItemID.IRON_FULL_HELM_G}).
			put(ItemID.IRON_PLATEBODY, new Integer[] {ItemID.IRON_PLATEBODY_T, ItemID.IRON_PLATEBODY_G}).
			put(ItemID.IRON_PLATELEGS, new Integer[] {ItemID.IRON_PLATELEGS_T, ItemID.IRON_PLATELEGS_G}).
			put(ItemID.IRON_PLATESKIRT, new Integer[] {ItemID.IRON_PLATESKIRT_T, ItemID.IRON_PLATESKIRT_G}).
			put(ItemID.IRON_KITESHIELD, new Integer[] {ItemID.IRON_KITESHIELD_T, ItemID.IRON_KITESHIELD_G}).

			put(ItemID.STEEL_FULL_HELM, new Integer[] {ItemID.STEEL_FULL_HELM_T, ItemID.STEEL_FULL_HELM_G}).
			put(ItemID.STEEL_PLATEBODY, new Integer[] {ItemID.STEEL_PLATEBODY_T, ItemID.STEEL_PLATEBODY_G}).
			put(ItemID.STEEL_PLATELEGS, new Integer[] {ItemID.STEEL_PLATELEGS_T, ItemID.STEEL_PLATELEGS_G}).
			put(ItemID.STEEL_PLATESKIRT, new Integer[] {ItemID.STEEL_PLATESKIRT_T, ItemID.STEEL_PLATESKIRT_G}).
			put(ItemID.STEEL_KITESHIELD, new Integer[] {ItemID.STEEL_KITESHIELD_T, ItemID.STEEL_KITESHIELD_G}).

			put(ItemID.BLACK_FULL_HELM, new Integer[] {ItemID.BLACK_FULL_HELM_T, ItemID.BLACK_FULL_HELM_G}).
			put(ItemID.BLACK_PLATEBODY, new Integer[] {ItemID.BLACK_PLATEBODY_T, ItemID.BLACK_PLATEBODY_G}).
			put(ItemID.BLACK_PLATELEGS, new Integer[] {ItemID.BLACK_PLATELEGS_T, ItemID.BLACK_PLATELEGS_G}).
			put(ItemID.BLACK_PLATESKIRT, new Integer[] {ItemID.BLACK_PLATESKIRT_T, ItemID.BLACK_PLATESKIRT_G}).
			put(ItemID.BLACK_KITESHIELD, new Integer[] {ItemID.BLACK_KITESHIELD_T, ItemID.BLACK_KITESHIELD_G}).

			put(ItemID.MITHRIL_FULL_HELM, new Integer[] {ItemID.MITHRIL_FULL_HELM_T, ItemID.MITHRIL_FULL_HELM_G}).
			put(ItemID.MITHRIL_PLATEBODY, new Integer[] {ItemID.MITHRIL_PLATEBODY_T, ItemID.MITHRIL_PLATEBODY_G}).
			put(ItemID.MITHRIL_PLATELEGS, new Integer[] {ItemID.MITHRIL_PLATELEGS_T, ItemID.MITHRIL_PLATELEGS_G}).
			put(ItemID.MITHRIL_PLATESKIRT, new Integer[] {ItemID.MITHRIL_PLATESKIRT_T, ItemID.MITHRIL_PLATESKIRT_G}).
			put(ItemID.MITHRIL_KITESHIELD, new Integer[] {ItemID.MITHRIL_KITESHIELD_T, ItemID.MITHRIL_KITESHIELD_G}).

			put(ItemID.ADAMANT_FULL_HELM, new Integer[] {ItemID.ADAMANT_FULL_HELM_T, ItemID.ADAMANT_FULL_HELM_G}).
			put(ItemID.ADAMANT_PLATEBODY, new Integer[] {ItemID.ADAMANT_PLATEBODY_T, ItemID.ADAMANT_PLATEBODY_G}).
			put(ItemID.ADAMANT_PLATELEGS, new Integer[] {ItemID.ADAMANT_PLATELEGS_T, ItemID.ADAMANT_PLATELEGS_G}).
			put(ItemID.ADAMANT_PLATESKIRT, new Integer[] {ItemID.ADAMANT_PLATESKIRT_T, ItemID.ADAMANT_PLATESKIRT_G}).
			put(ItemID.ADAMANT_KITESHIELD, new Integer[] {ItemID.ADAMANT_KITESHIELD_T, ItemID.ADAMANT_KITESHIELD_G}).

			put(ItemID.BLUE_WIZARD_HAT, new Integer[] {ItemID.BLUE_WIZARD_HAT_T, ItemID.BLUE_WIZARD_HAT_G}).
			put(ItemID.BLUE_WIZARD_ROBE, new Integer[] {ItemID.BLUE_WIZARD_ROBE_T, ItemID.BLUE_WIZARD_ROBE_G}).
			put(ItemID.BLUE_SKIRT, new Integer[] {ItemID.BLUE_SKIRT_T, ItemID.BLUE_SKIRT_G}).

			put(ItemID.WIZARD_HAT, new Integer[] {ItemID.BLACK_WIZARD_HAT_T, ItemID.BLACK_WIZARD_HAT_G}).
			put(ItemID.BLACK_ROBE, new Integer[] {ItemID.BLACK_WIZARD_ROBE_T, ItemID.BLACK_WIZARD_ROBE_G}).
			put(ItemID.BLACK_SKIRT, new Integer[] {ItemID.BLACK_SKIRT_T, ItemID.BLACK_SKIRT_G}).

			put(ItemID.LEATHER_BODY, new Integer[] {null, ItemID.LEATHER_BODY_G}).
			put(ItemID.LEATHER_CHAPS, new Integer[] {null, ItemID.LEATHER_CHAPS_G}).

			put(ItemID.STUDDED_BODY, new Integer[] {ItemID.STUDDED_BODY_T, ItemID.STUDDED_BODY_G}).
			put(ItemID.STUDDED_CHAPS, new Integer[] {ItemID.STUDDED_CHAPS_T, ItemID.STUDDED_CHAPS_G}).

			put(ItemID.GREEN_DHIDE_BODY, new Integer[] {ItemID.GREEN_DHIDE_BODY_T, ItemID.GREEN_DHIDE_BODY_G}).
			put(ItemID.GREEN_DHIDE_CHAPS, new Integer[] {ItemID.GREEN_DHIDE_CHAPS_T, ItemID.GREEN_DHIDE_CHAPS_G}).

			put(ItemID.BLUE_DHIDE_BODY, new Integer[] {ItemID.BLUE_DHIDE_BODY_T, ItemID.BLUE_DHIDE_BODY_G}).
			put(ItemID.BLUE_DHIDE_CHAPS, new Integer[] {ItemID.BLUE_DHIDE_CHAPS_T, ItemID.BLUE_DHIDE_CHAPS_G}).

			put(ItemID.RED_DHIDE_BODY, new Integer[] {ItemID.RED_DHIDE_BODY_T, ItemID.RED_DHIDE_BODY_G}).
			put(ItemID.RED_DHIDE_CHAPS, new Integer[] {ItemID.RED_DHIDE_CHAPS_T, ItemID.RED_DHIDE_CHAPS_G}).

			put(ItemID.BLACK_DHIDE_BODY, new Integer[] {ItemID.BLACK_DHIDE_BODY_T, ItemID.BLACK_DHIDE_BODY_G}).
			put(ItemID.BLACK_DHIDE_CHAPS, new Integer[] {ItemID.BLACK_DHIDE_CHAPS_T, ItemID.BLACK_DHIDE_CHAPS_G}).

			put(ItemID.MONKS_ROBE_TOP, new Integer[] {ItemID.MONKS_ROBE_TOP_T, ItemID.MONKS_ROBE_TOP_G}).
			put(ItemID.MONKS_ROBE, new Integer[] {ItemID.MONKS_ROBE_T, ItemID.MONKS_ROBE_G}).

			put(ItemID.CLIMBING_BOOTS, new Integer[] {null, ItemID.CLIMBING_BOOTS_G}).

			put(ItemID.WOODEN_SHIELD, new Integer[] {null, ItemID.WOODEN_SHIELD_G}).

			//these icons do not overlap the default icons well
//			put(ItemID.AMULET_OF_MAGIC, new Integer[] {null, ItemID.AMULET_OF_MAGIC_T}).
//			put(ItemID.AMULET_OF_DEFENCE, new Integer[] {null, ItemID.AMULET_OF_DEFENCE_T}).
//			put(ItemID.AMULET_OF_STRENGTH, new Integer[] {null, ItemID.STRENGTH_AMULET_T}).
//			put(ItemID.AMULET_OF_POWER, new Integer[] {null, ItemID.AMULET_OF_POWER_T}).
//
//			put(ItemID.AMULET_OF_GLORY, new Integer[] {null, ItemID.AMULET_OF_GLORY_T}).
//			put(ItemID.AMULET_OF_GLORY1, new Integer[] {null, ItemID.AMULET_OF_GLORY_T1}).
//			put(ItemID.AMULET_OF_GLORY2, new Integer[] {null, ItemID.AMULET_OF_GLORY_T2}).
//			put(ItemID.AMULET_OF_GLORY3, new Integer[] {null, ItemID.AMULET_OF_GLORY_T3}).
//			put(ItemID.AMULET_OF_GLORY4, new Integer[] {null, ItemID.AMULET_OF_GLORY_T4}).
//			put(ItemID.AMULET_OF_GLORY5, new Integer[] {null, ItemID.AMULET_OF_GLORY_T5}).
//			put(ItemID.AMULET_OF_GLORY6, new Integer[] {null, ItemID.AMULET_OF_GLORY_T6}).

			put(ItemID.FIRE_CAPE, new Integer[] {null, ItemID.INFERNAL_CAPE}).

			put(ItemID.DRAGON_FULL_HELM, new Integer[] {null, ItemID.DRAGON_FULL_HELM_G}).
			put(ItemID.DRAGON_CHAINBODY, new Integer[] {null, ItemID.DRAGON_CHAINBODY_G}).
			put(ItemID.DRAGON_PLATEBODY, new Integer[] {null, ItemID.DRAGON_PLATEBODY_G}).
			put(ItemID.DRAGON_PLATELEGS, new Integer[] {null, ItemID.DRAGON_PLATELEGS_G}).
			put(ItemID.DRAGON_PLATESKIRT, new Integer[] {null, ItemID.DRAGON_PLATESKIRT_G}).
			put(ItemID.DRAGON_SQ_SHIELD, new Integer[] {null, ItemID.DRAGON_SQ_SHIELD_G}).
			put(ItemID.DRAGON_KITESHIELD, new Integer[] {null, ItemID.DRAGON_KITESHIELD_G}).

			put(ItemID.DRAGON_SCIMITAR, new Integer[] {null, ItemID.DRAGON_SCIMITAR_OR}).
			put(ItemID.DRAGON_DEFENDER, new Integer[] {null, ItemID.DRAGON_DEFENDER_T}).
			put(ItemID.DRAGON_BOOTS, new Integer[] {null, ItemID.DRAGON_BOOTS_G}).

			put(ItemID.ARMADYL_GODSWORD, new Integer[] {null, ItemID.ARMADYL_GODSWORD_OR}).
			put(ItemID.BANDOS_GODSWORD, new Integer[] {null, ItemID.BANDOS_GODSWORD_OR}).
			put(ItemID.SARADOMIN_GODSWORD, new Integer[] {null, ItemID.SARADOMIN_GODSWORD_OR}).
			put(ItemID.ZAMORAK_GODSWORD, new Integer[] {null, ItemID.ZAMORAK_GODSWORD_OR}).

			put(ItemID.INFINITY_HAT, new Integer[] {ItemID.DARK_INFINITY_HAT, ItemID.LIGHT_INFINITY_HAT}).
			put(ItemID.INFINITY_TOP, new Integer[] {ItemID.DARK_INFINITY_TOP, ItemID.LIGHT_INFINITY_TOP}).
			put(ItemID.INFINITY_BOTTOMS, new Integer[] {ItemID.DARK_INFINITY_BOTTOMS, ItemID.LIGHT_INFINITY_BOTTOMS}).

			put(ItemID.ANCESTRAL_HAT, new Integer[] {null, ItemID.TWISTED_ANCESTRAL_HAT}).
			put(ItemID.ANCESTRAL_ROBE_TOP, new Integer[] {null, ItemID.TWISTED_ANCESTRAL_ROBE_TOP}).
			put(ItemID.ANCESTRAL_ROBE_BOTTOM, new Integer[] {null, ItemID.TWISTED_ANCESTRAL_ROBE_BOTTOM}).

			put(ItemID.TZHAARKETOM, new Integer[] {null, ItemID.TZHAARKETOM_T}).

			//these icons do not overlap the default icons well
//			put(ItemID.BERSERKER_NECKLACE, new Integer[] {null, ItemID.BERSERKER_NECKLACE_OR}).
//			put(ItemID.TORMENTED_BRACELET, new Integer[] {null, ItemID.TORMENTED_BRACELET_OR}).
//			put(ItemID.OCCULT_NECKLACE, new Integer[] {null, ItemID.OCCULT_NECKLACE_OR}).

			put(ItemID.AMULET_OF_FURY, new Integer[] {null, ItemID.AMULET_OF_FURY_OR}).
			put(ItemID.AMULET_OF_TORTURE, new Integer[] {null, ItemID.AMULET_OF_TORTURE_OR}).
			put(ItemID.NECKLACE_OF_ANGUISH, new Integer[] {null, ItemID.NECKLACE_OF_ANGUISH_OR}).

			//these icons do not overlap the default icons well
//			put(ItemID.STEAM_BATTLESTAFF, new Integer[] {null, ItemID.STEAM_BATTLESTAFF_12795}).
//			put(ItemID.LAVA_BATTLESTAFF, new Integer[] {null, ItemID.LAVA_BATTLESTAFF_21198}).
//			put(ItemID.MYSTIC_STEAM_STAFF, new Integer[] {null, ItemID.MYSTIC_STEAM_STAFF_12796}).
//			put(ItemID.MYSTIC_LAVA_STAFF, new Integer[] {null, ItemID.MYSTIC_LAVA_STAFF_21200}).

			//these icons do not overlap the default icons well
//			put(ItemID.ODIUM_WARD, new Integer[] {null, ItemID.ODIUM_WARD_12807}).
//			put(ItemID.MALEDICTION_WARD, new Integer[] {null, ItemID.MALEDICTION_WARD_12806}).

			put(ItemID.RUNE_DEFENDER, new Integer[] {null, ItemID.RUNE_DEFENDER_T}).
			put(ItemID.RUNE_BOOTS, new Integer[] {null, ItemID.GILDED_BOOTS}).

			build();

	private static final Map<Integer, Integer[]> GRACEFUL_REMAP = new ImmutableMap.Builder<Integer, Integer[]>().
			put(ItemID.GRACEFUL_HOOD, new Integer[] {
					ItemID.GRACEFUL_HOOD_13579,
					ItemID.GRACEFUL_HOOD_13591,
					ItemID.GRACEFUL_HOOD_13603,
					ItemID.GRACEFUL_HOOD_13615,
					ItemID.GRACEFUL_HOOD_13627,
					ItemID.GRACEFUL_HOOD_13667,
					ItemID.GRACEFUL_HOOD_21061,
					ItemID.GRACEFUL_HOOD_24743}).
			put(ItemID.GRACEFUL_TOP, new Integer[] {
					ItemID.GRACEFUL_TOP_13583,
					ItemID.GRACEFUL_TOP_13595,
					ItemID.GRACEFUL_TOP_13607,
					ItemID.GRACEFUL_TOP_13619,
					ItemID.GRACEFUL_TOP_13631,
					ItemID.GRACEFUL_TOP_13671,
					ItemID.GRACEFUL_TOP_21067,
					ItemID.GRACEFUL_TOP_24749}).
			put(ItemID.GRACEFUL_LEGS, new Integer[] {
					ItemID.GRACEFUL_LEGS_13585,
					ItemID.GRACEFUL_LEGS_13597,
					ItemID.GRACEFUL_LEGS_13609,
					ItemID.GRACEFUL_LEGS_13621,
					ItemID.GRACEFUL_LEGS_13633,
					ItemID.GRACEFUL_LEGS_13673,
					ItemID.GRACEFUL_LEGS_21070,
					ItemID.GRACEFUL_LEGS_24752}).
			put(ItemID.GRACEFUL_BOOTS, new Integer[] {
					ItemID.GRACEFUL_BOOTS_13589,
					ItemID.GRACEFUL_BOOTS_13601,
					ItemID.GRACEFUL_BOOTS_13613,
					ItemID.GRACEFUL_BOOTS_13625,
					ItemID.GRACEFUL_BOOTS_13637,
					ItemID.GRACEFUL_BOOTS_13677,
					ItemID.GRACEFUL_BOOTS_21076,
					ItemID.GRACEFUL_BOOTS_24758}).
			put(ItemID.GRACEFUL_CAPE, new Integer[] {
					ItemID.GRACEFUL_CAPE_13581,
					ItemID.GRACEFUL_CAPE_13593,
					ItemID.GRACEFUL_CAPE_13605,
					ItemID.GRACEFUL_CAPE_13617,
					ItemID.GRACEFUL_CAPE_13629,
					ItemID.GRACEFUL_CAPE_13669,
					ItemID.GRACEFUL_CAPE_21064,
					ItemID.GRACEFUL_CAPE_24746}).
			put(ItemID.GRACEFUL_GLOVES, new Integer[] {
					ItemID.GRACEFUL_GLOVES_13587,
					ItemID.GRACEFUL_GLOVES_13599,
					ItemID.GRACEFUL_GLOVES_13611,
					ItemID.GRACEFUL_GLOVES_13623,
					ItemID.GRACEFUL_GLOVES_13635,
					ItemID.GRACEFUL_GLOVES_13675,
					ItemID.GRACEFUL_GLOVES_21073,
					ItemID.GRACEFUL_GLOVES_24755}).
			build();

	//these icons do not overlap the default icons well
//	private static final int[] DRAGON_PICKAXE_REMAP = {
//			ItemID.DRAGON_PICKAXE_12797,
//			ItemID.DRAGON_PICKAXEOR,
//			ItemID.INFERNAL_PICKAXE,
//			ItemID.CRYSTAL_PICKAXE};

	private static final int[] DARK_BOW_REMAP = {
			ItemID.DARK_BOW_12765,
			ItemID.DARK_BOW_12766,
			ItemID.DARK_BOW_12767,
			ItemID.DARK_BOW_12768};

	private static final int[] ABYSSAL_WHIP_REMAP = {
			ItemID.VOLCANIC_ABYSSAL_WHIP,
			ItemID.FROZEN_ABYSSAL_WHIP,
			ItemID.ABYSSAL_TENTACLE};

	private static final Map<Integer, Integer[]> RUNE_ARMOUR_REMAP = new ImmutableMap.Builder<Integer, Integer[]>().
			put(ItemID.RUNE_FULL_HELM, new Integer[] {
					ItemID.RUNE_FULL_HELM_T,
					ItemID.RUNE_FULL_HELM_G,
					ItemID.GILDED_FULL_HELM,
					ItemID.ANCIENT_FULL_HELM,
					ItemID.ARMADYL_FULL_HELM,
					ItemID.BANDOS_FULL_HELM,
					ItemID.GUTHIX_FULL_HELM,
					ItemID.SARADOMIN_FULL_HELM,
					ItemID.ZAMORAK_FULL_HELM}).
			put(ItemID.RUNE_PLATEBODY, new Integer[] {
					ItemID.RUNE_PLATEBODY_T,
					ItemID.RUNE_PLATEBODY_G,
					ItemID.GILDED_PLATEBODY,
					ItemID.ANCIENT_PLATEBODY,
					ItemID.ARMADYL_PLATEBODY,
					ItemID.BANDOS_PLATEBODY,
					ItemID.GUTHIX_PLATEBODY,
					ItemID.SARADOMIN_PLATEBODY,
					ItemID.ZAMORAK_PLATEBODY}).
			put(ItemID.RUNE_PLATELEGS, new Integer[] {
					ItemID.RUNE_PLATELEGS_T,
					ItemID.RUNE_PLATELEGS_G,
					ItemID.GILDED_PLATELEGS,
					ItemID.ANCIENT_PLATELEGS,
					ItemID.ARMADYL_PLATELEGS,
					ItemID.BANDOS_PLATELEGS,
					ItemID.GUTHIX_PLATELEGS,
					ItemID.SARADOMIN_PLATELEGS,
					ItemID.ZAMORAK_PLATELEGS}).
			put(ItemID.RUNE_PLATESKIRT, new Integer[] {
					ItemID.RUNE_PLATESKIRT_T,
					ItemID.RUNE_PLATESKIRT_G,
					ItemID.GILDED_PLATESKIRT,
					ItemID.ANCIENT_PLATESKIRT,
					ItemID.ARMADYL_PLATESKIRT,
					ItemID.BANDOS_PLATESKIRT,
					ItemID.GUTHIX_PLATESKIRT,
					ItemID.SARADOMIN_PLATESKIRT,
					ItemID.ZAMORAK_PLATESKIRT}).
			put(ItemID.RUNE_KITESHIELD, new Integer[] {
					ItemID.RUNE_KITESHIELD_T,
					ItemID.RUNE_KITESHIELD_G,
					ItemID.GILDED_KITESHIELD,
					ItemID.ANCIENT_KITESHIELD,
					ItemID.ARMADYL_KITESHIELD,
					ItemID.BANDOS_KITESHIELD,
					ItemID.GUTHIX_KITESHIELD,
					ItemID.SARADOMIN_KITESHIELD,
					ItemID.ZAMORAK_KITESHIELD}).
			build();

	private static final int[] GRANITE_MAUL_REMAP = {
			ItemID.GRANITE_MAUL_12848,
			ItemID.GRANITE_MAUL_24225,
			ItemID.GRANITE_MAUL_24227};

	private static final int[] RUNE_SCIMITAR_REMAP = {
			ItemID.GILDED_SCIMITAR,
			ItemID.RUNE_SCIMITAR_23330,
			ItemID.RUNE_SCIMITAR_23332,
			ItemID.RUNE_SCIMITAR_23334};

	@Inject
	private ArmourTrimmerOverlay(ItemManager itemManager, ArmourTrimmerConfig config)
	{
		this.itemManager = itemManager;
		this.config = config;
		showOnEquipment();
		showOnInventory();
		showOnBank();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		int idx = 1;
		BufferedImage replacement = null;
		if (ITEM_REMAP.containsKey(itemId)) {
			if (config.trimGold())
			{
				replacement = itemManager.getImage(ITEM_REMAP.get(itemId)[idx]);
			}
			else if (ITEM_REMAP.get(itemId)[0] != null)
			{
				idx = 0;
				replacement = itemManager.getImage(ITEM_REMAP.get(itemId)[idx]);
			}
		}
		else if (GRACEFUL_REMAP.containsKey(itemId) && config.gracefulMode() != GracefulMode.NONE)
		{
			idx = config.gracefulMode().ordinal();
			replacement = itemManager.getImage(GRACEFUL_REMAP.get(itemId)[idx]);
		}
		//these icons do not overlap the default icons well
//		else if (itemId == ItemID.DRAGON_PICKAXE && config.dragonPickaxeMode() != DragonPickaxeMode.NONE)
//		{
//			idx = config.dragonPickaxeMode().ordinal();
//			replacement = itemManager.getImage(DRAGON_PICKAXE_REMAP[idx]);
//		}
		else if (itemId == ItemID.DARK_BOW && config.darkBowMode() != DarkBowMode.NONE)
		{
			idx = config.darkBowMode().ordinal();
			replacement = itemManager.getImage(DARK_BOW_REMAP[idx]);
		}
		else if (itemId == ItemID.ABYSSAL_WHIP && config.abyssalWhipMode() != AbyssalWhipMode.NONE)
		{
			idx = config.abyssalWhipMode().ordinal();
			replacement = itemManager.getImage(ABYSSAL_WHIP_REMAP[idx]);
		}
		else if (RUNE_ARMOUR_REMAP.containsKey(itemId) && config.runeArmourMode() != RuneArmourMode.NONE)
		{
			idx = config.runeArmourMode().ordinal();
			replacement = itemManager.getImage(RUNE_ARMOUR_REMAP.get(itemId)[idx]);
		}
		else if (itemId == ItemID.GRANITE_MAUL && config.graniteMaulMode() != GraniteMaulMode.NONE)
		{
			idx = config.graniteMaulMode().ordinal();
			replacement = itemManager.getImage(GRANITE_MAUL_REMAP[idx]);
		}
		else if (itemId == ItemID.RUNE_SCIMITAR && config.runeScimitarMode() != RuneScimitarMode.NONE)
		{
			idx = config.runeScimitarMode().ordinal();
			replacement = itemManager.getImage(RUNE_SCIMITAR_REMAP[idx]);
		}

		if (replacement != null) {
			Rectangle bounds = itemWidget.getCanvasBounds();
			graphics.drawImage(replacement, (int) bounds.getX(), (int) bounds.getY(), null);
		}
	}
}
