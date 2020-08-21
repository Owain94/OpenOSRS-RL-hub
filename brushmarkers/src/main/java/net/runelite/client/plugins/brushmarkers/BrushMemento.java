package net.runelite.client.plugins.brushmarkers;


import lombok.Getter;

public class BrushMemento
{
	@Getter
	private final BrushMarkerPoint point;
	@Getter
	private final boolean draw;

	public BrushMemento(BrushMarkerPoint point, boolean draw)
	{
		this.point = point;
		this.draw = draw;
	}
}
