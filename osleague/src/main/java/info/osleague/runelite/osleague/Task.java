package info.osleague.runelite.osleague;

import lombok.Data;

@Data
public class Task
{
	Task(int index, String name, int points, boolean completed, int spriteId)
	{
		this.index = index;
		this.points = points;
		this.name = name;
		this.completed = completed;
		this.taskDifficulty = TaskDifficulty.fromSprite(spriteId);
	}

	public int index;
	public boolean completed;
	public String name;
	public int points;
	public TaskDifficulty taskDifficulty;
}
