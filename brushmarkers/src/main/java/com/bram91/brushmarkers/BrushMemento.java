package com.bram91.brushmarkers;


import lombok.Getter;

public class BrushMemento
{
	@Getter
	private BrushMarkerPoint point;
	@Getter
	private boolean draw;

	public BrushMemento(BrushMarkerPoint point, boolean draw)
	{
		this.point = point;
		this.draw = draw;
	}
}
