package com.xpgrapher;

import net.runelite.api.Skill;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XpGraphPointManager {

    private XpGrapherPlugin grapherPlugin;
    private Map<Skill, ArrayList> skillGraphPointsMap = new HashMap<Skill, ArrayList>();
    public Map<Skill, Boolean> isSkillShownMap = new HashMap<Skill, Boolean>();

    public int maxVertAxisValue = 0;

    public int[] xpGraphMaxValues = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            12, 15, 20, 25, 30, 40, 50, 60, 80, 100,
            125, 150, 200, 250, 300, 400, 500, 600, 800, 1000,
            1250, 1500, 2000, 2500, 3000, 4000, 5000, 6000, 8000, 10000,
            12500, 15000, 20000, 25000, 30000, 40000, 50000, 60000, 80000, 100000,
            125000, 150000, 200000, 250000, 300000, 400000, 500000, 600000, 800000, 1000000,
            1250000, 1500000, 2000000, 2500000, 3000000, 4000000, 5000000, 6000000, 8000000, 10000000,
            12500000, 15000000, 20000000, 25000000, 30000000, 40000000, 50000000, 60000000, 80000000, 100000000
    };



    public XpGraphPointManager(XpGrapherPlugin grapherPlugin) {

        this.grapherPlugin = grapherPlugin;

        for (int i = 0; i < grapherPlugin.skillList.length; i++) {
            ArrayList<Integer> newGraphPointList = new ArrayList<Integer>();
            skillGraphPointsMap.put(grapherPlugin.skillList[i], newGraphPointList);
            isSkillShownMap.put(grapherPlugin.skillList[i], false);
        }

    }

    public boolean isSkillShown(Skill theSkill) {
        return isSkillShownMap.get(theSkill);
    }

    public int getGraphPointData(Skill skillToGraph, int x) {
        ArrayList<Integer> skillGraphData = skillGraphPointsMap.get(skillToGraph);
        //System.out.println(skillToGraph.getName());
        if (skillGraphData.size() == 0)
            return grapherPlugin.graphHeight;
        else {
            int y = skillGraphData.get(x);
            return y;
        }

    }

    public ArrayList<Integer> getGraphPointDataList(Skill theSkill) {
        return skillGraphPointsMap.get(theSkill);
    }

    public void update() {

        int maxXpGained = -1;

        for (int i = 0; i < grapherPlugin.skillList.length; i++) {

            Skill skillToCheck = grapherPlugin.skillList[i];

            if (grapherPlugin.isSkillShown(skillToCheck)) {
                int skillMinXp = grapherPlugin.xpDataManager.getXpData(skillToCheck, 0);
                //System.out.println(grapherPlugin.tickCount);
                int skillMaxXp = grapherPlugin.xpDataManager.getXpData(skillToCheck, grapherPlugin.tickCount);
                int skillXpGained = skillMaxXp - skillMinXp;
                if (maxXpGained == -1 || skillXpGained > maxXpGained)
                    maxXpGained = skillXpGained;
            }

        }

        boolean maxXpFound = false;
        int maxXpIndex = 0;
        while (!maxXpFound) {
            if (maxXpGained <= xpGraphMaxValues[maxXpIndex]) {
                maxXpFound = true;
                //System.out.println(xpGraphMaxValues[maxXpIndex]);
            } else {
                maxXpIndex++;
            }
        }
        maxVertAxisValue = xpGraphMaxValues[maxXpIndex];

        for (int i = 0; i < grapherPlugin.skillList.length; i++) {

            Skill skillToUpdate = grapherPlugin.skillList[i];

            ArrayList<Integer> newGraphPointList = new ArrayList<Integer>();

            for (int x = 0; x < grapherPlugin.graphWidth; x++) {

                double ratioAcrossGraph = (double)x/((double)grapherPlugin.graphWidth);

                int dataIndex = (int)(Math.floor(ratioAcrossGraph*(grapherPlugin.tickCount+1)));

                //int ceilDataIndex = (int)(Math.ceil(ratioAcrossGraph*(grapherPlugin.tickCount)));
                //int floorDataIndex = (int)(Math.floor(ratioAcrossGraph*(grapherPlugin.tickCount)));
                //System.out.println(ceilDataIndex + ", " + floorDataIndex);

                int dataXpValue = grapherPlugin.xpDataManager.getXpData(skillToUpdate, dataIndex);
                if (x == 0) {
                    dataXpValue = grapherPlugin.xpDataManager.getXpData(skillToUpdate, 0);
                }
                if (x == grapherPlugin.graphWidth - 1) {
                    dataXpValue = grapherPlugin.xpDataManager.getMostRecentXp(skillToUpdate);
                }

                int dataXpMinValue = grapherPlugin.xpDataManager.getXpData(skillToUpdate, 0);
                int dataXpGained = dataXpValue -  dataXpMinValue;

                double ratioVertical = dataXpGained/(double) maxVertAxisValue;
                int y = grapherPlugin.graphHeight - (int)((double)grapherPlugin.graphHeight*ratioVertical);

                //if (x == grapherPlugin.graphWidth-1) {
                //
                //    dataXpValue = grapherPlugin.getClient().getSkillExperience(skillToUpdate);
                //
                //}

                newGraphPointList.add(y);

            }

            skillGraphPointsMap.put(skillToUpdate, newGraphPointList);
        }

    }

}
