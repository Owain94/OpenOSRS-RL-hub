package net.runelite.client.plugins.clanchatwarnings;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ClanMember;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.events.ClanChanged;
import net.runelite.api.events.ClanMemberJoined;
import net.runelite.api.events.ClanMemberLeft;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.PlayerMenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Clan Chat Warnings",
	description = "Notifies you when players join clan chat. Supports adding notes to signal why you put them on the watchlist",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class ClanChatWarningsPlugin extends Plugin
{
	private static final Splitter NEWLINE_SPLITTER = Splitter.on("\n").omitEmptyStrings().trimResults();
	private static final ImmutableList<String> AFTER_OPTIONS = ImmutableList.of("Message", "Add ignore", "Remove friend", "Kick");
	private final List<Pattern> warnings;
	private final List<Pattern> warnPlayers;
	private final List<Pattern> exemptPlayers;
	private final List<Integer> coolTimer;
	private final List<String> coolName;
	private final List<Integer> trackTimer;
	private final List<String> trackName;
	private final List<String> clansName;
	private boolean hopping;
	private int clanJoinedTick;
	@Inject
	private Client client;
	@Inject
	private Notifier ping;
	@Inject
	private ClanChatWarningsConfig config;

	public ClanChatWarningsPlugin()
	{
		this.warnings = new ArrayList();
		this.exemptPlayers = new ArrayList();
		this.warnPlayers = new ArrayList();
		this.coolTimer = new ArrayList();
		this.coolName = new ArrayList();
		this.trackTimer = new ArrayList();
		this.trackName = new ArrayList();
		this.clansName = new ArrayList();
	}

	@Override
	protected void startUp()
	{
		this.updateSets();
	}

	@Override
	protected void shutDown()
	{
		this.warnings.clear();
		this.exemptPlayers.clear();
		this.warnPlayers.clear();
		this.coolTimer.clear();
		this.coolName.clear();
		this.trackTimer.clear();
		this.trackName.clear();
		this.clansName.clear();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		//If you or someone you love is able to figure out how to only have this enabled for clan and private chat, hit a Turtle up.
		if (true && this.config.track())
		{
			int groupId = WidgetInfo.TO_GROUP(event.getParam1());
			String option = event.getOption();
			if (groupId == WidgetInfo.CHATBOX.getGroupId() && !"Kick".equals(option) || groupId == WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId())
			{
				if (!AFTER_OPTIONS.contains(option))
				{
					return;
				}
				MenuEntry track = new MenuEntry();
				track.setOption("Track Player");
				track.setTarget(event.getTarget());
				track.setOpcode(MenuOpcode.RUNELITE.getId());
				track.setParam0(event.getParam0());
				track.setParam1(event.getParam1());
				track.setIdentifier(event.getIdentifier());
				this.insertMenuEntry(track, this.client.getMenuEntries());
			}
		}
	}

	private void insertMenuEntry(MenuEntry newEntry, MenuEntry[] entries)
	{
		MenuEntry[] newMenu = ObjectArrays.concat(entries, newEntry);
		int menuEntryCount = newMenu.length;
		ArrayUtils.swap(newMenu, menuEntryCount - 1, menuEntryCount - 2);
		this.client.setMenuEntries(newMenu);
	}

	@Subscribe
	public void onPlayerMenuOptionClicked(PlayerMenuOptionClicked event)
	{
		if (event.getMenuOption().equals("Track Player"))
		{
			for (String name : clansName)
			{
				if (event.getMenuTarget().toLowerCase().contains(name.toLowerCase()))
				{
					if (trackName.contains(name.toLowerCase()))
					{
						trackTimer.set(trackName.indexOf(name.toLowerCase()), (int) (this.config.trackerLength() / .6));
					}
					else
					{
						trackName.add(name.toLowerCase());
						trackTimer.add((int) (this.config.trackerLength() / .6));
					}
				}
			}
		}
	}

	void updateSets()
	{
		this.warnings.clear();
		this.exemptPlayers.clear();
		this.warnPlayers.clear();

		Stream var10000 = Text.fromCSV(this.config.warnPlayers()).stream().map((s) -> {
			return Pattern.compile(Pattern.quote(s), 2);
		});
		List var10001 = this.warnPlayers;
		var10000.forEach(var10001::add);
		Stream var10002 = NEWLINE_SPLITTER.splitToList(this.config.warnings()).stream().map((s) -> {
			try
			{
				return Pattern.compile(s, 2);
			}
			catch (PatternSyntaxException var2)
			{
				return null;
			}
		}).filter(Objects::nonNull);
		List var10003 = this.warnings;
		var10002.forEach(var10003::add);
		Stream var10004 = Text.fromCSV(this.config.exemptPlayers()).stream().map((s) -> {
			return Pattern.compile(Pattern.quote(s), 2);
		});
		List var10005 = this.exemptPlayers;
		var10004.forEach(var10005::add);
	}


	private void sendNotification(String player, String Comment, int type)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if (type == 1)
		{
			stringBuilder.append("has joined Clan Chat. ").append(Comment);
			String notification = stringBuilder.toString();
			if (this.config.kickable())
			{
				this.client.addChatMessage(ChatMessageType.FRIENDSCHAT, player, notification, "Warning");
			}
			else
			{
				this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", player + " " + notification, "");
			}
			if (this.config.warnedAttention())
			{
				if (this.clanJoinedTick != this.client.getTickCount() || this.config.selfPing())
				{
					this.ping.notify(player + " " + notification);
				}
			}
		}
		else if (type == 2)
		{
			stringBuilder.append(" has left Clan Chat.");
			String notification = stringBuilder.toString();
			this.client.addChatMessage(ChatMessageType.FRIENDSCHATNOTIFICATION, "", player + " " + notification, "");
			if (this.config.trackerPing())
			{
				this.ping.notify(player + " " + notification);
			}
		}
	}

	@Subscribe
	public void onClanMemberLeft(ClanMemberLeft event)
	{
		String name = event.getMember().getUsername();
		if (trackName.contains(name.toLowerCase()))
		{
			sendNotification(Text.toJagexName(name), "", 2);
			trackTimer.remove(trackName.indexOf(name.toLowerCase()));
			trackName.remove(name.toLowerCase());
		}
		clansName.remove(name);
	}

	@Subscribe
	public void onClanMemberJoined(ClanMemberJoined event)
	{
		if (this.config.track())
		{
			clansName.add(event.getMember().getUsername());
		}
		if (this.clanJoinedTick != this.client.getTickCount())
		{
			hopping = false;
		}
		//God have mercy on your soul if you're about to check how I did this.
		if ((this.clanJoinedTick != this.client.getTickCount() || this.config.selfCheck()) && !hopping)
		{
			ClanMember member = event.getMember();
			String memberNameX = Text.toJagexName(member.getUsername());
			String memberNameP = Text.toJagexName(member.getUsername());
			String memberNameR = Text.toJagexName(member.getUsername());
			StringBuffer sx;
			StringBuffer sp;
			StringBuffer sr;
			if (memberNameX.equalsIgnoreCase(Text.toJagexName(this.client.getLocalPlayer().getName())))
			{
				return;
			}
			if (coolName.contains(member.getUsername()))
			{
				return;
			}
			for (Iterator var2 = this.exemptPlayers.iterator(); var2.hasNext(); memberNameX = sx.toString())
			{ //For exempting people from being pinged
				Pattern pattern = (Pattern) var2.next();
				Matcher n = pattern.matcher(memberNameX.toLowerCase());
				sx = new StringBuffer();
				while (n.matches())
				{
					if (pattern.toString().substring(2, pattern.toString().length() - 2).equalsIgnoreCase(memberNameX.toLowerCase()))
					{
						return;
					}
				}
				n.appendTail(sx);
			}
			for (Iterator var4 = this.warnPlayers.iterator(); var4.hasNext(); memberNameP = sp.toString())
			{ //For checking specific players
				Pattern pattern = (Pattern) var4.next();
				Pattern patternDiv = Pattern.compile("~");
				//Yes Im aware this String is dumb, frankly I spent 20 mins trying to fix it and this is what worked so it stays.
				String slash = "\\\\";
				String[] sections = patternDiv.split(pattern.toString());
				String note = "";
				String nameP = "";
				if (sections.length > 1)
				{
					for (Integer x = 0; x < sections.length; x++)
					{
						if (x == sections.length - 1)
						{
							String[] notes = sections[x].split(slash);
							if (x > 1)
							{
								note += "~";
							}
							note += notes[0];
						}
						else if (x != 0)
						{
							if (x > 1)
							{
								note += "~";
							}
							note += sections[x];
						}
					}
				}
				if (sections.length == 1)
				{
					nameP = sections[0].substring(2, sections[0].length() - 2);
					nameP = nameP.trim();
				}
				else
				{
					nameP = sections[0].substring(2, sections[0].trim().length());
				}
				pattern = Pattern.compile(nameP.toLowerCase());
				Matcher l = pattern.matcher(memberNameP.toLowerCase());
				sp = new StringBuffer();
				while (l.matches())
				{
					if (nameP.equalsIgnoreCase(memberNameP))
					{
						sendNotification(Text.toJagexName(member.getUsername()), note, 1);
						if (this.config.cooldown() > 0)
						{
							coolName.add(Text.toJagexName(member.getUsername()));
							coolTimer.add((int) (this.config.cooldown() / .6));
						}
						break;
					}
				}
				l.appendTail(sp);
			}
			for (Iterator var3 = this.warnings.iterator(); var3.hasNext(); memberNameR = sr.toString())
			{ //For checking the regex
				Pattern pattern = (Pattern) var3.next();
				Pattern patternDiv = Pattern.compile("~");
				//Yes Im aware this String is dumb, frankly I spent 20 mins trying to fix it and this is what worked so it stays.
				String slash = "\\\\";
				String[] sections = patternDiv.split(pattern.toString());
				String note = "";
				if (sections.length > 1)
				{
					for (Integer x = 0; x < sections.length; x++)
					{
						if (x == sections.length - 1)
						{
							String[] notes = sections[x].split(slash);
							if (x > 1)
							{
								note += "~";
							}
							note += notes[0];
							pattern = Pattern.compile(sections[0].trim().toLowerCase());
						}
						else if (x != 0)
						{
							if (x > 1)
							{
								note += "~";
							}
							note += sections[x];
						}
					}
				}
				Matcher m = pattern.matcher(memberNameR.toLowerCase());
				sr = new StringBuffer();
				while (m.find())
				{
					sendNotification(Text.toJagexName(member.getUsername()), note, 1);
					if (this.config.cooldown() > 0)
					{
						coolName.add(Text.toJagexName(member.getUsername()));
						coolTimer.add((int) (this.config.cooldown() / .6));
					}
					break;
				}
				m.appendTail(sr);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (this.config.cooldown() > 0)
		{
			for (int i = 0; i < coolTimer.size(); i++)
			{
				if (coolTimer.get(i) > 0)
				{
					coolTimer.set(i, coolTimer.get(i) - 1);
				}
				else
				{
					coolTimer.remove(i);
					coolName.remove(i);
				}
			}
		}
		if (!trackName.isEmpty())
		{
			for (int i = 0; i < trackTimer.size(); i++)
			{
				if (trackTimer.get(i) > 0)
				{
					trackTimer.set(i, trackTimer.get(i) - 1);
				}
				else
				{
					trackTimer.remove(i);
					trackName.remove(i);
				}
			}
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("ClanChatPlus"))
		{
			this.updateSets();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.HOPPING)
		{
			hopping = true;
		}
	}

	@Subscribe
	public void onClanChanged(ClanChanged event)
	{
		if (event.isJoined())
		{
			this.clanJoinedTick = this.client.getTickCount();
		}
	}

	@Provides
	ClanChatWarningsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClanChatWarningsConfig.class);
	}
}