package info.osleague.runelite.osleague;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "OsLeague",
	description = "who needs one anyways, right?",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class OsLeaguePlugin extends Plugin
{
	public static final int MAX_TASK_COUNT = 961;

	private static final Pattern POINTS_PATTERN = Pattern.compile("Reward: <col=ffffff>(\\d*) points<\\/col>");

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ChatMessageManager chatMessageManager;

	private NavigationButton titleBarButton;

	private boolean filtersRecentlySetToAll = false;
	private List<Task> tasks;
	private List<Relic> relics;
	private List<Area> areas;

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGING_IN:
			case LOGIN_SCREEN:
				clearSavedData();
				break;
		}
	}

	@Override
	protected void startUp() throws Exception
	{
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "osleague.png");

		titleBarButton = NavigationButton.builder()
			.tab(false)
			.tooltip("Copy Tasks to Clipboard")
			.icon(icon)
			.onClick(this::copyJsonToClipboard)
			.build();

		clientToolbar.addNavigation(titleBarButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(titleBarButton);
		clearSavedData();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		filtersRecentlySetToAll = this.getAllFiltersSetToAll();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (filtersRecentlySetToAll && isTaskWindowOpen())
		{
			this.tasks = gatherTaskData();
			if (this.tasks != null)
			{
				filtersRecentlySetToAll = false;
				sendTasksUpdatedMessage();
			}
		}
	}

	private void clearSavedData()
	{
		this.tasks = null;
		this.relics = null;
		this.areas = null;
		filtersRecentlySetToAll = false;
	}

	private void sendTasksUpdatedMessage()
	{
		String chatMessage = this.tasks.size() + "/" + MAX_TASK_COUNT +	" tasks saved for export to OS League Tools";
		sendChatMessage(chatMessage, Color.BLUE);
	}

	private boolean isTaskWindowOpen()
	{
		Widget widget = client.getWidget(657, 10);
		return widget != null && !widget.isHidden();
	}

	private boolean getAllFiltersSetToAll()
	{
		/*
			TRAILBLAZER_LEAGUE_TASK_TIER_FILTER(10033),
			TRAILBLAZER_LEAGUE_TASK_TYPE_FILTER(11689),
			TRAILBLAZER_LEAGUE_TASK_AREA_FILTER(11692),
			TRAILBLAZER_LEAGUE_TASK_COMPLETED_FILTER(10034),
		 */
		return (client.getVarbitValue(10033) == 0 &&
				client.getVarbitValue(11689) == 0 &&
				client.getVarbitValue(11692) == 0 &&
				client.getVarbitValue(10034) == 0);
	}

	private void copyJsonToClipboard()
	{
		if (this.tasks == null || this.areas == null || this.relics == null)
		{
			showMessageBox(
				"Cannot Export Data",
				"You must open the tasks UI, areas UI, and relics UI before exporting.");
			return;
		}

		Gson gson = new Gson();

		OsLeagueExport osLeagueExport = new OsLeagueExport();
		osLeagueExport.areas = this.areas;
		osLeagueExport.relics = this.relics;
		osLeagueExport.tasks = this.tasks;

		String json = gson.toJson(osLeagueExport);
		final StringSelection stringSelection = new StringSelection(json);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

		showMessageBox(
			"OS League Tools Data Exported!",
			"Exported data copied to clipboard! Go to osleague.tools, click Manage Data > Import from Runelite, and paste into the box."
		);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == 657) //WidgetID.TRAILBLAZER_TASKS_GROUP_ID)
		{
			this.tasks = gatherTaskData();
			if (this.tasks != null)
			{
				sendTasksUpdatedMessage();
			}
		}
		if (widgetLoaded.getGroupId() == 655) //WidgetID.TRAILBLAZER_RELICS_GROUP_ID)
		{
			this.relics = gatherRelicData();
		}
		if (widgetLoaded.getGroupId() == 512) //WidgetID.TRAILBLAZER_AREAS_GROUP_ID)
		{
			this.areas = gatherAreaData();
		}
	}

	private List<Area> gatherAreaData()
	{
		List<Area> areas = new ArrayList<>();
		Widget mapWidget = client.getWidget(512, 8); //WidgetInfo.TRAILBLAZER_AREAS_MAP);
		if (mapWidget == null)
		{
			return null;
		}

		Widget[] widgets = mapWidget.getStaticChildren();
		for (Widget widget : widgets)
		{
			Area area = Area.getAreaBySprite(widget.getSpriteId());
			if (area != null)
			{
				areas.add(area);
			}
		}
		return areas;
	}

	private List<Relic> gatherRelicData()
	{
		List<Relic> relics = new ArrayList<>();
		Widget relicIconsWidget = client.getWidget(651, 1);
		if (relicIconsWidget == null)
		{
			return null;
		}

		Widget[] widgets = relicIconsWidget.getDynamicChildren();
		for (Widget widget : widgets)
		{
			Relic relic = Relic.getRelicBySprite(widget.getSpriteId());
			if (relic != null)
			{
				relics.add(relic);
			}
		}

		return relics;
	}

	private List<Task> gatherTaskData()
	{
		Widget taskLabelsWidget = client.getWidget(657, 10);
		Widget taskPointsWidget = client.getWidget(657, 11);
		Widget taskDifficultiesWidget = client.getWidget(657, 16);
		if (taskLabelsWidget == null || taskPointsWidget == null || taskDifficultiesWidget == null)
		{
			return null;
		}

		Widget[] taskLabels = taskLabelsWidget.getDynamicChildren();
		Widget[] taskPoints = taskPointsWidget.getDynamicChildren();
		Widget[] taskDifficulties = taskDifficultiesWidget.getDynamicChildren();
		if (taskLabels.length != taskPoints.length || taskPoints.length != taskDifficulties.length)
		{
			return null;
		}
		if (taskLabels.length != MAX_TASK_COUNT)
		{
			sendChatMessage("Could not gather tasks for OS League Tools export. All filters must be set to 'All'.", Color.RED);
			return null;
		}

		List<Task> tasks = new ArrayList<>();
		for (int i = 0; i < taskLabels.length; i++)
		{
			String name = taskLabels[i].getText();
			Task task = new Task(
				i, name,
				getTaskPoints(taskPoints[i]),
				isTaskCompleted(taskLabels[i]),
				taskDifficulties[i].getSpriteId());

			tasks.add(task);
		}

		return tasks;
	}

	private int getTaskPoints(Widget taskPoints)
	{
		Matcher m = POINTS_PATTERN.matcher(taskPoints.getText());
		if (m.find())
		{
			return Integer.parseInt(m.group(1));
		}
		return -1;
	}

	private boolean isTaskCompleted(Widget taskLabel)
	{
		return taskLabel.getTextColor() != 0x9f9f9f;
	}

	private static void showMessageBox(final String title, final String message)
	{
		SwingUtilities.invokeLater(() ->
			JOptionPane.showMessageDialog(
				null,
				message, title,
				INFORMATION_MESSAGE));
	}

	private void sendChatMessage(String chatMessage, Color color)
	{
		final String message = new ChatMessageBuilder()
				.append(color, chatMessage)
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}
}
