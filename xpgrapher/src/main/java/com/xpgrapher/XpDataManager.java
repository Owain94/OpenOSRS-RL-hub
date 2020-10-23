package com.xpgrapher;

import net.runelite.api.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XpDataManager {

    private Map<Skill, ArrayList> skillXpMap = new HashMap<Skill, ArrayList>();

    private XpGrapherPlugin grapherPlugin;

    public XpDataManager(XpGrapherPlugin grapherPlugin) {

        this.grapherPlugin = grapherPlugin;

        for (int i = 0; i < grapherPlugin.skillList.length; i++) {
            ArrayList<Integer> newXpList = new ArrayList<Integer>();
            skillXpMap.put(grapherPlugin.skillList[i], newXpList);
        }

    }

    public void update() {
        for (int i = 0; i < grapherPlugin.skillList.length; i++) {
            Skill skillToUpdate = grapherPlugin.skillList[i];
            ArrayList<Integer> xpListToUpdate = skillXpMap.get(skillToUpdate);
            int xpValueToAdd = grapherPlugin.getClient().getSkillExperience(skillToUpdate);

            if (xpListToUpdate.size() > 0) {

                int lastXpValue = xpListToUpdate.get(xpListToUpdate.size() - 1);
                if (lastXpValue < xpValueToAdd)
                    if (skillToUpdate.getName() != "Overall") {
                        grapherPlugin.xpGraphPointManager.isSkillShownMap.put(skillToUpdate, true);
                        //grapherPlugin.currentlyGraphedSkills.add(skillToUpdate);
                        grapherPlugin.graphSkill(skillToUpdate);
                    }

            }

            xpListToUpdate.add(xpValueToAdd);
            //if (xpValueToAdd > lastXpValue)
            //    grapherPlugin.xpGraphPointManager.isSkillShownMap.put(skillToUpdate, true);
        }
    }

    public int getXpData(Skill skillToGet, int tickNum) {
        ArrayList<Integer> xpListToGet = skillXpMap.get(skillToGet);
        int xpValueAtTickNum = xpListToGet.get(tickNum);
        return xpValueAtTickNum;
    }

    public int getMostRecentXp(Skill skillToGet) {
        ArrayList<Integer> xpListToGet = skillXpMap.get(skillToGet);
        return xpListToGet.get(xpListToGet.size()-1);
    }

}
