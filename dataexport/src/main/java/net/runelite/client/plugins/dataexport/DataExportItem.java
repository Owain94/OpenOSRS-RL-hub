package net.runelite.client.plugins.dataexport;

import lombok.Getter;

public class DataExportItem
{
	@Getter
	private final int id;

	@Getter
	private final int quantity;

	@Getter
	private final String name;

	DataExportItem(String name, int quantity, int id)
	{
		this.id = id;
		this.quantity = quantity;
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name + " x " + quantity;
	}

	public String getCSV()
	{
		return id + "," + quantity + "," + name;
	}
}