/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package com.zalcano;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;


public class ZalcanoOverlay extends OverlayPanel {
    private final Client client;
    private final ZalcanoPlugin plugin;
    private final ZalcanoConfig config;


    @Inject
    private ZalcanoOverlay(Client client, ZalcanoPlugin plugin, ZalcanoConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);

        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        if (!shouldShowOverlay()) return null;
        showTitle();
        if (config.showPlayerCount()) showPlayerCount();
        if (config.showHealth()) showHealth();
        if (config.showDamageDealt()) showDamageDealt();
        if (config.showToolSeedCalculations()) showToolSeedCalculations();
        return super.render(graphics2D);
    }

    private boolean shouldShowOverlay()
    {
        return plugin.playerInZalcanoArea();
    }

    private void showTitle()
    {
        panelComponent.getChildren().add(TitleComponent.builder().text("Zalcano").build());
    }

    private void showPlayerCount()
    {
        int playercount = plugin.getPlayersParticipating().size();
        panelComponent.getChildren().add(LineComponent.builder().left("Players: " + playercount).build());
    }

    private void showHealth()
    {
        int firstPhaseHealth = 640;
        int secondPhaseHealth = 360;
        int miningHp = plugin.getMiningHp();
        int phase = miningHp > firstPhaseHealth ? 1 : miningHp > secondPhaseHealth ? 2 : 3;

        Color color = decideColorBasedOnThreshold(miningHp, firstPhaseHealth, secondPhaseHealth);
        panelComponent.getChildren().add(LineComponent.builder().left("Mining HP:  " + miningHp + " / 1000 (Phase: " + phase + ")").leftColor(color).build());
        if (plugin.getZalcanoState() == ZalcanoStates.THROWING) panelComponent.getChildren().add(LineComponent.builder().left("Throwing HP:  " + plugin.getThrowingHp() + " / 300").build());
    }

    private void showDamageDealt()
    {
        Color color = decideColorBasedOnThreshold(plugin.getShieldDamageDealt(), plugin.getMinimumDamageUniquesShield(), plugin.getMinimumDamageLootShield());
        panelComponent.getChildren().add(LineComponent.builder().left("Shield Damage dealt: " + plugin.getShieldDamageDealt() + " / " + plugin.getMinimumDamageUniquesShield()).leftColor(color).build());

        color = decideColorBasedOnThreshold(plugin.getMiningDamageDealt(), plugin.getMinimumDamageUniquesMining(), plugin.getMinimumDamageLootMining());
        panelComponent.getChildren().add(LineComponent.builder().left("Mining Damage dealt: " + plugin.getMiningDamageDealt() + " / " + plugin.getMinimumDamageUniquesMining()).leftColor(color).build());
    }

    private Color decideColorBasedOnThreshold(int damage, int greenThreshold, int yellowThreshold)
    {
        if (damage >= greenThreshold) {
            return Color.GREEN;
        } else if (damage > yellowThreshold) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    private void showToolSeedCalculations()
    {
        panelComponent.getChildren().add(LineComponent.builder().left("Chance of tool seed: " + String.format("%.3g", plugin.getChanceOfToolSeedTable() * 100) + "%").build());
    }
}
