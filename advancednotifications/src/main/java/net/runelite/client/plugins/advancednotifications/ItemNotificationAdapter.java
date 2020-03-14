package net.runelite.client.plugins.advancednotifications;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;

public class ItemNotificationAdapter
{
	public void write(AdvancedNotificationsPlugin plugin, JsonWriter out, ItemNotification o) throws IOException
	{
		out.beginObject();
		out.name("item").value(o.getItem());
		out.name("comparator").value(Arrays.asList(InventoryComparator.COMPARATORS).indexOf(o.getComparator()));
		if (o.getComparator().takesParam())
		{
			out.name("comparatorParam").value(o.getComparatorParam());
		}
		out.endObject();
	}

	public ItemNotification read(AdvancedNotificationsPlugin plugin, JsonReader in) throws IOException
	{
		final ItemNotification notification = new ItemNotification(plugin);

		in.beginObject();
		while (in.hasNext())
		{
			switch (in.nextName())
			{
				case "item":
					notification.setItem(in.nextString());
					break;
				case "comparator":
					notification.setComparator(InventoryComparator.COMPARATORS[in.nextInt()]);
					break;
				case "comparatorParam":
					notification.setComparatorParam(in.nextInt());
					break;
			}
		}
		in.endObject();

		return notification;
	}
}