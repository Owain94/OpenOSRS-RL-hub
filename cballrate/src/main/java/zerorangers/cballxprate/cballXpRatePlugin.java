package zerorangers.cballxprate;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import static net.runelite.api.ObjectID.CANNON_BASE;
import static net.runelite.api.ProjectileID.CANNONBALL;
import static net.runelite.api.ProjectileID.GRANITE_CANNONBALL;
import net.runelite.api.events.ProjectileMoved;
import javax.inject.Inject;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "cballxprate",
        description = "Calculates Slayer and Ranged XP gained per cannonball used",
        tags = {"experience", "levels", "cannon", "cannonballs", "slayer","overlay"},
        enabledByDefault = false,
	type = PluginType.SKILLING
)

public class cballXpRatePlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private cballxprateoverlay cballSlayerXP_overlay;

    @Getter
    private WorldPoint cannonPosition;

    @Getter
    private GameObject cannon;

    @Getter
    private int TotalcballsUsed;

    @Getter
    private boolean cannonPlaced;

    @Getter
    private int initSlayerXP;

    @Getter
    private int initRangedXP;

    @Getter
    private double SlayerXPperCball;

    @Getter
    private double RangedXPperCball;

    private boolean initializeTracker;

    @Override
    public void startUp()
    {
        overlayManager.add(cballSlayerXP_overlay);
        TotalcballsUsed = 0;
        SlayerXPperCball = 0;
        RangedXPperCball = 0;
    }

    @Override
    public void shutDown()
    {
        overlayManager.remove(cballSlayerXP_overlay);
        cannonPlaced = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        GameState state = event.getGameState();
        if (state == GameState.LOGGED_IN)
        {
            initializeTracker = true;
            TotalcballsUsed = 0;
            SlayerXPperCball = 0;
            RangedXPperCball = 0;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (initializeTracker) {
            initializeTracker = false;
            initSlayerXP = client.getSkillExperience(Skill.SLAYER);
            initRangedXP = client.getSkillExperience(Skill.RANGED);
        }
    }

    public int getSlayerXP()
    {
        int currentSlayerXP = client.getSkillExperience(Skill.SLAYER);
        return currentSlayerXP;
    }

    public int getRangedXP()
    {
        int currentRangedXP = client.getSkillExperience(Skill.RANGED);
        return currentRangedXP;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        GameObject gameObject = event.getGameObject();

        Player localPlayer = client.getLocalPlayer();
        if (gameObject.getId() == CANNON_BASE && !cannonPlaced)
        {
            if (localPlayer.getWorldLocation().distanceTo(gameObject.getWorldLocation()) <= 2
                    && localPlayer.getAnimation() == AnimationID.BURYING_BONES)
            {
                cannonPosition = gameObject.getWorldLocation();
                cannon = gameObject;
            }
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        Projectile projectile = event.getProjectile();

        if ((projectile.getId() == CANNONBALL || projectile.getId() == GRANITE_CANNONBALL) && cannonPosition != null)
        {
            WorldPoint projectileLoc = WorldPoint.fromLocal(client, projectile.getX1(), projectile.getY1(), client.getPlane());

            if (projectileLoc.equals(cannonPosition) && projectile.getX() == 0 && projectile.getY() == 0)
            {
                TotalcballsUsed ++;
                SlayerXPperCball = Math.round((((float)getSlayerXP()-(float) initSlayerXP)/(float) TotalcballsUsed)*100.0)/100.0;
                RangedXPperCball = Math.round((((float)getRangedXP()-(float) initRangedXP)/(float) TotalcballsUsed)*100.0)/100.0;
            }
        }
    }
}
