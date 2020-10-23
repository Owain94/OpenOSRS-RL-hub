package com.optimalpoints;

import lombok.Getter;
import net.runelite.api.NPC;

public class CurrentBossData implements Comparable<CurrentBossData> {
    @Getter
    private final Integer score;

    private NPC npcData;

    public CurrentBossData(NPC npc, int score)
    {
        this.npcData = npc;
        this.score = score;
    }

    // For some reason this doesn't work with the lombok annotation
    public NPC getNpcData() {
        return npcData;
    }

    @Override
    public int compareTo(CurrentBossData o)
    {
        return this.getScore().compareTo(o.getScore());
    }
}
