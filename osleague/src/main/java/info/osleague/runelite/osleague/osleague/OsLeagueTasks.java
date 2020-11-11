package info.osleague.runelite.osleague.osleague;

import info.osleague.runelite.osleague.Task;
import java.util.List;
import java.util.stream.Collectors;

public class OsLeagueTasks
{
	public int version = 3;
	public List<String> tasks;

	public OsLeagueTasks(List<Task> tasks)
	{
		List<String> taskNumbers = tasks.stream()
			.filter(task -> task.Completed)
			.mapToInt(task -> task.OsLeagueIndex)
			.mapToObj(Integer::toString)
			.collect(Collectors.toList());

		this.tasks = taskNumbers;
	}
}
