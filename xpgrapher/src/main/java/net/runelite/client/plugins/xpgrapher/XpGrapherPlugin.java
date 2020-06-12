package net.runelite.client.plugins.xpgrapher;

import com.google.inject.Provides;
import java.util.ArrayList;
import javax.inject.Inject;
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
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "XP Grapher",
	description = "Shows a real time XP graph for any skill",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class XpGrapherPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private XpGrapherConfig config;

	@Provides
	XpGrapherConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(XpGrapherConfig.class);
	}

	private int tickNum = 0;

	private Skill skillToGraph = Skill.FLETCHING;
	private int skillXP;

	public int width = 200;
	public int height = 100;

	public ArrayList<Integer> xpList = new ArrayList<Integer>();
	public ArrayList<Integer[]> graphPoints = new ArrayList<Integer[]>();

	public int minimumXp = -1;
	public int maximumXp = -1;

	@Inject
	private XpGrapherOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Override
	public void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		skillXP = client.getSkillExperience((skillToGraph));
		xpList.add(skillXP);

		update(xpList);

		tickNum++;
	}

	public XpGrapherConfig getConfig()
	{
		return config;
	}

	public void update(ArrayList xpList)
	{
		width = config.graphWidth();
		height = config.graphHeight();

		ArrayList<Integer[]> newList = new ArrayList<Integer[]>();

		for (int x = 0; x < width; x++)
		{

			double ratioAcrossGraph = (double) x / (double) width;

			int dataIndex;
			//if the session time is not set, graph showing whole session
			if (!config.sessionTimeSet())
			{
				dataIndex = (int) (Math.floor(ratioAcrossGraph * xpList.size()));
			}
			//if the graph width is a specific timeframe
			else
			{
				double tickLength = 0.61; //seconds
				double tickLengthMinutes = tickLength / 60;
				int ticksInTimeFrame = (int) (config.sessionLength() / tickLengthMinutes);
				int startingTick = tickNum - ticksInTimeFrame;
				int endingTick = startingTick + ticksInTimeFrame;
				//System.out.println(startingTick + " through " + endingTick);
				dataIndex = (int) (ticksInTimeFrame * ratioAcrossGraph) + startingTick;
			}

			int maxXp;
			if (config.goalXPExists())
			{
				maxXp = config.goalXP();
			}
			else
			{
				maxXp = (int) xpList.get(xpList.size() - 1);
			}
			int minXp = (int) xpList.get(0);
			int xpRange = maxXp - minXp;
			int xpGained;
			if (dataIndex >= 0)
			{
				xpGained = (int) xpList.get(dataIndex) - minXp;
			}
			else
			{
				xpGained = 0;
			}

			double ratioVertical = xpGained / (double) xpRange;
			//System.out.println(xpGained/(double)xpRange);
			int y = height - (int) ((double) height * ratioVertical);
			//System.out.println(y);

			Integer[] newEntry = {x, y};
			newList.add(newEntry);

			minimumXp = minXp;
			maximumXp = maxXp;
		}

		graphPoints = newList;

		Skill theSkill = config.skillToGraph();

		//check if the tracked skill changed
		if (theSkill != skillToGraph)
		{
			skillToGraph = theSkill;
			resetData();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChangedEvent)
	{
		if (configChangedEvent.getKey().compareTo("resetGraph") == 0 && config.resetGraph())
		{
			resetData();
		}
	}

	private void resetData()
	{
		xpList.clear();
		graphPoints.clear();
		skillToGraph = config.skillToGraph();
		tickNum = 0;
	}
}