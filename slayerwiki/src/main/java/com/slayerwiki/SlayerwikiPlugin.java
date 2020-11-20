package com.slayerwiki;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import com.google.common.base.CaseFormat;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.MenuOpened;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.slayer.SlayerConfig;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.game.NPCManager;
import net.runelite.api.NpcID;
import okhttp3.HttpUrl;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.pf4j.Extension;


@Extension
@PluginDescriptor(
	name = "Slayer Wiki",
	description = "Adds Wiki option to slayer equipment to lookup current task.",
	tags = {"slayer", "wiki", "slayer wiki"},
	enabledByDefault = false,
	type = PluginType.SKILLING
)

public class SlayerwikiPlugin extends Plugin
{

	private static final String SLAYER_HELMET = "slayer helmet";
	private static final String ETERNAL_GEM = "eternal gem";
	private static final String ENCHANTED_GEM = "enchanted gem";
	private static final String SLAYER_RING = "slayer ring";
	private static final String BLACK_MASK = "black mask";

	private final static Set<String> SLAYER_ITEMS = ImmutableSet.of(SLAYER_HELMET, SLAYER_RING, ETERNAL_GEM, ENCHANTED_GEM, BLACK_MASK);
	private final static Map<String, Integer> SLAYER_TASKS = ImmutableMap.<String, Integer>builder()
			.put("aberrant spectres", NpcID.ABERRANT_SPECTRE)
			.put("abyssal demons", NpcID.ABYSSAL_DEMON_415)
			.put("adamant dragons", NpcID.ADAMANT_DRAGON)
			.put("ankous", NpcID.ANKOU)
			.put("aviansie", NpcID.AVIANSIE)
			.put("banshees", NpcID.BANSHEE)
			.put("basilisks", NpcID.BASILISK_417)
			.put("birds", NpcID.BIRD)
			.put("bears", NpcID.BLACK_BEAR)
			.put("black demons", NpcID.BLACK_DEMON)
			.put("black dragons", NpcID.BLACK_DRAGON)
			.put("black knights", NpcID.BLACK_KNIGHT)
			.put("bloodvelds", NpcID.BLOODVELD)
			.put("blue dragons", NpcID.BLUE_DRAGON)
			.put("brine rats", NpcID.BRINE_RAT)
			.put("bronze dragons", NpcID.BRONZE_DRAGON)
			.put("catablepon", NpcID.CATABLEPON)
			.put("cave bugs", NpcID.CAVE_BUG)
			.put("cave crawlers", NpcID.CAVE_CRAWLER)
			.put("cave horrors", NpcID.CAVE_HORROR)
			.put("cave slimes", NpcID.CAVE_SLIME)
			.put("cave kraken", NpcID.CAVE_KRAKEN)
			.put("chaos druids", NpcID.CHAOS_DRUID)
			.put("cockatrices", NpcID.COCKATHRICE)
			.put("cows", NpcID.COW)
			.put("crawling hands", NpcID.CRAWLING_HAND)
			.put("crocodiles", NpcID.CROCODILE)
			.put("dagannoths", NpcID.DAGANNOTH_970)
			.put("dark beast", NpcID.DARK_BEAST)
			.put("dark warriors", NpcID.DARK_WARRIOR)
			.put("dogs", NpcID.GUARD_DOG)
			.put("drakes", NpcID.DRAKE)
			.put("dwarfs", NpcID.DWARF)
			.put("earth warriors", NpcID.EARTH_WARRIOR)
			.put("elves", NpcID.ELF_ARCHER)
			.put("ent", NpcID.ENT)
			.put("fever spiders", NpcID.FEVER_SPIDER)
			.put("fire giants", NpcID.FIRE_GIANT)
			.put("flesh crawlers", NpcID.FLESH_CRAWLER)
			.put("fossil island wyvern", NpcID.SPITTING_WYVERN)
			.put("gargoyles", NpcID.GARGOYLE)
			.put("ghosts", NpcID.GHOST)
			.put("ghouls", NpcID.GHOUL)
			.put("goblins", NpcID.GOBLIN)
			.put("greater demons", NpcID.GREATER_DEMON)
			.put("green dragons", NpcID.GREEN_DRAGON)
			.put("harpie bug swarms", NpcID.HARPIE_BUG_SWARM)
			.put("hellhounds", NpcID.HELLHOUND)
			.put("hill giant", NpcID.HILL_GIANT)
			.put("hobgoblins", NpcID.HOBGOBLIN)
			.put("hydras", NpcID.HYDRA)
			.put("icefiends", NpcID.ICEFIEND)
			.put("ice giants", NpcID.ICE_GIANT)
			.put("infernal mages", NpcID.INFERNAL_MAGE)
			.put("iron dragons", NpcID.IRON_DRAGON)
			.put("jellies", NpcID.JELLY)
			.put("jungle horrors", NpcID.JUNGLE_HORROR)
			.put("kalphites", NpcID.KALPHITE_SOLDIER)
			.put("killerwatts", NpcID.KILLERWATT)
			.put("kurasks", NpcID.KURASK)
			.put("lava dragons", NpcID.LAVA_DRAGON)
			.put("lesser demons", NpcID.LESSER_DEMON)
			.put("lizardmen", NpcID.LIZARDMAN)
			.put("lizards", NpcID.LIZARD)
			.put("magic axes", NpcID.MAGIC_AXE)
			.put("mithril dragons", NpcID.MITHRIL_DRAGON)
			.put("minotaurs", NpcID.MINOTAUR)
			.put("mogres", NpcID.MOGRE)
			.put("molanisks", NpcID.MOLANISK)
			.put("monkeys", NpcID.MONKEY_1038)
			.put("moss giants", NpcID.MOSS_GIANT)
			.put("nechryael", NpcID.NECHRYAEL)
			.put("ogres", NpcID.OGRE)
			.put("otherworldly beings", NpcID.OTHERWORLDLY_BEING)
			.put("pirate", NpcID.PIRATE)
			.put("pyrefiends", NpcID.PYREFIEND)
			.put("red dragons", NpcID.RED_DRAGON)
			.put("rockslugs", NpcID.ROCKSLUG)
			.put("rogues", NpcID.ROGUE)
			.put("rune dragons", NpcID.RUNE_DRAGON)
			.put("scabarites", NpcID.SCABARAS)
			.put("scorpions", NpcID.SCORPION)
			.put("sea snakes", NpcID.SEA_SNAKE_YOUNG)
			.put("shades", NpcID.SHADE)
			.put("shadow warriors", NpcID.SHADOW_WARRIOR)
			.put("skeletal wyverns", NpcID.SKELETAL_WYVERN)
			.put("skeletons", NpcID.SKELETON)
			.put("smoke devils ", NpcID.SMOKE_DEVIL)
			.put("spiders", NpcID.SPIDER)
			.put("steel dragons", NpcID.STEEL_DRAGON)
			.put("suqahs", NpcID.SUQAH)
			.put("terror dogs", NpcID.TERROR_DOG)
			.put("trolls", NpcID.TROLL)
			.put("turoths", NpcID.TUROTH)
			.put("vampyres", NpcID.FERAL_VAMPYRE)
			.put("wall beasts", NpcID.WALL_BEAST)
			.put("waterfiends", NpcID.WATERFIEND)
			.put("werewolves", NpcID.WEREWOLF)
			.put("wolves ", NpcID.WOLF)
			.put("wyrms ", NpcID.WYRM)
			.put("zombies", NpcID.ZOMBIE)
			.put("zygomites", NpcID.ZYGOMITE)
			.put("abyssal sire", NpcID.ABYSSAL_SIRE)
			.put("alchemical hydra", NpcID.ALCHEMICAL_HYDRA)
			.put("barrows", NpcID.DHAROK_THE_WRETCHED)
			.put("callisto", NpcID.CALLISTO)
			.put("cerberus", NpcID.CERBERUS)
			.put("chaos elemental", NpcID.CHAOS_ELEMENTAL)
			.put("chaos fanatic", NpcID.CHAOS_FANATIC)
			.put("commander zilyana", NpcID.COMMANDER_ZILYANA)
			.put("crazy archaeologist", NpcID.CRAZY_ARCHAEOLOGIST)
			.put("dagannoth kings", NpcID.DAGANNOTH_REX)
			.put("giant mole", NpcID.GIANT_MOLE)
			.put("general graardor", NpcID.GENERAL_GRAARDOR)
			.put("k'ril tsutsaroth", NpcID.KRIL_TSUTSAROTH)
			.put("kalphite queen", NpcID.KALPHITE_QUEEN)
			.put("king black dragon", NpcID.KING_BLACK_DRAGON)
			.put("kraken", NpcID.KRAKEN)
			.put("kree'arra", NpcID.KREEARRA)
			.put("sarachnis", NpcID.SARACHNIS)
			.put("scorpia", NpcID.SCORPIA)
			.put("thermonuclear smoke devil", NpcID.THERMONUCLEAR_SMOKE_DEVIL)
			.put("venenatis", NpcID.VENENATIS)
			.put("vet'ion", NpcID.VETION)
			.put("vorkath", NpcID.VORKATH)
			.put("zulrah", NpcID.ZULRAH)
			.put("dust devils", NpcID.DUST_DEVIL)
			.build();

