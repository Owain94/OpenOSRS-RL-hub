package com.playtime;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NpcID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Play Time",
	description = "Tracking in-game play time days and hours",
	enabledByDefault = false,
	type = PluginType.UTILITY
)
public class PlayTimePlugin extends Plugin
{
	private static final ZoneId UTC = ZoneId.of("UTC");
	private static final ZoneId JAGEX = ZoneId.of("Europe/London");
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy");
	public static final String HANS_TIME_KEY = "OLD";

	private boolean ready = false;
	private boolean loadedData = false;
	private boolean loadedHans = false;

	public long getSessionTicks() {
		return sessionTicks;
	}

	private long sessionTicks = 0;

	public long getTotalTicks() {
		return totalTicks;
	}

	private long totalTicks = 0;

	private PlayTimePanel panel;
	private NavigationButton navButton;

	@Inject
	private TimeRecordWriter writer;

	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private PlayTimeConfig config;

	private PlayTimeRecord record;
	public HashMap<String, PlayTimeRecord> records = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		panel = new PlayTimePanel(this);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "pluginicon.png");

		navButton = NavigationButton.builder()
				.tooltip("Play Time")
				.priority(6)
				.icon(icon)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		if (client.getGameState().equals(GameState.LOGGED_IN) || client.getGameState().equals(GameState.LOADING))
		{
			loadData();
		}
		panel.showView();
	}

	public PlayTimeConfig getConfig() {
		return config;
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		saveData();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			panel.showView();
			return;
		}

		Widget npcHead = client.getWidget(WidgetInfo.DIALOG_NPC_HEAD_MODEL);
		if (npcHead != null && npcHead.getModelId() == NpcID.HANS && !loadedHans) {
			Widget textw = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT);
			String text = textw.getText();
			text = text.replaceAll("<br>", "");
			Matcher m = Pattern.compile("([0-9]+)[^0-9]*([0-9]+)[^0-9]*([0-9]+)").matcher(text);
			if (m.find() && m.groupCount() == 3 && text.startsWith("You've spent")) {
				long days = Long.parseLong(m.group(1));
				long hours = Long.parseLong(m.group(2));
				long mins = Long.parseLong(m.group(3));
				long total = (days * 100 * 60 * 24) + (hours * 100 * 60) + (mins * 100);
				if (records != null) {
					long trackedTime = 0;
					for (int i = 0; i < records.size(); i++) {
						PlayTimeRecord rec = records.get(i);
						if (rec != null && rec.getDate() != HANS_TIME_KEY) {
							trackedTime += rec.getTime();
						}
					}
					total -= trackedTime;
					totalTicks = total + trackedTime;
				}
				PlayTimeRecord rec = records.get(HANS_TIME_KEY);
				if (rec == null) {
					rec = new PlayTimeRecord(HANS_TIME_KEY, total);
					records.put(HANS_TIME_KEY, rec);
				}
				else {
					rec.setTime(total);
				}
				if (records == null) {
					totalTicks = total;
				}
				saveData();
				loadedHans = true;
			}
		}
		else if (npcHead == null || npcHead.getModelId() != NpcID.HANS) {
			loadedHans = false;
		}
		sessionTicks++;
		totalTicks++;

		PlayTimeRecord rec = getCurrentRecord();
		rec.setTime(rec.getTime() + 1);

		if (sessionTicks % 10 == 0) {
			saveData();
		}
		loadData();
		panel.showView();
	}

	public PlayTimeRecord getCurrentRecord() {
		if (!loadedData) {
			return null;
		}
		if (record != null && record.getDate() == LocalDate.now().format(DATE_FORMAT)) {
			return record;
		}
		PlayTimeRecord rec = records.get(LocalDate.now().format(DATE_FORMAT));
		if (rec == null) {
			rec = new PlayTimeRecord(LocalDate.now().format(DATE_FORMAT), 0);
			records.put(rec.getDate(), rec);
		}
		record = rec;
		return rec;
	}

	public void resetCounter() {
		sessionTicks = 0;
	}

	public void loadData() {
		if (loadedData) {
			return;
		}
		writer.setPlayerUsername(client.getUsername());
		ArrayList<PlayTimeRecord> recs = writer.loadPlayTimeRecords();
		totalTicks = 0;
		recs.forEach((rec) -> {
			totalTicks += rec.getTime();
			records.put(rec.getDate(), rec);
		});
		loadedData = true;
	}

	public void saveData() {
		if (!loadedData) {
			return;
		}
		ArrayList<PlayTimeRecord> recs = new ArrayList<>();
		records.forEach((k, v) -> recs.add(v));
		writer.writePlayTimeFile(recs);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState state = event.getGameState();
		if (panel != null) {
			panel.showView();
		}
		switch (state)
		{
			case LOGGING_IN:
			case HOPPING:
			case CONNECTION_LOST:
				ready = true;
				break;
			case LOGGED_IN:
				if (ready)
				{
					loadData();
					ready = false;
				}
				break;
			case LOGIN_SCREEN:
				sessionTicks = 0;
				panel.showView();
				break;
		}
	}

	@Provides
	PlayTimeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PlayTimeConfig.class);
	}
}
