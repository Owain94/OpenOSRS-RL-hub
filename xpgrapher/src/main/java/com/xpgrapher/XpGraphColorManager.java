package com.xpgrapher;

import net.runelite.api.Skill;

import java.awt.*;
import java.util.HashMap;

public class XpGraphColorManager {

    private HashMap<Skill, Color> skillColorMap = new HashMap<Skill, Color>();
    private XpGrapherPlugin grapherPlugin;

    private Color[] skillColorData = {

            //attack
            new Color(79,143,35),
            //Defence
            new Color(115, 115, 115),
            //strength
            new Color(0,64,255),
            //hitpoints
            new Color(143, 35, 35),
            //ranged
            new Color(106,255,0),
            //prayer
            new Color(255,212,0),
            //magic
            new Color(0,149,255),
            //cooking
            new Color(107,35,143),
            //woodcutting
            new Color(35,98,143),
            //fletching
            new Color(255, 127, 0),
            //fishing
            new Color(231, 233, 185),
            //firemaking
            new Color(255, 0, 0),
            //crafting
            new Color(255,0,170),
            //smithing
            new Color(185,237,224),
            //mining
            new Color(204,204,204),
            //herblore
            new Color(191,255,0),
            //agility
            new Color(185,215,237),
            //thieving
            new Color(255,255,0),
            //slayer
            new Color(220,185,237),
            //farming
            new Color(0,234,255),
            //runecraft
            new Color(170,0,255),
            //hunter
            new Color(237, 185, 185),
            //construction
            new Color(220, 190, 255),
            //overall
            new Color(143, 106, 35),
    };

    public XpGraphColorManager(XpGrapherPlugin grapherPlugin) {
        this.grapherPlugin = grapherPlugin;
        for (int i = 0; i < skillColorData.length; i++) {
            skillColorMap.put(grapherPlugin.skillList[i], skillColorData[i]);
        }
    }

    public Color getSkillColor(Skill skill) {
        //return skillColorMap.get(skill);
        if (skill.getName() == "Attack")
            return grapherPlugin.config.attackColor();
        else if (skill.getName() == "Strength")
            return grapherPlugin.config.strengthColor();
        else if (skill.getName() == "Defence")
            return grapherPlugin.config.defenceColor();
        else if (skill.getName() == "Ranged")
            return grapherPlugin.config.rangedColor();
        else if (skill.getName() == "Prayer")
            return grapherPlugin.config.prayerColor();

        else if (skill.getName() == "Magic")
            return grapherPlugin.config.magicColor();
        else if (skill.getName() == "Runecraft")
            return grapherPlugin.config.runecraftColor();
        else if (skill.getName() == "Construction")
            return grapherPlugin.config.constructionColor();
        else if (skill.getName() == "Hitpoints")
            return grapherPlugin.config.hitpointsColor();
        else if (skill.getName() == "Agility")
            return grapherPlugin.config.agilityColor();

        else if (skill.getName() == "Herblore")
            return grapherPlugin.config.herbloreColor();
        else if (skill.getName() == "Thieving")
            return grapherPlugin.config.thievingColor();
        else if (skill.getName() == "Crafting")
            return grapherPlugin.config.craftingColor();
        else if (skill.getName() == "Fletching")
            return grapherPlugin.config.fletchingColor();
        else if (skill.getName() == "Slayer")
            return grapherPlugin.config.slayerColor();
        else if (skill.getName() == "Hunter")
            return grapherPlugin.config.hunterColor();
        else if (skill.getName() == "Mining")
            return grapherPlugin.config.miningColor();
        else if (skill.getName() == "Smithing")
            return grapherPlugin.config.smithingColor();
        else if (skill.getName() == "Fishing")
            return grapherPlugin.config.fishingColor();
        else if (skill.getName() == "Cooking")
            return grapherPlugin.config.cookingColor();

        else if (skill.getName() == "Firemaking")
            return grapherPlugin.config.firemakingColor();
        else if (skill.getName() == "Woodcutting")
            return grapherPlugin.config.woodcuttingColor();
        else if (skill.getName() == "Farming")
            return grapherPlugin.config.farmingColor();
        return Color.BLACK;
    }

    public void setSkillColor(Skill skill, Color color) {
        skillColorMap.put(skill, color);
    }

}
