/*
 * Copyright (c) 2020, Christopher Oswald <https://github.com/cesoun>
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
package net.runelite.client.plugins.optimalquestguide;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Quest;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.optimalquestguide.panels.GuidePanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Optimal Quest Guide",
	description = "I wanted this so now you get this as well. OSRS Wiki Optimal Quest Guide.",
	tags = {"quest", "guide", "optimal"},
	enabledByDefault = false,
	type = PluginType.UTILITY
)
@Slf4j
public class GuidePlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private SkillIconManager skillIconManager;

	private NavigationButton navigationButton;
	private GuidePanel guidePanel;
	private QuestInfo[] questInfos;

	@Override
	protected void startUp()
	{
		// Parse the quests.json to be loaded into the panel.
		InputStream questDataFile = GuidePlugin.class.getResourceAsStream("quests.json");
		questInfos = new Gson().fromJson(new InputStreamReader(questDataFile), QuestInfo[].class);

		guidePanel = new GuidePanel(questInfos);

		// Setup the icon.
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "panel_icon.png");

		// Build the navigation button that shows on the sidebar.
		navigationButton = NavigationButton.builder()
			.tooltip("Optimal Quest Guide")
			.icon(icon)
			.priority(1)
			.panel(guidePanel)
			.build();

		// Add the button to the sidebar.
		clientToolbar.addNavigation(navigationButton);


		// Update the quest list if we are logged in.
		if (client.getGameState().equals(GameState.LOGGED_IN))
		{
			clientThread.invoke(this::updateQuestList);
		}
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e)
	{
		if (e.getGroupId() == WidgetID.MINIMAP_GROUP_ID || e.getGroupId() == WidgetID.QUEST_COMPLETED_GROUP_ID)
		{
			updateQuestList();
		}
	}

	private void updateQuestList()
	{
		for (Quest quest : Quest.values())
		{
			for (QuestInfo info : questInfos)
			{
				if (quest.getName().equalsIgnoreCase(info.getName()))
				{
					info.setWidget(quest);
					info.setQuestState(quest.getState(client));
					break;
				}
			}
		}

		guidePanel.updateQuests(questInfos);
	}
}
