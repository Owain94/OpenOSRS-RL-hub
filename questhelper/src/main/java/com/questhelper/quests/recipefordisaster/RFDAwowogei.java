/*
 * Copyright (c) 2020, Zoinkwiz
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
package com.questhelper.quests.recipefordisaster;

import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.QuestVarbits;
import com.questhelper.Zone;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.PrayerRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.ItemRequirementCondition;
import com.questhelper.steps.conditional.LogicType;
import com.questhelper.steps.conditional.Operation;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Prayer;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR
)
public class RFDAwowogei extends BasicQuestHelper
{
	ItemRequirement cookedSnake, cookedSnakeHighlighted, mAmulet, gorillaGreegree, ninjaGreegree, zombieGreegree, bananaHighlighted, monkeyNutsHighlighted, ropeHighlighted,
		knife, pestleAndMortar, tchikiNuts, tchikiNutsHighlighted, redBanana, redBananaHighlighted, snakeCorpse, snakeCorpseHighlighted,
		rawStuffedSnake, rawStuffedSnakeHighlighted, slicedBanana, greegreeEquipped, paste, combatGear;
	
	Requirement protectMelee;

	ConditionForStep inDiningRoom, askedAboutBanana, askedAboutNut, onCrashIsland, inSnakeHole, inNutHole, inTempleDungeon, hasRawStuffedSnake,
		hasPaste, hasSnakeCorpse, hasSlicedRedBanana, hasRedBanana, hasTchikiNut, inCookRoom, hasCookedsnake;

	QuestStep enterDiningRoom, inspectAwowogei, talkToAwowogei, talkToWiseMonkeys, useBananaOnWiseMonkeys, useNutsOnWiseMonkeys, goToCrashIsland,
		enterCrashHole, killSnake, leaveSnakeHole, returnToApeAtoll, useRopeOnTree, enterNutHole, takeNuts, grindNuts, sliceBanana, stuffSnake,
		enterZombieDungeon, enterCookingHole, cookSnake, enterDiningRoomAgain, useSnakeOnAwowogei;

	Zone diningRoom, crashIsland, snakeHole, nutHole, templeDungeon, cookRoom;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();

		ConditionalStep goInspectSkrach = new ConditionalStep(this, enterDiningRoom);
		goInspectSkrach.addStep(inDiningRoom, inspectAwowogei);
		steps.put(0, goInspectSkrach);

		steps.put(5, talkToAwowogei);

		steps.put(10, talkToWiseMonkeys);

		ConditionalStep prepareMeal = new ConditionalStep(this, useBananaOnWiseMonkeys);
		prepareMeal.addStep(new Conditions(new Conditions(inDiningRoom, hasCookedsnake)), useSnakeOnAwowogei);
		prepareMeal.addStep(new Conditions(hasCookedsnake), enterDiningRoomAgain);
		prepareMeal.addStep(new Conditions(hasRawStuffedSnake, inCookRoom), cookSnake);
		prepareMeal.addStep(new Conditions(hasRawStuffedSnake, inTempleDungeon), enterCookingHole);
		prepareMeal.addStep(new Conditions(hasRawStuffedSnake), enterZombieDungeon);
		prepareMeal.addStep(new Conditions(hasSnakeCorpse, hasSlicedRedBanana, hasPaste), stuffSnake);
		prepareMeal.addStep(new Conditions(hasSnakeCorpse, hasRedBanana, hasPaste), sliceBanana);
		prepareMeal.addStep(new Conditions(hasSnakeCorpse, hasRedBanana, hasTchikiNut), grindNuts);
		prepareMeal.addStep(new Conditions(askedAboutNut, hasSnakeCorpse, hasRedBanana, inNutHole), takeNuts);
		prepareMeal.addStep(new Conditions(askedAboutNut, hasSnakeCorpse, hasRedBanana), enterNutHole);
		prepareMeal.addStep(new Conditions(askedAboutBanana, askedAboutNut, onCrashIsland, hasSnakeCorpse), returnToApeAtoll);
		prepareMeal.addStep(new Conditions(askedAboutBanana, askedAboutNut, inSnakeHole, hasSnakeCorpse), leaveSnakeHole);
		prepareMeal.addStep(new Conditions(askedAboutBanana, askedAboutNut, hasSnakeCorpse), useRopeOnTree);
		prepareMeal.addStep(new Conditions(askedAboutBanana, askedAboutNut, inSnakeHole), killSnake);
		prepareMeal.addStep(new Conditions(askedAboutBanana, askedAboutNut, onCrashIsland), enterCrashHole);
		prepareMeal.addStep(new Conditions(askedAboutBanana, askedAboutNut), goToCrashIsland);
		prepareMeal.addStep(askedAboutBanana, useNutsOnWiseMonkeys);
		steps.put(15, prepareMeal);
		steps.put(20, prepareMeal);
		steps.put(30, prepareMeal);
		steps.put(40, prepareMeal);
		return steps;
	}

	public void setupRequirements()
	{
		cookedSnake = new ItemRequirement("Stuffed snake", ItemID.STUFFED_SNAKE);
		cookedSnakeHighlighted = new ItemRequirement("Stuffed snake", ItemID.STUFFED_SNAKE);
		cookedSnakeHighlighted.setHighlightInInventory(true);

		mAmulet = new ItemRequirement("M'speak amulet", ItemID.MSPEAK_AMULET, 1, true);
		gorillaGreegree = new ItemRequirement("Gorilla greegree", ItemID.GORILLA_GREEGREE, 1, true);
		gorillaGreegree.addAlternates(ItemID.ANCIENT_GORILLA_GREEGREE, ItemID.BEARDED_GORILLA_GREEGREE);
		gorillaGreegree.setTip("Kill a gorilla in the monkey temple for their bones, and make a greegree from them");
		ninjaGreegree = new ItemRequirement("Ninja greegree", ItemID.NINJA_MONKEY_GREEGREE, 1, true);
		ninjaGreegree.setTip("Kill a monkey archer in the monkey market for its bones and make a greegree from them");
		ninjaGreegree.addAlternates(ItemID.NINJA_MONKEY_GREEGREE_4025, ItemID.KRUK_MONKEY_GREEGREE);
		zombieGreegree = new ItemRequirement("Zombie greegree", ItemID.ZOMBIE_MONKEY_GREEGREE, 1, true);
		zombieGreegree.setTip("Kill a zombie monkey under the monkey temple and make a greegree from them");
		zombieGreegree.addAlternates(ItemID.ZOMBIE_MONKEY_GREEGREE_4030);
		greegreeEquipped = new ItemRequirement("Any greegree", ItemID.GORILLA_GREEGREE, 1, true);
		greegreeEquipped.addAlternates(ItemID.ANCIENT_GORILLA_GREEGREE, ItemID.BEARDED_GORILLA_GREEGREE, ItemID.KARAMJAN_MONKEY_GREEGREE, ItemID.KRUK_MONKEY_GREEGREE,
			ItemID.NINJA_MONKEY_GREEGREE, ItemID.ZOMBIE_MONKEY_GREEGREE, ItemID.ZOMBIE_MONKEY_GREEGREE_4030, ItemID.NINJA_MONKEY_GREEGREE_4025);
		bananaHighlighted = new ItemRequirement("Banana", ItemID.BANANA);
		bananaHighlighted.setHighlightInInventory(true);
		monkeyNutsHighlighted = new ItemRequirement("Monkey nuts", ItemID.MONKEY_NUTS);
		monkeyNutsHighlighted.setHighlightInInventory(true);
		monkeyNutsHighlighted.setTip("You can buy some from Solihib in the monkey market");
		ropeHighlighted = new ItemRequirement("Rope", ItemID.ROPE);
		ropeHighlighted.setHighlightInInventory(true);
		knife = new ItemRequirement("Knife", ItemID.KNIFE);
		knife.setHighlightInInventory(true);
		pestleAndMortar = new ItemRequirement("Pestle and mortar", ItemID.PESTLE_AND_MORTAR);
		pestleAndMortar.setHighlightInInventory(true);

		tchikiNuts = new ItemRequirement("Tchiki monkey nuts", ItemID.TCHIKI_MONKEY_NUTS);
		tchikiNutsHighlighted = new ItemRequirement("Tchiki monkey nuts", ItemID.TCHIKI_MONKEY_NUTS);
		tchikiNutsHighlighted.setHighlightInInventory(true);
		redBanana = new ItemRequirement("Red banana", ItemID.RED_BANANA);
		redBananaHighlighted = new ItemRequirement("Red banana", ItemID.RED_BANANA);
		redBananaHighlighted.setHighlightInInventory(true);
		snakeCorpse = new ItemRequirement("Snake corpse", ItemID.SNAKE_CORPSE);
		snakeCorpseHighlighted = new ItemRequirement("Snake corpse", ItemID.SNAKE_CORPSE);
		snakeCorpseHighlighted.setHighlightInInventory(true);

		rawStuffedSnake = new ItemRequirement("Raw stuffed snake", ItemID.RAW_STUFFED_SNAKE);
		rawStuffedSnakeHighlighted = new ItemRequirement("Raw stuffed snake", ItemID.RAW_STUFFED_SNAKE);
		rawStuffedSnakeHighlighted.setHighlightInInventory(true);

		slicedBanana = new ItemRequirement("Sliced red banana", ItemID.SLICED_RED_BANANA);
		paste = new ItemRequirement("Tchiki nut paste", ItemID.TCHIKI_NUT_PASTE);
		paste.setHighlightInInventory(true);

		combatGear = new ItemRequirement("Combat gear", -1, -1);

		protectMelee = new PrayerRequirement("Protect from Melee", Prayer.PROTECT_FROM_MELEE);
	}

	public void loadZones()
	{
		diningRoom = new Zone(new WorldPoint(1856, 5313, 0), new WorldPoint(1870, 5333, 0));
		crashIsland = new Zone(new WorldPoint(2883, 2693, 0), new WorldPoint(2941, 2747, 0));
		snakeHole = new Zone(new WorldPoint(3019, 5484, 0), new WorldPoint(3028, 5492, 0));
		nutHole = new Zone(new WorldPoint(3014, 5451, 0), new WorldPoint(3030, 5462, 0));
		templeDungeon = new Zone(new WorldPoint(2777, 9185, 0), new WorldPoint(2818, 9219, 0));
		cookRoom = new Zone(new WorldPoint(3049, 5477, 0), new WorldPoint(3069, 5492, 0));
	}

	public void setupConditions()
	{
		inDiningRoom = new ZoneCondition(diningRoom);
		onCrashIsland = new ZoneCondition(crashIsland);
		inSnakeHole = new ZoneCondition(snakeHole);
		inNutHole = new ZoneCondition(nutHole);
		inTempleDungeon = new ZoneCondition(templeDungeon);
		inCookRoom = new ZoneCondition(cookRoom);


		askedAboutBanana = new VarbitCondition(1915, 10, Operation.GREATER_EQUAL);
		askedAboutNut = new VarbitCondition(1916, 10, Operation.GREATER_EQUAL);

		hasRawStuffedSnake = new ItemRequirementCondition(rawStuffedSnake);

		hasPaste = new ItemRequirementCondition(paste);
		hasSnakeCorpse = new Conditions(LogicType.OR, new ItemRequirementCondition(snakeCorpse), new ItemRequirementCondition(rawStuffedSnake));
		hasSlicedRedBanana = new ItemRequirementCondition(slicedBanana);
		hasRedBanana = new Conditions(LogicType.OR, new ItemRequirementCondition(redBanana), new ItemRequirementCondition(slicedBanana));
		hasTchikiNut = new Conditions(LogicType.OR, new ItemRequirementCondition(tchikiNuts), new ItemRequirementCondition(paste));
		hasCookedsnake = new ItemRequirementCondition(cookedSnake);
	}

	public void setupSteps()
	{
		enterDiningRoom = new ObjectStep(this, ObjectID.LARGE_DOOR_12349, new WorldPoint(3213, 3221, 0), "Go inspect Awowogei in Lumbridge Castle.");
		inspectAwowogei = new ObjectStep(this, ObjectID.AWOWOGEI_12347, new WorldPoint(1865, 5319, 0), "Inspect Awowogei.");
		inspectAwowogei.addSubSteps(enterDiningRoom);

		talkToAwowogei = new ObjectStep(this, ObjectID.AWOWOGEI, new WorldPoint(2803, 2765, 0), "Talk to Awowogei on Ape Atoll.", greegreeEquipped, mAmulet);
		talkToWiseMonkeys = new NpcStep(this, NpcID.IWAZARU, new WorldPoint(2789, 2795, 0), "Talk to the three monkey sat in the temple.", greegreeEquipped, mAmulet);
		talkToWiseMonkeys.addDialogStep("Do you know anything about the King's favourite dish?");
		useBananaOnWiseMonkeys = new NpcStep(this, NpcID.IWAZARU, new WorldPoint(2789, 2795, 0), "Use a banana on the one of the three monkeys.", bananaHighlighted, greegreeEquipped, mAmulet);
		useBananaOnWiseMonkeys.addIcon(ItemID.BANANA);
		useNutsOnWiseMonkeys = new NpcStep(this, NpcID.IWAZARU, new WorldPoint(2789, 2795, 0), "Use some monkey nuts on the one of the three monkeys.", monkeyNutsHighlighted, greegreeEquipped, mAmulet);
		useNutsOnWiseMonkeys.addIcon(ItemID.MONKEY_NUTS);

		goToCrashIsland = new NpcStep(this, NpcID.LUMDO_1454, new WorldPoint(2802, 2706, 0), "Travel to Crash Island with Lumbdo.", combatGear);
		enterCrashHole = new ObjectStep(this, ObjectID.PIT_15572, new WorldPoint(2922, 2722, 0), "Enter the hole on Crash Island. Protect melee when entering as you'll be attacked straight away by snakes.", combatGear, protectMelee);
		enterCrashHole.addDialogStep("Yes, I'm as hard as nails.");
		killSnake = new NpcStep(this, NpcID.BIG_SNAKE, new WorldPoint(3019, 5485, 0), "Kill a giant snake for its corpse. Kill a few in case you burn it.", snakeCorpse);
		leaveSnakeHole = new ObjectStep(this, ObjectID.ROPE_15571, new WorldPoint(3024, 5489, 0), "Leave the hole.");
		returnToApeAtoll = new NpcStep(this, NpcID.LUMDO_1454, new WorldPoint(2892, 2723, 0), "Travel back to Ape Atoll with Lumbdo.");

		useRopeOnTree = new ObjectStep(this, NullObjectID.NULL_15580, new WorldPoint(2697, 2786, 0), "Use a rope on the red banana tree on the north west of Ape Atoll as a gorilla.", gorillaGreegree, ropeHighlighted);
		useRopeOnTree.addIcon(ItemID.ROPE);

		enterNutHole = new ObjectStep(this, ObjectID.HOLE_15491, new WorldPoint(2758, 2729, 0), "Go around the monkey agility course until you reach a hole, and go down it.", ninjaGreegree);
		takeNuts = new ObjectStep(this, ObjectID.BUSH_16059, new WorldPoint(3021, 5458, 0), "Take a nut from the bush. Take a few in case you burn the final meal.");

		grindNuts = new DetailedQuestStep(this, "Use a pestle and mortar on the tchiki nuts.", pestleAndMortar, tchikiNutsHighlighted);
		sliceBanana = new DetailedQuestStep(this, "Use a knife/slash weapon on the red banana.", knife, redBananaHighlighted);

		stuffSnake = new DetailedQuestStep(this, "Use the paste on the snake to stuff it.", paste, slicedBanana, snakeCorpseHighlighted);

		enterZombieDungeon = new ObjectStep(this, ObjectID.TRAPDOOR_4880, new WorldPoint(2807, 2785, 0), "Enter the trapdoor in the monkey temple.", zombieGreegree, rawStuffedSnake);
		((ObjectStep)(enterZombieDungeon)).addAlternateObjects(ObjectID.TRAPDOOR_4879);
		enterCookingHole = new ObjectStep(this, ObjectID.EXIT_16061, new WorldPoint(2805, 9199, 0), "Enter the hole just under where you entered,", zombieGreegree, rawStuffedSnake);
		cookSnake = new ObjectStep(this, NullObjectID.NULL_26175, new WorldPoint(3056, 5485, 0), "Go across the hot rocks with the zombie greegree equipped. Cook the stuffed snake on the rock at the end of the room.", zombieGreegree, rawStuffedSnakeHighlighted);
		cookSnake.addIcon(ItemID.RAW_STUFFED_SNAKE);

		enterDiningRoomAgain = new ObjectStep(this, ObjectID.DOOR_12348, new WorldPoint(3207, 3217, 0), "Go give the snake to Awowogei to finish the quest.", cookedSnake);
		useSnakeOnAwowogei = new ObjectStep(this, ObjectID.AWOWOGEI_12347, new WorldPoint(1865, 5319, 0), "Give the snake to Awowogei to finish the quest.", cookedSnakeHighlighted);
		useSnakeOnAwowogei.addIcon(ItemID.STUFFED_SNAKE);
		useSnakeOnAwowogei.addSubSteps(enterDiningRoomAgain);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRequirements()
	{
		return new ArrayList<>(Arrays.asList(mAmulet, bananaHighlighted, monkeyNutsHighlighted, ropeHighlighted, knife, pestleAndMortar, gorillaGreegree, ninjaGreegree, zombieGreegree));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("Big Snake (level 84)", "If you need the greegrees still, a zombie, ninja, and guard monkey"));
	}

	@Override
	public ArrayList<String> getNotes()
	{
		return new ArrayList<>(Collections.singletonList("If you don't have the ninja/gorilla/zombie greegrees ready, it's recommended you get them all in a single run to Zooknock to save time."));
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", new ArrayList<>(Collections.singletonList(inspectAwowogei))));
		allSteps.add(new PanelDetails("Saving Awowogei", new ArrayList<>(Arrays.asList(talkToAwowogei, talkToWiseMonkeys, useBananaOnWiseMonkeys, useNutsOnWiseMonkeys, goToCrashIsland, enterCrashHole, killSnake, leaveSnakeHole,
			returnToApeAtoll, useRopeOnTree, enterNutHole, takeNuts, grindNuts, sliceBanana, stuffSnake, enterZombieDungeon, enterCookingHole, cookSnake, useSnakeOnAwowogei)),
			mAmulet, bananaHighlighted, monkeyNutsHighlighted, ropeHighlighted, knife, pestleAndMortar, zombieGreegree, ninjaGreegree, gorillaGreegree));
		return allSteps;
	}

	@Override
	public boolean isCompleted()
	{
		return (client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER_MONKEY_AMBASSADOR.getId()) >= 50 || client.getVarbitValue(QuestVarbits.QUEST_RECIPE_FOR_DISASTER.getId()) < 3);
	}
}
