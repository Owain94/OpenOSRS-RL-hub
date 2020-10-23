package com.github.zakru.advancednotifications;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

public class ComparatorAdapter extends TypeAdapter<InventoryComparator.Pointer>
{
	@Override
	public void write(JsonWriter out, InventoryComparator.Pointer o) throws IOException
	{
		out.value(Arrays.asList(InventoryComparator.COMPARATORS).indexOf(o.object));
	}

	@Override
	public InventoryComparator.Pointer read(JsonReader in) throws IOException
	{
		return new InventoryComparator.Pointer(InventoryComparator.COMPARATORS[in.nextInt()]);
	}
}
