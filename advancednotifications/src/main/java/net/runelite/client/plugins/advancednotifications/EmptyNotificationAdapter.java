package net.runelite.client.plugins.advancednotifications;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Arrays;

public class EmptyNotificationAdapter
{
	public void write(JsonWriter out, EmptyNotification o) throws IOException
	{
		out.beginObject();
		out.name("comparator").value(Arrays.asList(InventoryComparator.COMPARATORS).indexOf(o.getComparator()));
		if (o.getComparator().takesParam())
		{
			out.name("comparatorParam").value(o.getComparatorParam());
		}
		out.endObject();
	}

	public EmptyNotification read(AdvancedNotificationsPlugin plugin, JsonReader in) throws IOException
	{
		final EmptyNotification notification = new EmptyNotification(plugin);

		in.beginObject();
		while (in.hasNext())
		{
			switch (in.nextName())
			{
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