package com.preemptive;

import com.google.inject.Provides;
import javax.inject.Inject;

import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Preemptive Strike",
	description = "Allows using ::kick [user] to create a fake message from a user to kick from chat.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class PreemptivePlugin extends Plugin
{
	@Inject
	private Client client;

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted) {
		if (commandExecuted.getCommand().equals("kick")) {
			this.client.addChatMessage(ChatMessageType.FRIENDSCHAT, Strings.join(commandExecuted.getArguments(), " "), "Kick me", "Preemptive Strike");
		}
	}
}
