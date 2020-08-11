package net.runelite.client.plugins.npcoverheaddialogue;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.npcoverheaddialogue.dialog.DialogNpc;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "NPC Overhead Dialog",
	description = "Displays NPC dialog over their head, includes custom ambient, walking, damage, and death dialog as well.",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class NPCOverheadDialoguePlugin extends Plugin
{
	private static final int FAST_TICK_TIMEOUT = 2;
	private static final int SLOW_TICK_TIMEOUT = 5;
	private static final int MOVING_TICK_DELAY = 2;
	private static final int AMBIENT_TICK_TIMEOUT = 15; // 9 seconds
	private static final Random RANDOM = new Random();

	@Inject
	private Client client;

	@Inject
	private NPCOverheadDialogueConfig config;

	private final Map<Actor, ActorDialogState> dialogStateMap = new HashMap<>();
	private Actor actor = null;
	private String lastNPCText = "";
	private int actorTextTick = 0;
	private String lastPlayerText = "";
	private int playerTextTick = 0;

	@Provides
	NPCOverheadDialogueConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NPCOverheadDialogueConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged c)
	{
		if (!c.getGameState().equals(GameState.LOADING))
		{
			return;
		}

		actor = null;
		lastNPCText = "";
		lastPlayerText = "";
		dialogStateMap.clear();
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		if (event.getTarget() == null || event.getSource() != client.getLocalPlayer())
		{
			return;
		}
		if (config.showDialogBoxText() && event.getSource() != actor && actor != null)
		{
			actor.setOverheadText(null);
		}
		lastNPCText = "";
		lastPlayerText = "";
		actor = event.getTarget();
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		//log.info("animation changed for: " + animationChanged.getActor().getName());
		if (!(animationChanged.getActor() instanceof NPC))
		{
			return;
		}

		final NPC npc = (NPC) animationChanged.getActor();
		if (config.showDeathDialog() && npc.isDead() && npc.getName() != null)
		{
			//for death text, best option for slayer item killed monsters
			//final DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(npc.getName()));
			DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcID(npc.getId());

			if (dialogNpc == null)
			{
				dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(Objects.requireNonNullElse(npc.getName(), "")));
			}
			if (dialogNpc == null)
			{
				return;
			}

			final ActorDialogState state = getOrCreateActorDialogState(npc);
			if (state == null)
			{
				return;
			}

			final String[] dialogues = dialogNpc.getDeathDialogs();
			if (dialogues == null)
			{
				return;
			}

			setOverheadText(dialogues[RANDOM.nextInt(dialogues.length)], npc, state);
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor().getName() == null || !(event.getActor() instanceof NPC))
		{
			return;
		}

		//final DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(event.getActor().getName()));
		DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcID(((NPC) event.getActor()).getId());

		if (dialogNpc == null)
		{
			dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(Objects.requireNonNullElse(event.getActor().getName(), "")));
		}
		if (dialogNpc == null)
		{
			return;
		}

		final NPC npc = (NPC) event.getActor();
		final ActorDialogState state = getOrCreateActorDialogState(npc);
		// If state is null that means we aren't tracking this npc
		if (state == null)
		{
			return;
		}

		if (config.showDeathDialog() && npc.isDead())
		{
			final String[] dialogues = dialogNpc.getDeathDialogs();
			if (dialogues != null)
			{
				setOverheadText(dialogues[RANDOM.nextInt(dialogues.length)], npc, state);
				state.setInCombat(true);
				// return here so damage text doesn't get applied since death dialog exists
				return;
			}
		}

		if (config.showDamageDialog() && event.getHitsplat().getAmount() > 0)
		{
			final String[] dialogues = dialogNpc.getDamageDialogs();
			if (dialogues == null)
			{
				return;
			}

			setOverheadText(dialogues[RANDOM.nextInt(dialogues.length)], npc, state);
			state.setInCombat(true);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (actor != null)
		{
			if (config.showDialogBoxText())
			{
				checkWidgetDialogs();
			}
			if (actorTextTick > 0 && client.getTickCount() - actorTextTick > SLOW_TICK_TIMEOUT)
			{
				actor.setOverheadText(null);
				actorTextTick = 0;
			}
		}

		if (client.getLocalPlayer() != null && playerTextTick > 0 && client.getTickCount() - playerTextTick > SLOW_TICK_TIMEOUT)
		{
			client.getLocalPlayer().setOverheadText(null);
			playerTextTick = 0;
		}

		npcTextInvoker();

		final int currentTick = client.getTickCount();
		for (final Map.Entry<Actor, ActorDialogState> entry : dialogStateMap.entrySet())
		{
			final Actor actor = entry.getKey();
			final ActorDialogState state = entry.getValue();

			final int activeTicks = state.getDialogChangeTick() > 0 ? currentTick - state.getDialogChangeTick() : -1;
			if (state.getDialog() == null
				|| (state.isInCombat() && activeTicks > FAST_TICK_TIMEOUT)
				|| (!state.isInCombat() && activeTicks > SLOW_TICK_TIMEOUT))
			{
				if (!Objects.equals(state.getDialog(), actor.getOverheadText()))
				{
					state.setDialogChangeTick(client.getTickCount());
				}
				actor.setOverheadText(null);
				state.setDialog(null);
				state.setInCombat(false);
			}
			else
			{
				setOverheadText(state.getDialog(), actor, state);
			}
		}
	}

	// checks all local NPCs movement/idle timeout and applies an overhead message if necessary, Uses Ambient dialogues
	private void npcTextInvoker()
	{
		for (final NPC npc : client.getNpcs())
		{
			DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcID(npc.getId());

			if (dialogNpc == null)
			{
				dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(Objects.requireNonNullElse(npc.getName(), "")));
			}
			if (dialogNpc == null)
			{
				continue;
			}

			final ActorDialogState state = getOrCreateActorDialogState(npc);
			// If state is null that means we aren't tracking this npc
			if (state == null)
			{
				continue;
			}

			if (config.showWalkingDialog())
			{
				checkWalkingDialog(npc, state);
			}
			if (config.showAmbientDialog())
			{
				checkAmbientDialog(npc, state);
			}
		}
	}

	private void checkWalkingDialog(final NPC npc, final ActorDialogState state)
	{
		DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcID(npc.getId());

		if (dialogNpc == null)
		{
			dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(Objects.requireNonNullElse(npc.getName(), "")));
		}
		if (dialogNpc == null)
		{
			return;
		}

		if (hasNpcMoved(npc))
		{
			//log.info(npc + " has moved");


			final String[] dialogues = dialogNpc.getWalkingDialogs();
			if (dialogues == null)
			{
				return;
			}

			final String dialogue = dialogues[RANDOM.nextInt(dialogues.length)];
			//log.info(state.getTicksWithoutMoving() + " ticks without moving, and " + MOVING_TICK_DELAY + " is the delay");
			if (state.getTicksWithoutMoving() > MOVING_TICK_DELAY)
			{
				setOverheadText(dialogue, npc, state);
				state.setTicksWithoutMoving(0);
				log.debug("set " + npc + " overhead text");
			}
		}
		else
		{
			state.setTicksWithoutMoving(state.getTicksWithoutMoving() + 1);
			log.debug("state's ticks without moving has been incremented to " + state.getTicksWithoutMoving());
		}
	}

	private void checkAmbientDialog(final NPC npc, final ActorDialogState state)
	{
		DialogNpc dialogNpc = DialogNpc.getDialogNpcsByNpcName(Text.escapeJagex(Objects.requireNonNullElse(npc.getName(), "")));
		if (dialogNpc == null)
		{
			return;
		}

		final String[] dialogues = dialogNpc.getAmbientDialogs();
		if (dialogues == null)
		{
			return;
		}

		if ((client.getTickCount() - state.getDialogChangeTick()) >= AMBIENT_TICK_TIMEOUT
			&& (RANDOM.nextInt(100) + 1) <= 1)
		{
			final String dialogue = dialogues[RANDOM.nextInt(dialogues.length)];
			setOverheadText(dialogue, npc, state);
		}
	}

	private void checkWidgetDialogs()
	{
		final String npcDialogText = getWidgetTextSafely(WidgetInfo.DIALOG_NPC_TEXT);
		final String playerDialogText = getWidgetTextSafely(WidgetID.DIALOG_PLAYER_GROUP_ID, 4);

		// For when the NPC has dialog
		if (npcDialogText != null && !lastNPCText.equals(npcDialogText))
		{
			lastNPCText = npcDialogText;
			actor.setOverheadText(npcDialogText);
			actorTextTick = client.getTickCount();
		}

		//For when your player has dialogue
		if (playerDialogText != null && !lastPlayerText.equals(playerDialogText))
		{
			lastPlayerText = playerDialogText;
			if (client.getLocalPlayer() != null)
			{
				client.getLocalPlayer().setOverheadText(playerDialogText);
				playerTextTick = client.getTickCount();
			}
		}
	}

	private boolean hasNpcMoved(NPC npc)
	{
		final ActorDialogState state = getOrCreateActorDialogState(npc);
		// If state is null that means we aren't tracking this npc
		if (state == null)
		{
			return false;
		}

		final WorldPoint npcPos = npc.getWorldLocation();
		final WorldPoint lastNpcPos = new WorldPoint(state.getLastXCoordinate(), state.getLastYCoordinate(), -1);
		log.debug("npc has moved? : " + (npcPos.distanceTo2D(lastNpcPos) > 0) + " : " + npcPos.distanceTo2D(lastNpcPos));
		state.setLastXCoordinate(npc.getWorldLocation().getX());
		state.setLastYCoordinate(npc.getWorldLocation().getY());
		return npcPos.distanceTo2D(lastNpcPos) > 0;
	}

	@Nullable
	private ActorDialogState getOrCreateActorDialogState(final NPC npc)
	{
		if (npc.getName() == null || !DialogNpc.isDialogNpc(Text.escapeJagex(npc.getName())))
		{
			return null;
		}

		ActorDialogState result = dialogStateMap.get(npc);
		if (result == null)
		{
			result = new ActorDialogState(
				npc, Text.escapeJagex(npc.getName()), npc.getOverheadText(),
				npc.getOverheadText() == null ? 0 : client.getTickCount(),
				npc.getWorldLocation().getX(), npc.getWorldLocation().getY(),
				2, false);
			dialogStateMap.put(npc, result);
		}

		return result;
	}

	private void setOverheadText(final String dialogue, final Actor actor, final ActorDialogState state)
	{
		if (state.getDialogChangeTick() <= 0 || !Objects.equals(state.getDialog(), dialogue))
		{
			state.setDialogChangeTick(client.getTickCount());
		}
		state.setDialog(dialogue);
		actor.setOverheadText(dialogue);
	}

	private String getWidgetTextSafely(final WidgetInfo info)
	{
		return getWidgetTextSafely(info.getGroupId(), info.getChildId());
	}

	private String getWidgetTextSafely(final int group, final int child)
	{
		return client.getWidget(group, child) == null ? null : Text.sanitizeMultilineText(client.getWidget(group, child).getText());
	}
}