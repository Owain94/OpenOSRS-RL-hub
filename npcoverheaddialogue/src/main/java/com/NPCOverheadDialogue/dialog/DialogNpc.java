package com.NPCOverheadDialogue.dialog;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

@Getter
public enum DialogNpc
{
    CLEANER("Cleaner", -1 ,DialogCategories.CLEANER),

    DRAKE("Drake", 2004, DialogCategories.DUCKS),
    DUCK("Duck", -1, DialogCategories.DUCKS),
    DUCKLING("Duckling", -1, DialogCategories.DUCKS),

    ADAMANT_DRAGON("Adamant dragon", -1, DialogCategories.DRAGONS),
    BABY_BLACK_DRAGON("Baby black dragon", -1, DialogCategories.DRAGONS),
    BABY_BLUE_DRAGON("Baby blue dragon", -1, DialogCategories.DRAGONS),
    BABY_GREEN_DRAGON("Baby green dragon", -1, DialogCategories.DRAGONS),
    BABY_RED_DRAGON("Baby red dragon", -1, DialogCategories.DRAGONS),
    BLACK_DRAGON("Black dragon", -1, DialogCategories.DRAGONS),
    BLUE_DRAGON("Blue dragon", -1, DialogCategories.DRAGONS),
    BRONZE_DRAGON("Bronze dragon", -1, DialogCategories.DRAGONS),
    BRUTAL_BLACK_DRAGON("Brutal black dragon", -1, DialogCategories.DRAGONS),
    BRUTAL_BLUE_DRAGON("Brutal blue dragon", -1, DialogCategories.DRAGONS),
    BRUTAL_GREEN_DRAGON("Brutal green dragon", -1, DialogCategories.DRAGONS),
    BRUTAL_RED_DRAGON("Brutal red dragon", -1, DialogCategories.DRAGONS),
    CORRUPTED_DRAGON("Corrupted dragon", -1, DialogCategories.DRAGONS),
    CRYSTALLINE_DRAGON("Crystalline dragon", -1, DialogCategories.DRAGONS),
    DRAKE_DRAGON("Drake", -1, DialogCategories.DRAGONS),
    GREEN_DRAGON("Green dragon", -1, DialogCategories.DRAGONS),
    IRON_DRAGON("Iron dragon", -1, DialogCategories.DRAGONS),
    LAVA_DRAGON("Lava dragon", -1, DialogCategories.DRAGONS),
    MITHRIL_DRAGON("Mithril dragon", -1, DialogCategories.DRAGONS),
    REANIMATED_DRAGON("Reanimated dragon", -1, DialogCategories.DRAGONS),
    RED_DRAGON("Red dragon", -1, DialogCategories.DRAGONS),
    RUNE_DRAGON("Rune dragon", -1, DialogCategories.DRAGONS),
    STEEL_DRAGON("Steel dragon", -1, DialogCategories.DRAGONS),

    FISHING_SPOT("Fishing spot", -1, DialogCategories.FISHING_SPOT),
    ROD_FISHING_SPOT("Rod Fishing spot", -1, DialogCategories.FISHING_SPOT),

    BLACK_SWAN("Black swan", -1, DialogCategories.HONKING_BIRDS),
    CORMORANT("Cormorant", -1, DialogCategories.HONKING_BIRDS),
    GOOSE("Goose", -1, DialogCategories.HONKING_BIRDS),
    SWAN("Swan", -1, DialogCategories.HONKING_BIRDS),

    RELDO("Reldo", -1, DialogCategories.LIBRARIAN),

    LENNY("Lenny", -1, DialogCategories.LENNY),

    BABY_MOLE("Baby Mole", -1, DialogCategories.MID_SIZED_RODENTS),
    RABBIT("Rabbit", -1, DialogCategories.MID_SIZED_RODENTS),
    RED_PANDA("Red Panda", -1, DialogCategories.MID_SIZED_RODENTS),
    SQUIRREL("Squirrel", -1, DialogCategories.MID_SIZED_RODENTS),

    PIG("Pig", -1, DialogCategories.PIGS),
    PIGLET("Piglet", -1, DialogCategories.PIGS),

