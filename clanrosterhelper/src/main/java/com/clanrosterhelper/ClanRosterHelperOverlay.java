/*
 * Copyright (c) 2020, Spencer Imbleau <spencer@imbleau.com>
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
package com.clanrosterhelper;

import net.runelite.api.Ignore;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class ClanRosterHelperOverlay extends Overlay {

    /**
     * A reference to the plugin object
     */
    private final ClanRosterHelperPlugin plugin;

    /**
     * The UI Component
     */
    private final PanelComponent panelComponent;

    @Inject
    public ClanRosterHelperOverlay(ClanRosterHelperPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.panelComponent = new PanelComponent();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        //Only show this when the clan setup widget is up
        return panelComponent.render(graphics);
    }

    // Subroutines below

    /**
     * Subroutine - Update the user interface. This is quite a beefy process
     * so this method is only called when there's the possibility of a
     * potential UI change.
     */
    public void update() {
        panelComponent.setPreferredSize(new Dimension(200, 0));

        panelComponent.getChildren().clear();
        if (plugin.isClanRosterCorrupt()) {
            panelComponent.getChildren().add(TitleComponent.builder().text("Input URI is malformed/corrupt").build());
            return;
        } else if (plugin.getClanMembers() == null) {
            panelComponent.getChildren().add(TitleComponent.builder().text("Visit 'Clan Setup'").build());
            return;
        }

        //We're good to go! Build the renderable interface for clan actions.
        panelComponent.getChildren().add(TitleComponent.builder().text("Clan Roster Actions").build());
        qualifyActions();
    }

    /**
     * Subroutine - contains the login for drawing what the user needs to
     * accomplish to match the extract.
     */
    private void qualifyActions() {
        //Get the extract information
        ClanRosterTruth extract = plugin.getClanRosterTruth();
        //Get the clan setup info
        List<ClanMemberMap> clanMembers = plugin.getClanMembers();

        //Iterate through known clan setup members
        for (ClanMemberMap extractMember : extract.MEMBERS) {
            //Find the extract member's match if it exists
            ClanMemberMap match = null;
            for (ClanMemberMap clanMember : clanMembers) {
                if (extractMember.getRSN().equalsIgnoreCase(clanMember.getRSN())) {
                    match = clanMember;
                    break;
                }
            }

            //Check if the matched player is correctly ranked
            if (match != null) {
                if (!ranksMatch(extractMember.getRank(), match.getRank())) {
                    switch (extractMember.getRank().toLowerCase()) {
                        case "not in clan":
                        case "friend":
                            drawMember("'" + match.getRSN() + "' rank:", "Not in clan");
                            break;
                        case "ignore":
                            drawMember("'" + match.getRSN() + "'", "Remove Friend");
                            drawMember("'" + match.getRSN() + "'", "Ignore Player");
                            break;
                        default:
                            drawMember("'" + match.getRSN() + "' rank:", extractMember.getRank());
                            break;
                    }
                }
            } else {
                switch (extractMember.getRank()) {
                    case "Ignore":
                        if (!isIgnored(extractMember.getRSN())) {
                            drawMember("'" + extractMember.getRSN() + "'", "Ignore Player");
                        }
                        break;
                    default:
                        drawMember("'" + extractMember.getRSN() + "'", "Add Player");
                        if (!ranksMatch(extractMember.getRank(), "friend")) {
                            drawMember("'" + extractMember.getRSN() + "' rank:", extractMember.getRank());
                        }
                        break;
                }

            }
        }

        //Now do the reverse to find unneeded friends/ignores/etc
        for (ClanMemberMap clanMember : clanMembers) {
            ClanMemberMap match = null;
            for (ClanMemberMap extractMember : extract.MEMBERS) {
                if (clanMember.getRSN().equalsIgnoreCase(extractMember.getRSN())) {
                    match = clanMember;
                    break;
                }
            }

            //If they are not in the extract, they should be removed.
            if (match == null) {
                drawMember("'" + clanMember.getRSN() + "':", "Remove Friend");
            }
        }
    }

    /**
     * Subroutine - Check if two ranks match
     *
     * @param rank1 - the first rank string
     * @param rank2 - the second rank string
     * @return true if the ranks match, false otherwise
     */
    private boolean ranksMatch(String rank1, String rank2) {
        // Friend and "Not In Clan" are synonymous on extracts and on the clan setup page.
        if (rank1.equalsIgnoreCase("Friend") || rank1.equalsIgnoreCase("Not In Clan")) {
            if (rank2.equalsIgnoreCase("Not In Clan") || rank2.equalsIgnoreCase("Friend")) {
                return true;
            } else {
                return false;
            }
        } else {
            return rank1.toLowerCase().equalsIgnoreCase(rank2);
        }
    }

    /**
     * Subroutine - Check if a player is ignorelisted
     *
     * @param rsn - the rsn to check for ignore status
     * @return true if the player is ignored, false otherwise
     */
    private boolean isIgnored(String rsn) {
        Ignore[] ignores = this.plugin.getClient().getIgnoreContainer().getMembers();
        for (Ignore ignore : ignores) {
            if (ignore.getName().equalsIgnoreCase(rsn)) {
                return true;
            } else if (ignore.getPrevName() != null && ignore.getPrevName().equalsIgnoreCase(rsn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Subroutine - Build a listed item on the main UI
     *
     * @param rsn    - the RSN to add to the list
     * @param action - the action to add to the list
     */
    private void drawMember(String rsn, String action) {
        panelComponent.getChildren().add(
                LineComponent.builder().left(rsn).right(action).build()
        );
    }
}
