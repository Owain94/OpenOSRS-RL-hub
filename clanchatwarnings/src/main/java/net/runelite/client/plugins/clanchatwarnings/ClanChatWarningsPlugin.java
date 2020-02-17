package net.runelite.client.plugins.clanchatwarnings;

import com.google.common.base.Splitter;
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
import net.runelite.api.events.ClanChanged;
import net.runelite.api.events.ClanMemberJoined;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.util.Text;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Clan Chat Warnings",
	description = "Notifies you when players join clan chat. Supports adding notes to signal why you put them on the watchlist",
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class ClanChatWarningsPlugin extends Plugin
{
	private static final Splitter NEWLINE_SPLITTER = Splitter.on("\n").omitEmptyStrings().trimResults();
	private final List<Pattern> warnings;
	private final List<Pattern> warnPlayers;
	private final List<Pattern> exemptPlayers;

	@Inject
	private Client client;

	@Inject
	private Notifier ping;

	@Inject
	private ClanChatWarningsConfig config;

	private boolean hopping;
	private int clanJoinedTick;

	public ClanChatWarningsPlugin()
	{
		this.warnings = new ArrayList();
		this.exemptPlayers = new ArrayList();
		this.warnPlayers = new ArrayList();
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
	}

	void updateSets()
	{
		this.warnings.clear();
		this.exemptPlayers.clear();
		this.warnPlayers.clear();

		Stream var10000 = Text.fromCSV(this.config.warnPlayers()).stream().map((s) -> Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE));
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


	private void sendNotification(String player, String Comment)
	{
		String notification = "has joined Clan Chat. " + Comment;
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
				this.ping.notify(notification);
			}
		}
	}

	@Subscribe
	public void onClanMemberJoined(ClanMemberJoined event)
	{
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
			for (Iterator<Pattern> var2 = this.exemptPlayers.iterator(); var2.hasNext(); memberNameX = sx.toString())
			{ //For exempting people from being pinged
				Pattern pattern = (Pattern) var2.next();
				Matcher n = pattern.matcher(memberNameX.toLowerCase());
				sx = new StringBuffer();
				while (n.matches())
				{
					if (pattern.toString().substring(2, pattern.toString().length() - 2).toLowerCase().equals(memberNameX.toLowerCase()))
					{
						return;
					}
				}
				n.appendTail(sx);
			}
			for (Iterator<Pattern> var4 = this.warnPlayers.iterator(); var4.hasNext(); memberNameP = sp.toString())
			{ //For checking specific players
				Pattern pattern = (Pattern) var4.next();
				Pattern patternDiv = Pattern.compile("~");
				//Yes Im aware this String is dumb, frankly I spent 20 mins trying to fix it and this is what worked so it stays.
				String slash = "\\\\";
				String[] sections = patternDiv.split(pattern.toString());
				StringBuilder note = new StringBuilder();
				String nameP = "";
				if (sections.length > 1)
				{
					for (int x = 0; x < sections.length; x++)
					{
						if (x == sections.length - 1)
						{
							String[] notes = sections[x].split(slash);
							if (x > 1)
							{
								note.append("-");
							}
							note.append(notes[0]);
						}
						else if (x != 0)
						{
							if (x > 1)
							{
								note.append("-");
							}
							note.append(sections[x]);
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
					if (nameP.toLowerCase().equals(memberNameP.toLowerCase()))
					{
						sendNotification(Text.toJagexName(member.getUsername()), note.toString());
						break;
					}
				}
				l.appendTail(sp);
			}
			for (Iterator<Pattern> var3 = this.warnings.iterator(); var3.hasNext(); memberNameR = sr.toString())
			{ //For checking the regex
				Pattern pattern = (Pattern) var3.next();
				Pattern patternDiv = Pattern.compile("~");
				//Yes Im aware this String is dumb, frankly I spent 20 mins trying to fix it and this is what worked so it stays.
				String slash = "\\\\";
				String[] sections = patternDiv.split(pattern.toString());
				StringBuilder note = new StringBuilder();
				if (sections.length > 1)
				{
					for (int x = 0; x < sections.length; x++)
					{
						if (x == sections.length - 1)
						{
							String[] notes = sections[x].split(slash);
							if (x > 1)
							{
								note.append("-");
							}
							note.append(notes[0]);
							pattern = Pattern.compile(sections[0].trim().toLowerCase());
						}
						else if (x != 0)
						{
							if (x > 1)
							{
								note.append("-");
							}
							note.append(sections[x]);
						}
					}
				}
				Matcher m = pattern.matcher(memberNameR.toLowerCase());
				sr = new StringBuffer();
				if (m.find())
				{
					sendNotification(Text.toJagexName(member.getUsername()), note.toString());
					break;
				}
				m.appendTail(sr);
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