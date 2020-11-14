package info.osleague.runelite.osleague;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

public enum Area
{
	MISTHALIN("Misthalin", 2722),
	KARAMJA("Karamja", 2723),
	WILDERNESS("Wilderness", 2724),
	ASGARNIA("Asgarnia", 2725),
	KANDARIN("Kandarin", 2726),
	DESERT("Desert", 2727),
	FREMENNIK("Fremennik", 2728),
	TIRANNWN("Tirannwn", 2729),
	MORYTANIA("Morytania", 2730);

	@Getter
	private final String name;
	private final int spriteId;

	Area(String name, int spriteId)
	{
		this.name = name;
		this.spriteId = spriteId;
	}

	private static final Map<Integer, Area> SPRITES;

	static
	{
		ImmutableMap.Builder<Integer, Area> spriteBuilder = new ImmutableMap.Builder<>();

		for (Area area : values())
		{
			spriteBuilder.put(area.spriteId, area);
		}

		SPRITES = spriteBuilder.build();
	}

	static Area getAreaBySprite(int id)
	{
		return SPRITES.get(id);
	}
}
