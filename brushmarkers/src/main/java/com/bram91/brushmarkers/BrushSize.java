package com.bram91.brushmarkers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BrushSize
{
	ONE("1",1),
	THREE("3",3),
	FIVE("5", 5);

	private final String name;
	@Getter
	private final int size;

	@Override
	public String toString()
	{
		return name;
	}
}
