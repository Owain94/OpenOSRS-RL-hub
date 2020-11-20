/*
 * Copyright (c) 2020, RKGman
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

package com.music.firebeats;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.util.stream.Collectors;

import jaco.mp3.player.MP3Player;
import net.runelite.client.util.ImageUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Fire Beats",
	description = "Plays remixes of the current track being played in game.",
	tags = {"music"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class FireBeatsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private FireBeatsConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private FireBeatsOverlay overlay;

	private final static Map<String, String> DOMAIN_WHITELIST_MAP =
			createDomainWhitelistMap("Domain-Whitelist.csv");

	private final static String TRACK_LIST_REPO =
			"https://raw.githubusercontent.com/RKGman/fire-beats/master/src/main/resources/Osrs-Track-Remix-List.csv";

	private final int FADING_TRACK_STATE = 0;

	private final int PLAYING_TRACK_STATE = 1;

	private int currentPlayerState = PLAYING_TRACK_STATE;

	private NavigationButton navButton;

	private FireBeatsPanel panel;

	private Widget currentTrackBox;

	private String previousTrack = "";

	private String nextTrack = "";

	private MP3Player trackPlayer = new MP3Player();

	private Thread handlePlayThread = null;

	private Map<String, Track> mp3Map = new HashMap<String, Track>();

	private ArrayList<String> availableTrackNameArray = new ArrayList<String>();

	private Random rng = new Random();

	private Collection<Widget> tracks = null;

	private boolean remixAvailable = false;

	private boolean changingTracks = false;

	private boolean initializeTrack = true;

	private boolean comingFromLogin = true;

	private static Map createDomainWhitelistMap(String whitelistResource)
	{
		Map<String, String> whitelist = new HashMap<String, String>();

		try
		{
			InputStream whitelistStream = FireBeatsPlugin.class.getClassLoader().getResourceAsStream(whitelistResource);

			BufferedReader br = new BufferedReader(new InputStreamReader(whitelistStream));

			String line = "";
			String delimiter = ",";
			boolean isHeader = true;

			while ((line = br.readLine()) != null)
			{
				if (isHeader == true)
				{
					isHeader = false;
					continue; // Ignore header
				}

				String[] domain = line.split(delimiter);
				whitelist.put(domain[0], domain[1]); // domain, name
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		return whitelist;
	}

	private boolean checkWhitelist(String url)
	{
		boolean rv = false;

		try
		{
			String domain = url.split("/")[2];

			if (DOMAIN_WHITELIST_MAP.containsKey(domain) == true)
			{
				rv = true;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		return rv;
	}

	private void buildMp3TrackMap()
	{
		try
		{
			// Check if track listing CSV exists.
			File trackFile = new File(RuneLite.RUNELITE_DIR, "Osrs-Track-Remix-List.csv");
			if(trackFile.exists() == false) {
				// Copy default track list from resources.
				String updatedCsv = getUpdatedListFromRepo();
				FileWriter writer = new FileWriter(trackFile.getPath());
				writer.write(updatedCsv);
				writer.close();
			}
			else if (config.updateFromRepo() == true)
			{
				updateListFromRepo(false);
			}

			String line = "";
			String delimiter = ",";

			BufferedReader br = new BufferedReader(new FileReader(trackFile.toPath().toString()));
			while ((line = br.readLine()) != null)   //returns a Boolean value
			{
				String[] track = line.split(delimiter);    // use comma as separator
				if (track.length == 1)
				{
					// System.out.println("Track: [Name=" + track[0] + "]");
					Track newTrack = new Track();
					newTrack.name = track[0];
					mp3Map.put(track[0], newTrack);
				}
				else if (track.length == 2)
				{
					// System.out.println("Track: [Name=" + track[0] + ", Link=" + track[1] + "]");
					Track newTrack = new Track();
					newTrack.name = track[0];
					newTrack.link = track[1];
					mp3Map.put(track[0], newTrack);
					availableTrackNameArray.add(track[0]);
				}
				else
				{
					// System.out.println("Track: [Name=" + track[0] + ", Link=" + track[1] + ", Credit=" + track[2] + "]");
					Track newTrack = new Track();
					newTrack.name = track[0];
					newTrack.link = track[1];
					newTrack.credit = track[2];
					mp3Map.put(track[0], newTrack);
					availableTrackNameArray.add(track[0]);
				}
			}

			log.info("Tracks successfully added to map.");
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}
	}

	private String getTrackLink(String anonFilesLink)
	{
		String link = "";

		try
		{
			// Only if in whitelist
			if (checkWhitelist(anonFilesLink) == true)
			{
				Document doc = Jsoup.connect(anonFilesLink).get();

				Element downloadUrl = doc.getElementById("download-url");

				link = downloadUrl.attr("href");

				link = link.replace(" ", "%20");

				log.info("Link: " + link);
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		return link;
	}

	private void fadeCurrentTrack()
	{
		if (trackPlayer.getVolume() == 0)
		{
			client.setMusicVolume(0);
			previousTrack = nextTrack;
			currentPlayerState = PLAYING_TRACK_STATE;
		}
		else
		{
			trackPlayer.setVolume(trackPlayer.getVolume() - 7);
			if (trackPlayer.getVolume() < 7)
			{
				trackPlayer.setVolume(0);
				trackPlayer.stop();
				previousTrack = nextTrack;
				currentPlayerState = PLAYING_TRACK_STATE;
				comingFromLogin = false;
			}
		}

	}

	private String getUpdatedListFromRepo()
	{
		String rv = "";

		try
		{
			// Only if in whitelist
			if (checkWhitelist(TRACK_LIST_REPO) == true)
			{
				URL url = new URL(TRACK_LIST_REPO);

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");

				BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));

				String inputLine;
				StringBuffer content = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine + "\n");
				}

				in.close();

				rv = content.toString();

				connection.disconnect();
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		return rv;
	}

	private boolean isOnMusicTab()
	{
		return client.getVar(VarClientInt.INTERFACE_TAB) == 13;
	}

	private void tagRemixedTracks()
	{
		final Widget musicList = client.getWidget(WidgetInfo.MUSIC_TRACK_LIST);

		if (tracks == null)
		{
			tracks = Arrays.stream(musicList.getDynamicChildren())
					.sorted(Comparator.comparing(Widget::getRelativeY))
					.collect(Collectors.toList());
		}

		for (Widget track : tracks)
		{
			Track mappedTrack = mp3Map.get(track.getText());
			if (mappedTrack != null && mappedTrack.link != null)
			{
				// The track can be played, mark cyan.
				track.setTextColor(Color.CYAN.getRGB());
				// TODO: Figure out how to mark tracks not unlocked.  getColor doesn't match with Color.red / green
			}
		}
	}

	private void playTrack(boolean repeat, boolean shuffle) {
		trackPlayer.getPlayList().clear();

		Track track = mp3Map.get(nextTrack);

		if (repeat == true)
		{
			track = mp3Map.get(previousTrack);
		}

		if (shuffle == true)
		{
			track = mp3Map.get(availableTrackNameArray.get(rng.nextInt(availableTrackNameArray.size())));
		}

		if (track != null && track.link != null)
		{
			remixAvailable = true;
			client.setMusicVolume(0);
			trackPlayer.setVolume(config.volume() - config.remixVolumeOffset());
			Track finalTrack = track;
			handlePlayThread = new Thread(() -> {
				try
				{
					// Get actual track link
					String directLink = getTrackLink(finalTrack.link);
					trackPlayer.addToPlayList(new URL(directLink));
					trackPlayer.play();
				}
				catch (Exception e)
				{
					log.error(e.getMessage());
				}
			});

			handlePlayThread.start();

			client.addChatMessage(ChatMessageType.GAMEMESSAGE,
					"",
					"Fire Beats Notice: " + track.name + " remix produced by " + track.credit,
					null);

			initializeTrack = false;
		}
		else
		{
			remixAvailable = false;
			if (config.playOriginalIfNoRemix() == true)
			{
				client.setMusicVolume(config.volume());
				initializeTrack = false;
			}
		}
	}

	@Override
	protected void startUp() throws Exception
	{
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/fire_beats_icon.png");

		panel = new FireBeatsPanel(this);

		navButton = NavigationButton.builder()
				.tooltip("Fire Beats")
				.icon(icon)
				.priority(50)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		// Build map of mp3 track links
		buildMp3TrackMap();

		overlayManager.add(overlay);

		log.info("Fire Beats started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Fire Beats stopped!");
	}

	@Subscribe
	public void onVarClientIntChanged(VarClientIntChanged varClientIntChanged)
	{
		if (isOnMusicTab() == true)
		{
			tagRemixedTracks();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			config.setMusicVolume(0);
		}
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			try
			{
				client.setMusicVolume(0); // Attempt to force mute.
				comingFromLogin = true;

				if (config.mute() == true)
				{
					trackPlayer.setVolume(0);
				}

				// Stop current track
				// trackPlayer.stop();
				trackPlayer.getPlayList().clear();
				// Start playing new track
				Track track = mp3Map.get("Scape Main");
				if (track.link != null)
				{
					remixAvailable = true;
					trackPlayer.setVolume(config.volume() - config.remixVolumeOffset());
					handlePlayThread = new Thread(() -> {
						try
						{
							// Get actual track link
							String directLink = getTrackLink(track.link);
							trackPlayer.addToPlayList(new URL(directLink));
							trackPlayer.play();
						}
						catch (Exception e)
						{
							log.error(e.getMessage());
						}
					});

					handlePlayThread.start();
				}
				else
				{
					remixAvailable = false;
					//  TODO: Handle playing normal song, or not
				}
			}
			catch (Exception e)
			{
				log.error(e.getMessage());
			}
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		if (widgetLoaded.getGroupId() == WidgetID.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX_GROUP_ID)
		{
			Widget viewport = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX);
			currentTrackBox = viewport.createChild(-1, WidgetType.TEXT);
		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			log.info("HELLO!!!");
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		final Widget currentTrack = client.getWidget(
				MusicWidgetInfo.MUSIC_CURRENT_TRACK.getGroupId(),
				MusicWidgetInfo.MUSIC_CURRENT_TRACK.getChildId());

		// If loop flag set, the player is loaded with music, and it is no longer playing, start again.
		if (config.loop() == true && trackPlayer.getPlayList().size() > 0 && trackPlayer.isPlaying() == false)
		{
			playTrack(true, false);
		}

		if (config.shuffleMode() == true && trackPlayer.getPlayList().size() > 0 && trackPlayer.isPlaying() == false)
		{
			shuffleNextTrack();
		}

		if (isOnMusicTab() == true)
		{
			tagRemixedTracks();
		}

		if (previousTrack != currentTrack.getText())
		{
			changingTracks = true;
			nextTrack = currentTrack.getText();
			currentPlayerState = FADING_TRACK_STATE;
			initializeTrack = true;
		}
		else
		{
			changingTracks = false;
		}


		try
		{
			if (changingTracks == true && currentPlayerState == FADING_TRACK_STATE)
			{
				if (config.shuffleMode() == false || comingFromLogin == true)
				{
					fadeCurrentTrack();
				}
			}
			else
			{
				if (initializeTrack == true)
				{
						playTrack(false, false);
				}
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

		if (config.mute() == true)
		{
			trackPlayer.setVolume(0);
			client.setMusicVolume(0);
		}
		else
		{
			if (remixAvailable == true)
			{ 	// If not in a fading state...
				if (currentPlayerState == PLAYING_TRACK_STATE)
				{
					// TODO: Make this not trash.
					if (trackPlayer.getVolume() < (config.volume() - config.remixVolumeOffset()))
					{
						trackPlayer.setVolume(trackPlayer.getVolume() + 7);
					}
					else if (trackPlayer.getVolume() > (config.volume() - config.remixVolumeOffset()))
					{
						trackPlayer.setVolume(config.volume() - config.remixVolumeOffset());
					}

					client.setMusicVolume(0);
				}
			}
			else
			{
				if (config.playOriginalIfNoRemix() == true)
				{
					trackPlayer.setVolume(0);
					client.setMusicVolume(config.volume());
				}
				else
				{
					trackPlayer.setVolume(0);
					client.setMusicVolume(0);
				}
			}
		}

		if (currentTrackBox != null)
		{
			currentTrackBox.setText(currentTrack.getText());

			if (mp3Map.get(currentTrack.getText()) != null && mp3Map.get(currentTrack.getText()).link != null)
			{
				currentTrack.setTextColor(Color.CYAN.getRGB());
			}
			else
			{
				currentTrack.setTextColor(Color.GREEN.getRGB());
			}
		}
	}

	@Provides
	FireBeatsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FireBeatsConfig.class);
	}

	public void updateListFromRepo(boolean updateMap)
	{
		try
		{
			// Check if track listing CSV exists.
			File trackFile = new File(RuneLite.RUNELITE_DIR, "Osrs-Track-Remix-List.csv");
			if(trackFile.exists() == false) {
				// Copy default track list from resources.
				String updatedCsv = getUpdatedListFromRepo();
				FileWriter writer = new FileWriter(trackFile.getPath());
				writer.write(updatedCsv);
				writer.close();
			}
			else
			{
				// Overwrite contents with new
				trackFile.delete();
				String updatedCsv = getUpdatedListFromRepo();
				FileWriter writer = new FileWriter(trackFile.getPath());
				writer.write(updatedCsv);
				writer.close();
			}

			// Reload map if necessary
			if (updateMap == true)
			{
				buildMp3TrackMap();
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage());
		}

	}

	public void shuffleNextTrack()
	{
		playTrack(false, true);
	}

	public Widget getCurrentTrackBox()
	{
		return currentTrackBox;
	}

	public FireBeatsConfig getMusicConfig()
	{
		return config;
	}
}

class Track
{
	public String name;
	public String link;
	public String credit;
}
