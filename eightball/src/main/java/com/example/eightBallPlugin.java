package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Slf4j
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
	List<String> activityList = Arrays.asList("Kill {number} {creature}s", "Train {skill} for {number} levels.", "Do an easy clue scroll", "Commit die", "Make an Energy Potion", "Talk to {number} random NPCs", "Do inferno blindfolded", "Casually commit cow genocide", "Buy a Party Hat", "Buy 13 bronze daggers (No, I didn't randomize this one)");
	List<String> enemyList = Arrays.asList("Hill Giant", "Goblin", "Cow", "Wizard", "Giant Spider", "Rat", "Barbarian");
	List<String> choiceList = Arrays.asList("Give away 500k gp to a random", "Send a copypasta of the Bee Movie on the RuneLite Discord", "Commit die", "PK someone level 80+", "Fight the Corporeal Beast solo right now (No banking)", "Buy 69 Bronze Daggers", "Get a fire cape with your current gear", "Train Runecrafting for 30 levels", "Make a HCIM account right now", "Walk to the GE barefoot");
	@Inject
	private Client client;

//	public void onGameStateChanged(GameStateChanged gameStateChanged)
//	{
//		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
//		{
//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
//		}
//	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted){
		if (commandExecuted.getCommand().equals("8ball")) {
			Random rand = new Random();
			if (commandExecuted.getArguments().length != 0 && commandExecuted.getArguments()[0].equals("skill"))
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: " + skillList.get(rand.nextInt(skillList.size())), null);
			}
			else if (commandExecuted.getArguments().length != 0 && commandExecuted.getArguments()[0].equals("quest")){
				List whatToDo = new ArrayList();
				for (int j = 0; j < (int)((Math.random()*((10-1)+1))+1); j++) {
					Random rand2 = new Random();
					String element = activityList.get(rand2.nextInt(activityList.size())).replace("{number}", Integer.toString((int)(Math.random()*((20-1)+1))+1));
					if (element.contains("{creature}")){
						element = element.replace("{creature}", enemyList.get(rand2.nextInt(enemyList.size())));
					}
					if (element.contains("{skill}")){
						element = element.replace("{skill}", skillList.get(rand2.nextInt(skillList.size())));
					}
					whatToDo.add(element);
				}
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: " + whatToDo.toString().substring(1, whatToDo.toString().length() - 1), null);

			} else if (commandExecuted.getArguments().length != 0 && commandExecuted.getArguments()[0].equals("choice")){
				Random rand1 = new Random();
				Random rand2 = new Random();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: Would you rather: " + choiceList.get(rand1.nextInt(choiceList.size())) + " OR " + choiceList.get(rand2.nextInt(choiceList.size())) + "?", null);
			}
			else {
				switch (WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID()) {
					case 0:
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: Wtf dude how are you even here-", null);
						break;
					case 12597:
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: Drink the forbidden berry fruit punch, I double dare ya.", null);
						break;
					default:
						client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "The 8ball says: " + answerList.get(rand.nextInt(answerList.size())), null);
						break;
				}
			}
		}
	}
}