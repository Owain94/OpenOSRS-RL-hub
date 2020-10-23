package com.NPCOverheadDialogue.dialog;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DialogCategories
{
    CLEANER(
            null,
            null,
            null,
            new String[] {"*sweep* *sweep*", "Time for a break!", "I just swept there!", "*whistles*"}),
    DUCKS(
            new String[]{"Quack!", "Quack! Quack!"},
            null,
            null,
            null),
    DRAGONS(
            new String[]{"*snarl*", "Roar!", "*stomp* *stomp*"},
            null,
            new String[]{"Roar!"},
            null),
    FISHING_SPOT(
            new String[]{"*blub* *blub*", "*splash*"},
            null,
            null,
            null),
    HONKING_BIRDS(
            new String[]{"Honk!", "Honk! Honk!"},
            null,
            null,
            null),
    LENNY(
            new String[]{"I sure love the rabbits!", "Gotta tend the rabbits!"},
            null,
            null,
            null),
    LIBRARIAN(
            new String[]{"I am a librarian", "shhhhhh", "I have always imagined that paradise will be a kind of Library"},
            null,
            null,
            new String[]{"I am a walking librarian"}),
    MID_SIZED_RODENTS(
            new String[]{"*nibble* *nibble*"},
            new String[]{"squeak!"},
            new String[]{"squeeaak..."},
            null),
    PIGS(
            new String[]{"Oink!"},
            null,
            null,
            null),
    RATS(
            new String[]{"*scamper* *scamper*"},
            new String[]{"hiss"},
            new String[]{"hissssssssssssss"},
            null),
    SEAGULLS(
            new String[]{"Squawk!"},
            null,
            null,
            null),
    SKELETONS(
            null,
            null,
            new String[]{"*crumbles*"},
            null),
    STORE_CLERK(
            new String[]{"Come check out my wares!", "I have the best prices around", "Buying all junk", "Bank sale"},
            null,
            null,
            null)
    ;

    private final String[] ambientDialogs;
    private final String[] damageDialogs;
    private final String[] deathDialogs;
    private final String[] walkingDialogs;
}