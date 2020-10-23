package com.thatgamerblue.runelite.plugins.fakeiron;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum FakeIronIcons
{

	ORIGINAL_ICONS_PLACEHOLDER("--- Original ---", "", true),
	IRONMAN("Ironman", "", false),
	HCIM("Hardcore", "", false),
	ULTIMATE("Ultimate", "", false),
	CUSTOM_PLACEHOLDER("--- Custom ---", "", true),
	GREEN("Green", "green.png", false),
	PURPLE("Purple", "purple.png", false),
	PINK("Pink", "pink.png", false),
	ORANGE("Orange", "orange.png", false);

	private final String name;
	private final String imagePath;
	private final boolean header;

	public String toString()
	{
		return name;
	}
}
