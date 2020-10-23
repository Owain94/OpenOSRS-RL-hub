/*
 * Copyright (c) 2020, Hippipi <https://github.com/hippipi>
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

package com.volcanicmine;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import java.time.Duration;
import java.time.Instant;
import org.pf4j.Extension;


@Extension
@PluginDescriptor(
        name = "Volcanic Mine",
        description = "Useful timers for when to start moving in Volcanic Mine",
        tags = {"volcanic", "mine", "vm", "mining", "timer", "warning"},
	enabledByDefault = false,
	type = PluginType.SKILLING
)
@Slf4j
public class VMPlugin extends Plugin
{
    // Chat messages
    private static final String CHAT_VM_START = "The volcano awakens! You can now access the area below...";
    private static final String PLATFORM_WARNING_MESSAGE = "The platform beneath you will disappear soon!";
    private static final String BOULDER_WARNING_MESSAGE = "The current boulder stage is complete.";
    // Constants
    private static final int PLATFORM_STAGE_3_ID = 31000;
    private static final int BOULDER_BREAK_STAGE_1_ID = 7807;
    private static final int BOULDER_BREAK_STAGE_2_ID = 7809;
    private static final int BOULDER_BREAK_STAGE_3_ID = 7811;
    private static final int BOULDER_BREAK_STAGE_4_ID = 7813;
    private static final int BOULDER_BREAK_STAGE_5_ID = 7815;
    private static final int VM_REGION_NORTH = 15263;
    private static final int VM_REGION_SOUTH = 15262;
    private static final Duration VM_FULL_TIME = Duration.ofMinutes(10);
    private static final Duration VM_HALF_TIME = Duration.ofMinutes(5);

    @Inject
    private Notifier notifier;

    @Inject
    private Client client;

    @Inject
    private VMConfig config;

    @Provides
    VMConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VMConfig.class);
    }

    // Timer variables
    private Duration timeUntilVentWarning;
    private Duration timeUntilEruptionWarning;
    private Instant VMTimer;
    // Event warning latches
    private boolean hasWarnedVent = false;
    private boolean hasWarnedEruption = false;

    private void reset()
    {
        VMTimer = null;
        hasWarnedVent = false;
        hasWarnedEruption = false;
    }

    private void calcVentWarningTime()
    {
        if (config.showVentWarning())
        {
            timeUntilVentWarning = VM_HALF_TIME.minusSeconds(config.ventWarningTime());
        }
    }

    private void calcEruptionWarningTime()
    {
        if (config.showEruptionWarning())
        {
            timeUntilEruptionWarning = VM_FULL_TIME.minusSeconds(config.eruptionWarningTime());
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("volcanicmine") || event.getKey().equals("showPlatformWarning"))
        {
            return;
        }
        // Calculate new vent warning times if enabled
        calcVentWarningTime();
        calcEruptionWarningTime();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        // Fetch times upon client loading
        if (event.getGameState() == GameState.LOGIN_SCREEN)
        {
            timeUntilVentWarning = VM_HALF_TIME.minusSeconds(config.ventWarningTime());
            timeUntilEruptionWarning = VM_FULL_TIME.minusSeconds(config.eruptionWarningTime());
        }
    }


    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
        {
            return;
        }

        // Remove colour and line breaks - not required?
        String chatMsg = Text.removeTags(event.getMessage());

        // Begin timer when start chat line is detected
        if (chatMsg.equals(CHAT_VM_START))
        {
            VMTimer = Instant.now();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (VMTimer != null)
        {
            // If timer is active and player is not in VM, null timer and escape
            if (!isInVM())
            {
                reset();
                return;
            }

            Duration timeSinceStart = Duration.between(VMTimer, Instant.now());

            if (timeSinceStart.compareTo(timeUntilVentWarning) >= 0 && !hasWarnedVent && config.showVentWarning())
            {
                notifier.notify("The vents will shift in " + config.ventWarningTime() + " seconds!");
                hasWarnedVent = true;
            }
            if (timeSinceStart.compareTo(timeUntilEruptionWarning) >= 0 && !hasWarnedEruption && config.showEruptionWarning())
            {
                notifier.notify("The volcano will erupt in " + config.eruptionWarningTime() + " seconds!");
                hasWarnedEruption = true;
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        // Skip calculation if not in VM
        if (!isInVM())
        {
            return;
        }

        // If warning is enabled and game object spawned is a stage 3 platform
        if (config.showPlatformWarning() && event.getGameObject().getId() == PLATFORM_STAGE_3_ID)
        {
            // Fetch coordinates of player and game object
            int playerX = client.getLocalPlayer().getWorldLocation().getX();
            int playerY = client.getLocalPlayer().getWorldLocation().getY();
            int objectX = event.getGameObject().getWorldLocation().getX();
            int objectY = event.getGameObject().getWorldLocation().getY();

            // Notify player if the stage 3 platform is beneath them
            if (playerX == objectX && playerY == objectY)
            {
                notifier.notify(PLATFORM_WARNING_MESSAGE);
            }
        }
    }


    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        // Return if not in VM
        if (!isInVM())
        {
            return;
        }

        // If warning is enabled and npc spawned is a boulder that is breaking
        if (config.showBoulderWarning())
        {
            NPC npc = npcSpawned.getNpc();

            switch(npc.getId())
            {
                case BOULDER_BREAK_STAGE_1_ID:
                case BOULDER_BREAK_STAGE_2_ID:
                case BOULDER_BREAK_STAGE_3_ID:
                case BOULDER_BREAK_STAGE_4_ID:
                case BOULDER_BREAK_STAGE_5_ID:
                    notifier.notify(BOULDER_WARNING_MESSAGE);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isInVM()
    {
        return WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_NORTH ||
                WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == VM_REGION_SOUTH;
    }
}
