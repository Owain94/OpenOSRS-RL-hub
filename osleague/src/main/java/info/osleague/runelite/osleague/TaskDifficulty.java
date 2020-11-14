package info.osleague.runelite.osleague;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

@Getter
enum TaskDifficulty
{
	EASY(2316),
	MEDIUM(2317),
	HARD(2318),
	ELITE(2319),
	MASTER(2320);

	private final int spriteId;

	private static final Map<Integer, TaskDifficulty> TASK_DIFFICULTY_SPRITE_IDS;

	static
	{
		ImmutableMap.Builder<Integer, TaskDifficulty> builder = new ImmutableMap.Builder<>();

		for (TaskDifficulty taskDifficulty : values())
		{
			builder.put(taskDifficulty.spriteId, taskDifficulty);
		}

		TASK_DIFFICULTY_SPRITE_IDS = builder.build();
	}

	TaskDifficulty(int spriteId)
	{
		this.spriteId = spriteId;
	}

	static TaskDifficulty fromSprite(int spriteId)
	{
		return TASK_DIFFICULTY_SPRITE_IDS.get(spriteId);
	}
}