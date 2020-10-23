package com.TrayIndicators;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
		name = "Tray Indicators",
		description = "Displays your hitpoints, prayer or absorption in the system tray.",
		tags = {"notifications"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class TrayIndicatorsPlugin extends Plugin
{
	private static final int[] NMZ_MAP_REGION = {9033};
	private boolean startUp;

	@Inject
	private Client client;

	@Inject
	private TrayIndicatorsConfig config;

	@Provides
	TrayIndicatorsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TrayIndicatorsConfig.class);
	}

	private java.util.List<TrayIcon> trayIcons = new ArrayList<TrayIcon>();

	@Override
	protected void startUp() throws Exception
	{
		startUp = true;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("Tray Indicators"))
		{
			switch (event.getKey())
			{
				case "health":
					if (event.getNewValue().equals("true")) {
						trayIcons.add(0, setupTrayIcon(0));
					}else {
						removeTrayIcon(0);
					}
					break;
				case "prayer":
					if (event.getNewValue().equals("true")) {
						trayIcons.add(1, setupTrayIcon(1));
					}else{
						removeTrayIcon(1);
					}
					break;
				case "absorption":
					if (event.getNewValue().equals("true")) {
						trayIcons.add(2, setupTrayIcon(2));
					}else {
						removeTrayIcon(2);
					}
					break;
				default:
					break;
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{

		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			if(startUp == false) {
				removeAllTrayIcons();
				startUp = true;
			}
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		startUp = false;
		removeAllTrayIcons();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if(startUp)
		{
			if (config.health())
				trayIcons.add(0, setupTrayIcon(0));

			if (config.prayer())
				trayIcons.add(1, setupTrayIcon(1));

			if(isInNightmareZone() && config.absorption())
				trayIcons.add(2, setupTrayIcon(2));

			startUp = false;
		}

		SystemTray systemTray = SystemTray.getSystemTray();

		if (config.absorption()) {
			if (isInNightmareZone() && !indexExists(trayIcons, 2)) {
				trayIcons.add(2, setupTrayIcon(2));
			} else if (!isInNightmareZone() && indexExists(trayIcons, 2)) {
				removeTrayIcon(2);
			}
		}

		for (int i=0; i < trayIcons.size(); i++)
		{
			TrayIcon trayIcon = trayIcons.get(i);
			switch (i)
			{
				case 0:
					if(config.health()) {
						trayIcon.setImage(createImage(i));
					}
					break;
				case 1:
					if(config.prayer()) {
						trayIcon.setImage(createImage(i));
					}
					break;
				case 2:
					if(config.absorption()) {
						trayIcon.setImage(createImage(i));
					}
					break;
			}
		}
	}

	private TrayIcon setupTrayIcon(int i)
	{
		if (!SystemTray.isSupported())
		{
			return null;
		}

		SystemTray systemTray = SystemTray.getSystemTray();

		TrayIcon trayIcon = new TrayIcon(createImage(i));
		trayIcon.setImageAutoSize(true);
		//trayIcon.hashCode();

		try
		{
			systemTray.add(trayIcon);
		}
		catch (AWTException ex)
		{
			log.debug("Unable to add system tray icon", ex);
			return trayIcon;
		}

		return trayIcon;
	}

	public BufferedImage createImage(int i){
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = image.createGraphics();
		switch(i) {
			case 0:
				// Health
				graphics.setColor(config.healthColor());
				break;
			case 1:
				// Prayer
				graphics.setColor(config.prayerColor());
				break;
			case 2:
				graphics.setColor(config.absorptionColor());
				break;
		}

		//log.info( "Heigth: " + image.getHeight() + " Calc: " + Integer.toString(image.getHeight() * (client.getBoostedSkillLevel(Skill.PRAYER) / client.getRealSkillLevel(Skill.PRAYER))));
		//graphics.fillRect ( 0, 0, image.getWidth(), (image.getHeight() * (client.getBoostedSkillLevel(Skill.PRAYER) / client.getRealSkillLevel(Skill.PRAYER))));
		graphics.fillRect ( 0, 0, image.getWidth(), image.getHeight());

		if(client.getLocalPlayer() != null) {
			String text = "";
			switch(i) {
				case 0:
					text = Integer.toString(client.getBoostedSkillLevel(Skill.HITPOINTS));
					graphics.setColor(new Color ( 0, 0, 0 ));
					break;
				case 1:
					text = Integer.toString(client.getBoostedSkillLevel(Skill.PRAYER));
					graphics.setColor(new Color ( 0, 0, 0 ));
					break;
				case 2:
					if(client.getVar(Varbits.NMZ_ABSORPTION) == 1000)
						graphics.setFont(new Font(graphics.getFont().getName(), Font.PLAIN, 8));
					else if(client.getVar(Varbits.NMZ_ABSORPTION) >= 100)
						graphics.setFont(new Font(graphics.getFont().getName(), Font.PLAIN, 9));


					text = Integer.toString(client.getVar(Varbits.NMZ_ABSORPTION));
					graphics.setColor(new Color ( 0, 0, 0 ));
					break;
			}

			FontMetrics metrics = graphics.getFontMetrics();
			int x = (image.getWidth() - metrics.stringWidth(text)) / 2;
			int y = ((image.getWidth() - metrics.getHeight()) / 2) + metrics.getAscent();

			graphics.drawString(text, x, y);
		}

		return image;
	}

	public boolean isInNightmareZone()
	{
		return Arrays.equals(client.getMapRegions(), NMZ_MAP_REGION);
	}

	public boolean indexExists(final List list, final int index) {
		return index >= 0 && index < list.size();
	}

	public void removeTrayIcon(int i){
		if(!indexExists(trayIcons, i))
		{
			log.info("Index: '" + i + "' does not exist for trayIcons ;(");
			return;
		}

		SystemTray systemTray = SystemTray.getSystemTray();
		TrayIcon trayIcon = trayIcons.get(i);
		systemTray.remove(trayIcon);
		trayIcons.remove(i);
	}

	public void removeAllTrayIcons(){
		SystemTray systemTray = SystemTray.getSystemTray();

		for (int i=0; i < trayIcons.size(); i++)
		{
			TrayIcon trayIcon = trayIcons.get(i);
			systemTray.remove(trayIcon);
		}

		trayIcons.removeAll(trayIcons);
	}
}