	static final HttpUrl WIKI_BASE = HttpUrl.parse("https://oldschool.runescape.wiki/w/Special:Lookup?type=npc&id=");

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private NPCManager npcManager;

	@Inject
	private Notifier notifier;


	@Subscribe
	public void onMenuOpened(MenuOpened event) {
		final String target = Text.removeTags(event.getFirstEntry().getTarget().toLowerCase());
		for ( String slayerItem : SLAYER_ITEMS) {
			if (target.contains(slayerItem)) {
				final SlayerConfig slayerConfig = configManager.getConfig(SlayerConfig.class);
				final String slayerTask = slayerConfig.taskName();

				if (!slayerTask.isEmpty()) {
					final MenuEntry[] menuEntries = event.getMenuEntries();
					final Integer menuLength = menuEntries.length + 1;
					final String TASK = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, slayerTask);

					MenuEntry[] menuList = new MenuEntry[menuLength];
					int num = 0;
					menuList[num++] = menuEntries[0];

					final MenuEntry entry = new MenuEntry();
					entry.setOption("Task Wiki");
					entry.setTarget(ColorUtil.prependColorTag(TASK, Color.orange));
					entry.setOpcode(MenuOpcode.RUNELITE.getId());
					menuList[num++] = entry;

					for (final MenuEntry menuEntry : Arrays.copyOfRange(event.getMenuEntries(), 1, event.getMenuEntries().length)) {
						menuList[num++] = menuEntry;
					}
					client.setMenuEntries(menuList);
				}
				break;
			}
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuOpcode() == MenuOpcode.RUNELITE) {
			if (event.getOption().equals("Task Wiki")) {
				event.consume();

				final SlayerConfig slayerConfig = configManager.getConfig(SlayerConfig.class);
				final String slayerTask = slayerConfig.taskName();
				final Integer taskId = SLAYER_TASKS.get(slayerTask.toLowerCase());

				HttpUrl.Builder urlBuilder = WIKI_BASE.newBuilder();

				if (taskId != null) {
					// Lookup with ID if we can
					urlBuilder.addQueryParameter("id", taskId.toString());
				}
					// Fallback lookup with name (good for monsters with many types like bears and bandits)
					urlBuilder.addQueryParameter("name", slayerTask);
					LinkBrowser.browse(urlBuilder.build().toString());
			}
		}
	}

	@Provides
	SlayerwikiConfig provideConfig(ConfigManager configManager) { return configManager.getConfig(SlayerwikiConfig.class); }

}
