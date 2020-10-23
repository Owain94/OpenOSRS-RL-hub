//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.highlight;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class HighlightOverlay extends Overlay {
    private static final Rectangle PADDING = new Rectangle(2, 1, 0, 1);
    private final HighlightPlugin plugin;
    private final Client client;
    private int hasScrolled;
    @Inject
    private HighlightConfig config;
    @Inject
    private HighlightOverlay(HighlightPlugin plugin, Client client) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.client = client;

    }
    public Dimension render(Graphics2D graphics) {
        if(config.clanFirst()){
            if(this.plugin.getClan()){
                String player = this.plugin.getPlayer();
                if(player==""){
                    this.hasScrolled=0;
                    return null;
                } else {
                    Widget clanContainer = this.client.getWidget(WidgetInfo.FRIENDS_CHAT_LIST);
                    if(clanContainer!=null&&!clanContainer.isHidden()){
                        Widget found = null;
                        Widget[] var8 = clanContainer.getDynamicChildren();
                        int var9 = var8.length;
                        for (int var10 = 0; var10 < var9; ++var10) {
                            Widget clany = var8[var10];
                            if (clany.getText().contains(player)) {
                                found = clany;
                                break;
                            }
                        }
                        if(found==null){
                            return null;
                        }else{
                            if (this.hasScrolled != found.getRelativeY()) {
                                this.hasScrolled = found.getRelativeY();
                                this.plugin.scrollToWidget(this.client.getWidget(WidgetInfo.FRIENDS_CHAT_LIST), this.client.getWidget(7, 17), new Widget[]{found});
                            }
                            this.plugin.highlightWidget(graphics, found, this.client.getWidget(WidgetInfo.FRIENDS_CHAT_LIST), PADDING, (String) null);
                            return null;
                        }
                    }else if (hasScrolled != 0) {
                        hasScrolled = 0;
                        this.plugin.resetPlayer();
                        return null;
                    } else {
                        return null;
                    }
                }
            }else{
                int world = this.plugin.getWorld();
                if (world == 0) {
                    this.hasScrolled = 0;
                    return null;
                } else {
                    Widget worldContainer = this.client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST);
                    if (worldContainer != null && !worldContainer.isHidden()) {
                        Widget found = null;
                        Widget[] var8 = worldContainer.getDynamicChildren();
                        int var9 = var8.length;
                        for (int var10 = 0; var10 < var9; ++var10) {
                            Widget track = var8[var10];
                            if (track.getName().contains("" + world)) {
                                found = track;
                                break;
                            }
                        }
                        if (found == null) {
                            return null;
                        } else {
                            if (this.hasScrolled != world) {
                                this.hasScrolled = world;
                                this.plugin.scrollToWidget(this.client.getWidget(69, 15), this.client.getWidget(69, 18), new Widget[]{found});
                            }
                            this.plugin.highlightWidget(graphics, found, this.client.getWidget(69, 15), PADDING, (String) null);
                            return null;
                        }
                    } else if (hasScrolled != 0) {
                        hasScrolled = 0;
                        this.plugin.resetWorld();
                        return null;
                    } else {
                        return null;
                    }
                }
            }
        } else {
            int world = this.plugin.getWorld();
            if (world == 0) {
                this.hasScrolled = 0;
                return null;
            } else {
                Widget worldContainer = this.client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST);
                if (worldContainer != null && !worldContainer.isHidden()) {
                    Widget worldList = this.client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST);
                    Widget found = null;
                    if (worldList == null) {
                        return null;
                    } else {
                        Widget[] var8 = worldList.getDynamicChildren();
                        int var9 = var8.length;
                        for (int var10 = 0; var10 < var9; ++var10) {
                            Widget track = var8[var10];
                            if (track.getName().contains("" + world)) {
                                found = track;
                                break;
                            }
                        }

                        if (found == null) {
                            return null;
                        } else {
                            if (this.hasScrolled != world) {
                                this.hasScrolled = world;
                                this.plugin.scrollToWidget(this.client.getWidget(69, 15), this.client.getWidget(69, 18), new Widget[]{found});
                            }
                            this.plugin.highlightWidget(graphics, found, this.client.getWidget(69, 15), PADDING, (String) null);
                            return null;
                        }
                    }
                } else if (hasScrolled != 0) {
                    hasScrolled = 0;
                    this.plugin.resetWorld();
                    return null;
                } else {
                    return null;
                }
            }
        }
    }
}
