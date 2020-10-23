package com.xpgrapher;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.midi.SysexMessage;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "XP Grapher",
	description = "Shows a real time XP graph for any skill",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class XpGrapherPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	public XpGrapherConfig config;

	public Skill[] skillList;
	public Skill mostRecentSkillGained;

	public int tickCount = 0;
	public long startTime = 0;
	public long currentTime = 0;
	public XpDataManager xpDataManager;
	public XpGraphPointManager xpGraphPointManager;
	public XpGraphColorManager xpGraphColorManager;

	public int graphWidth;
	public int graphHeight;

	public int numVerticalDivisions = 5;

	//public int maxNumberOfGraphedSkills = 8;
	public ArrayList<Skill> currentlyGraphedSkills = new ArrayList<Skill>();

	private boolean lastTickResetGraphSwitch = false;
	private boolean thisTickResetGraphSwitch = false;

	public boolean startMessageDisplaying = true;

	@Provides
	XpGrapherConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(XpGrapherConfig.class);
	}


	@com.google.inject.Inject
	private XpGrapherOverlay overlay;

	@com.google.inject.Inject
	private OverlayManager overlayManager;

	@Override
	public void startUp()
	{
		graphWidth = config.graphWidth();
		graphHeight = config.graphHeight();
		skillList = Skill.values();
		xpDataManager = new XpDataManager(this);
		xpGraphPointManager = new XpGraphPointManager(this);
		xpGraphColorManager = new XpGraphColorManager(this);
		startTime = System.currentTimeMillis();

		overlayManager.add(overlay);

	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	private boolean isSkillCurrentlyGraphed(Skill theSkill)
	{
		for (int i = 0; i < currentlyGraphedSkills.size(); i++)
		{
			if (currentlyGraphedSkills.get(i).getName() == theSkill.getName())
			{
				return true;
			}
		}
		return false;
	}

	public void graphSkill(Skill skillToAdd)
	{
		mostRecentSkillGained = skillToAdd;
		if (!isSkillCurrentlyGraphed(skillToAdd))
		{
			if (currentlyGraphedSkills.size() < config.maxSkillsToGraph())
			{
				currentlyGraphedSkills.add(skillToAdd);
				xpGraphPointManager.isSkillShownMap.put(skillToAdd, true);
			}
			else
			{
				while (currentlyGraphedSkills.size() > config.maxSkillsToGraph())
				{
					removeSkill();
				}
				currentlyGraphedSkills.add(skillToAdd);
				xpGraphPointManager.isSkillShownMap.put(skillToAdd, true);
			}
		}

	}

	public void removeSkill()
	{
		xpGraphPointManager.isSkillShownMap.put(currentlyGraphedSkills.get(0), false);
		currentlyGraphedSkills.remove(0);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{

		currentTime = System.currentTimeMillis();
		graphWidth = config.graphWidth();
		graphHeight = config.graphHeight();
		xpDataManager.update();
		xpGraphPointManager.update();

		if (currentlyGraphedSkills.size() > config.maxSkillsToGraph())
		{
			int excessAmount = currentlyGraphedSkills.size() - config.maxSkillsToGraph();
			for (int i = 0; i < excessAmount; i++)
			{
				removeSkill();
			}
		}

		//System.out.println(tickCount);
		//System.out.println("fletching xp from data manager " + xpDataManager.getXpData(Skill.FLETCHING, tickCount));
		//System.out.println("y value from graph manager " + xpGraphPointManager.getGraphPointData(Skill.FLETCHING, tickCount));

		tickCount++;

		thisTickResetGraphSwitch = config.resetGraph();
		if (thisTickResetGraphSwitch && !lastTickResetGraphSwitch)
		{
			resetAll();
		}

		lastTickResetGraphSwitch = thisTickResetGraphSwitch;

	}

	private void resetAll()
	{
		xpDataManager = new XpDataManager(this);
		xpGraphPointManager = new XpGraphPointManager(this);
		startTime = System.currentTimeMillis();
		currentlyGraphedSkills = new ArrayList<Skill>();
		tickCount = 0;
	}

	public Client getClient()
	{
		return client;
	}

	public boolean isSkillShown(Skill skill)
	{

		return xpGraphPointManager.isSkillShown(skill);
	}

}
