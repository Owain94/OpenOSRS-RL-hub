package net.runelite.client.plugins.dataexport;

import lombok.Getter;

public class DataExportSkill
{
	@Getter
	private final String name;

	@Getter
	private final int level;

	@Getter
	private final int experience;

	DataExportSkill(String name, int level, int experience)
	{
		this.name = name;
		this.level = level;
		this.experience = experience;
	}
}