    GIANT_RAT("Giant rat", -1, DialogCategories.RATS),
    RAT("Rat", -1, DialogCategories.RATS),

    GULLS("Gull", -1, DialogCategories.SEAGULLS),
    PELICAN("Pelican", -1, DialogCategories.SEAGULLS),
    SEAGULL("Seagull", -1, DialogCategories.SEAGULLS),

    GARGOYLE("Gargoyle", -1, DialogCategories.SKELETONS),
    SKELETON("Skeleton", -1, DialogCategories.SKELETONS)

    ;

    private final String npcName;
    private final int npcID;
    private final DialogCategories[] dialogCategories;

    DialogNpc(final String npcName, final int npcID, DialogCategories... dialogCategories)
    {
        this.npcName = npcName;
        this.npcID = npcID;
        this.dialogCategories = dialogCategories;
    }

    private static final Map<String, DialogNpc> NAME_MAP;
    static
    {
        ImmutableMap.Builder<String, DialogNpc> builder = new ImmutableMap.Builder<>();
        for (final DialogNpc n : values())
        {
            String d;
            if(n.getNpcID() != -1){
                d = Integer.toString(n.getNpcID());
            }
            else{
                d = n.getNpcName().toUpperCase();
            }
            builder.put(d, n);
        }
        NAME_MAP = builder.build();
    }

    public static boolean isDialogNpc(final String npcName)
    {
        return NAME_MAP.containsKey(npcName.toUpperCase());
    }

    @Nullable
    public static DialogNpc getDialogNpcsByNpcName(final String npcName)
    {
        return NAME_MAP.get(npcName.toUpperCase());
    }

    @Nullable
    public static DialogNpc getDialogNpcsByNpcID(final int npcID) {
        //log.info("ID being checked is " + npcID);
        return NAME_MAP.get(Integer.toString(npcID));
    }

    @Nullable
    public String[] getAmbientDialogs()
    {
        String[] dialogs = new String[0];
        for (final DialogCategories category : dialogCategories)
        {
            if (category.getAmbientDialogs() != null)
            {
                dialogs = ArrayUtils.addAll(dialogs, category.getAmbientDialogs());
            }
        }

        return dialogs.length > 0 ? dialogs : null;
    }

    @Nullable
    public static String[] getAmbientDialogsByNpcName(final String npcName)
    {
        final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

        if (v == null)
        {
            return null;
        }

        return v.getAmbientDialogs();
    }

    @Nullable
    public String[] getDamageDialogs()
    {
        String[] dialogs = new String[0];
        for (final DialogCategories category : dialogCategories)
        {
            if (category.getDamageDialogs() != null)
            {
                dialogs = ArrayUtils.addAll(dialogs, category.getDamageDialogs());
            }
        }

        return dialogs.length > 0 ? dialogs : null;
    }

    @Nullable
    public static String[] getDamageDialogsByNpcName(final String npcName)
    {
        final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

        if (v == null)
        {
            return null;
        }

        return v.getDamageDialogs();
    }

    @Nullable
    public String[] getDeathDialogs()
    {
        String[] dialogs = new String[0];
        for (final DialogCategories category : dialogCategories)
        {
            if (category.getDeathDialogs() != null)
            {
                dialogs = ArrayUtils.addAll(dialogs, category.getDeathDialogs());
            }
        }

        return dialogs.length > 0 ? dialogs : null;
    }

    @Nullable
    public static String[] getDeathDialogsByNpcName(final String npcName)
    {
        final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

        if (v == null)
        {
            return null;
        }

        return v.getDeathDialogs();
    }

    @Nullable
    public String[] getWalkingDialogs()
    {
        String[] dialogs = new String[0];
        for (final DialogCategories category : dialogCategories)
        {
            if (category.getWalkingDialogs() != null)
            {
                dialogs = ArrayUtils.addAll(dialogs, category.getWalkingDialogs());
            }
        }

        return dialogs.length > 0 ? dialogs : null;
    }

    @Nullable
    public static String[] getWalkingDialogsByNpcName(final String npcName)
    {
        final DialogNpc v = NAME_MAP.get(npcName.toUpperCase());

        if (v == null)
        {
            return null;
        }

        return v.getWalkingDialogs();
    }
}