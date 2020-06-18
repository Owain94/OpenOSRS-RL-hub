package net.runelite.client.plugins.eightball;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.CommandExecuted;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "8ball",
	description = "Leave the hardest choices to the 8ball! Just do \"::8ball\" in the chat to use it!",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class eightBallPlugin extends Plugin
{
	List<String> answerList = Arrays.asList("Definitely.", "Nope.", "Yes.", "Nah.", "Ask me later.", "All I can say is yes.", "Pretty sure it's a yes.", "Pretty sure it's a no.", "I can't answer that.", "That is never going to happen.", "You are legit asking this to an rng plugin?", "Maybe.", "This will 100% happen.", "Surely.", "The rng Gods will smile to you.", "Bruh, of course not lmaoooo");
	List<String> skillList = Arrays.asList("Attack", "Strength", "Defense", "Ranged", "Prayer", "Magic", "Runecraft, let me see you   s u f f e r", "Hitpoints", "Crafting", "Mining", "Smithing", "Fishing", "Cooking", "Firemaking", "Woodcutting");

	@Inject
	private Client client;

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		if (commandExecuted.getCommand().equals("8ball"))
		{
			Random rand = new Random();
			if (commandExecuted.getArguments().length != 0 && commandExecuted.getArguments()[0].equals("skill"))
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: " + skillList.get(rand.nextInt(skillList.size())), null);
			}
			else if (WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == 0)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: Wtf dude how are you even here-", null);
			}
			else if (WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == 12597)
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: Drink the forbidden berry fruit punch, I double dare ya.", null);
			}
			else
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: " + answerList.get(rand.nextInt(answerList.size())), null);
			}
		}
	}
}