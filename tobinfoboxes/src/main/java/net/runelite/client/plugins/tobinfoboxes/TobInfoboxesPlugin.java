package net.runelite.client.plugins.tobinfoboxes;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.util.Text;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "ToB Infoboxes",
	description = "Theatre of Blood room timer infoboxes",
	tags = {"raid", "tob", "timer", "infobox"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class TobInfoboxesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TobInfoboxesConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	private static final Pattern MAIDEN_COMPLETE = Pattern.compile("Wave 'The Maiden of Sugadinti' complete! Duration: (\\d+):(\\d+)");
	private static final Pattern BLOAT_COMPLETE = Pattern.compile("Wave 'The Pestilent Bloat' complete! Duration: (\\d+):(\\d+)");
	private static final Pattern NYLOCAS_COMPLETE = Pattern.compile("Wave 'The Nylocas' complete! Duration: (\\d+):(\\d+)");
	private static final Pattern SOTETSEG_COMPLETE = Pattern.compile("Wave 'Sotetseg' complete! Duration: (\\d+):(\\d+)");
	private static final Pattern XARPUS_COMPLETE = Pattern.compile("Wave 'Xarpus' complete! Duration: (\\d+):(\\d+)");
	private static final Pattern VERZIK_COMPLETE = Pattern.compile("Wave 'The Final Challenge' complete! Duration: (\\d+):(\\d+)");
	private static final Pattern TOTAL_COMPLETE = Pattern.compile("Theatre of Blood wave completion time: (\\d+):(\\d+)");

	private TobInfobox maidenInfoBox;
	private TobInfobox bloatInfoBox;
	private TobInfobox nyloInfoBox;
	private TobInfobox soteInfoBox;
	private TobInfobox xarpusInfoBox;
	private TobInfobox verzikInfoBox;
	private TobInfobox totalInfoBox;

	boolean instanced = false;
	boolean prevInstance = false;

	@Override
	protected void shutDown() throws Exception
	{
		removeAll();
	}

	private TobInfobox addInfoBox(String path, String bossname, int luminance, String time, boolean outline)
	{
		BufferedImage bossImg = ImageUtil.getResourceStreamFromClass(getClass(), path);
		if (outline)
		{
			bossImg = ImageUtil.outlineImage(bossImg, Color.BLACK);
		}
		bossImg = ImageUtil.luminanceOffset(bossImg, luminance);
		TobInfobox box = new TobInfobox(bossImg, this, time, bossname);
		return box;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String message = Text.removeTags(event.getMessage());
		if (config.showMaiden())
		{
			Matcher mMaiden = MAIDEN_COMPLETE.matcher(message);
			if (mMaiden.find())
			{
				String time = mMaiden.group(1) + ":" + mMaiden.group(2);
				maidenInfoBox = addInfoBox("/maiden.png", "Maiden", -35, time, false);
				infoBoxManager.addInfoBox(maidenInfoBox);
			}
		}

		if (config.showBloat())
		{
			Matcher mBloat = BLOAT_COMPLETE.matcher(message);
			if (mBloat.find())
			{
				String time = mBloat.group(1) + ":" + mBloat.group(2);
				bloatInfoBox = addInfoBox("/bloat.png", "Bloat", -25, time, false);
				infoBoxManager.addInfoBox(bloatInfoBox);
			}
		}

		if (config.showNylo())
		{
			Matcher mNylo = NYLOCAS_COMPLETE.matcher(message);
			if (mNylo.find())
			{
				String time = mNylo.group(1) + ":" + mNylo.group(2);
				nyloInfoBox = addInfoBox("/nylo.png", "Nylo", -30, time, false);
				infoBoxManager.addInfoBox(nyloInfoBox);
			}
		}

		if (config.showSotetseg())
		{
			Matcher mSote = SOTETSEG_COMPLETE.matcher(message);
			if (mSote.find())
			{
				String time = mSote.group(1) + ":" + mSote.group(2);
				soteInfoBox = addInfoBox("/sote.png", "Sotetseg", 0, time, false);
				infoBoxManager.addInfoBox(soteInfoBox);
			}
		}

		if (config.showXarpus())
		{
			Matcher mXarp = XARPUS_COMPLETE.matcher(message);
			if (mXarp.find())
			{
				String time = mXarp.group(1) + ":" + mXarp.group(2);
				xarpusInfoBox = addInfoBox("/xarp.png", "Xarpus", 0, time, true);
				infoBoxManager.addInfoBox(xarpusInfoBox);
			}
		}

		if (config.showVerzik())
		{
			Matcher mVerzik = VERZIK_COMPLETE.matcher(message);
			if (mVerzik.find())
			{
				String time = mVerzik.group(1) + ":" + mVerzik.group(2);
				verzikInfoBox = addInfoBox("/verzik.png", "Verzik", 0, time, true);
				infoBoxManager.addInfoBox(verzikInfoBox);
			}
		}

		if (config.showTotal())
		{
			Matcher mTotal = TOTAL_COMPLETE.matcher(message);
			if (mTotal.find())
			{
				String time = mTotal.group(1) + ":" + mTotal.group(2);
				totalInfoBox = addInfoBox("/total.png", "Total", -35, time, false);
				infoBoxManager.addInfoBox(totalInfoBox);
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!config.showMaiden())
		{
			infoBoxManager.removeInfoBox(maidenInfoBox);
		}
		if (!config.showBloat())
		{
			infoBoxManager.removeInfoBox(bloatInfoBox);
		}
		if (!config.showNylo())
		{
			infoBoxManager.removeInfoBox(nyloInfoBox);
		}
		if (!config.showSotetseg())
		{
			infoBoxManager.removeInfoBox(soteInfoBox);
		}
		if (!config.showXarpus())
		{
			infoBoxManager.removeInfoBox(xarpusInfoBox);
		}
		if (!config.showVerzik())
		{
			infoBoxManager.removeInfoBox(verzikInfoBox);
		}
		if (!config.showTotal())
		{
			infoBoxManager.removeInfoBox(totalInfoBox);
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		prevInstance = instanced;
		instanced = client.isInInstancedRegion();
		if (!prevInstance && instanced)
		{
			removeAll();
		}
	}

	private void removeAll()
	{
		infoBoxManager.removeInfoBox(maidenInfoBox);
		infoBoxManager.removeInfoBox(bloatInfoBox);
		infoBoxManager.removeInfoBox(nyloInfoBox);
		infoBoxManager.removeInfoBox(soteInfoBox);
		infoBoxManager.removeInfoBox(xarpusInfoBox);
		infoBoxManager.removeInfoBox(verzikInfoBox);
		infoBoxManager.removeInfoBox(totalInfoBox);
	}

	@Provides
	TobInfoboxesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TobInfoboxesConfig.class);
	}
}
