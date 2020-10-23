package com.NPCOverheadDialogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.Actor;

@Data
@AllArgsConstructor
class ActorDialogState
{
    private Actor actor;
    private String name;
    private String dialog;
    private int dialogChangeTick;
    private int lastXCoordinate;
    private int lastYCoordinate;
    private int ticksWithoutMoving;
    private boolean inCombat;
}