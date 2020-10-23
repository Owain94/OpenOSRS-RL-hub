package com.diabolickal.pickpocketinfo;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "Pickpocket Info",
        description = "Shows helpful pickpocketing info.",
        tags = {"thieving", "pickpocket"},
        enabledByDefault = false,
	type = PluginType.SKILLING
)

@Slf4j
public class PickpocketInfoPlugin extends Plugin
{
    private float attempts, passes, percent;
    private int dodgyCharges = -1, pouchNum, brokenDodgy, totalPouches, lastPouchNum;
    private String lastTarget;
    private Instant lastPickpocket;
    private boolean hasDodgy, targetHasPouches;

    private static final Pattern DODGY_CHECK_PATTERN = Pattern.compile(
            "Your dodgy necklace has (\\d+) charges? left\\.");
    private static final Pattern DODGY_PROTECT_PATTERN = Pattern.compile(
            "Your dodgy necklace protects you\\..*It has (\\d+) charges? left\\.");
    private static final Pattern DODGY_BREAK_PATTERN = Pattern.compile(
            "Your dodgy necklace protects you\\..*It then crumbles to dust\\.");
    @Inject
    private Client client;

    @Inject
    private PickpocketInfoOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private PickpocketInfoConfig config;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        dodgyCharges = config.dodgyNecklace();
        clientThread.invokeLater(() ->
        {
            final ItemContainer container = client.getItemContainer(InventoryID.EQUIPMENT);
            final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

            if (container != null)
            {
                CheckForEquippedDodgy(container.getItems());
                CheckForPouches(inventory.getItems());
            }
        });
    }
    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
    }
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if(chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM)
        {
            CheckDodgy(chatMessage);
            CheckPickpocket(chatMessage);
        }
    }
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (event.getItemContainer() == client.getItemContainer(InventoryID.EQUIPMENT))
        {
            CheckForEquippedDodgy(event.getItemContainer().getItems());

        }
        if(event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
        {
            CheckForPouches(event.getItemContainer().getItems());
        }

    }
    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGIN_SCREEN)
        {
            lastPickpocket = null;
            if(config.resetType() == ResetType.LOGOUT)
            {
                ResetRate();
            }
        }
    }

    @Provides
    PickpocketInfoConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PickpocketInfoConfig.class);
    }

    private void ResetRate()
    {
        attempts = 0.0f;
        passes = 0.0f;
        brokenDodgy = 0;
        targetHasPouches = false;
    }


    private void CheckForEquippedDodgy(Item[] items)
    {
        Item Necklace = items[EquipmentInventorySlot.AMULET.getSlotIdx()];
        hasDodgy = (Necklace.getId() == 21143);
    }

    private void CheckDodgy(ChatMessage chatMessage)
    {
        Matcher dodgyCheck = DODGY_CHECK_PATTERN.matcher(chatMessage.getMessage());
        Matcher dodgyProtect = DODGY_PROTECT_PATTERN.matcher(chatMessage.getMessage());
        Matcher dodgyBreak = DODGY_BREAK_PATTERN.matcher(chatMessage.getMessage());
        if (dodgyCheck.find())
        {
            dodgyCharges = Integer.parseInt(dodgyCheck.group(1));
            config.dodgyNecklace(dodgyCharges);
        }
        else if(dodgyProtect.find())
        {
            dodgyCharges = Integer.parseInt(dodgyProtect.group(1));
            config.dodgyNecklace(dodgyCharges);
        }
        else if(dodgyBreak.find())
        {
            dodgyCharges = 10;
            brokenDodgy++;
            config.dodgyNecklace(dodgyCharges);
        }
    }

    private void CheckForPouches(Item[] items)
    {
        pouchNum = 0;
        for (int i = 0; i < items.length; i++)
        {
            //Get the name because each NPC's coin pouch has a different ID
            String name = client.getItemDefinition(items[i].getId()).getName();
            if(name.toLowerCase().equals("coin pouch"))
            {
                pouchNum = items[i].getQuantity();
                targetHasPouches = true;

                break;
            }
        }
        if(pouchNum > lastPouchNum)
            totalPouches++;

        lastPouchNum = pouchNum;
    }

    private void CheckPickpocket(ChatMessage chatMessage)
    {
        String msg = chatMessage.getMessage().toLowerCase();
        if (msg.contains("you attempt to pick"))
        {
            //Check chat messages for target, attempt, and successes
            String pickTarget = msg.split("pick the ")[1];
            pickTarget = pickTarget.split("'s")[0];
            lastPickpocket = Instant.now();
            if (!pickTarget.equals(lastTarget))
            {
                ResetCounter();
            }
            attempts += 1.0f;
            lastTarget = pickTarget;
        }
        if (msg.contains("you pick the"))
            passes += 1.0f;
        if (msg.contains("you pick the") || msg.contains("you fail to pick"))
            percent = (passes / attempts) * 100;
    }

    //Called when the pickpocket target changes
    private void ResetCounter()
    {
        attempts = 0.0f;
        passes = 0.0f;
        totalPouches = 0;
        brokenDodgy = 0;
        targetHasPouches = false;
    }

    //Encapsulation stuff
    public float attempts() {return  attempts; }
    public float percent()
    {
        return percent;
    }
    public int dodgyCharges()
    {
        return dodgyCharges;
    }
    public Instant lastPickpocket()
    {
        return lastPickpocket;
    }
    public int pouchNum()
    {
        return pouchNum;
    }
    public boolean hasDodgy()
    {
        return hasDodgy;
    }
    public boolean targetHasPouches()
    {
        return targetHasPouches;
    }
    public int brokenDodgy(){return  brokenDodgy;}
    public int totalPouches(){return  totalPouches;}

}
