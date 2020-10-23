package com.github.zakru.advancednotifications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class NotificationAdapter extends TypeAdapter<Notification>
{
	private final AdvancedNotificationsPlugin plugin;
	private final Gson gson;

	public NotificationAdapter(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
		gson = new GsonBuilder()
			.registerTypeAdapter(Notification.class, this)
			.registerTypeAdapter(InventoryComparator.Pointer.class, new ComparatorAdapter())
			.create();
	}

	@Override
	public void write(JsonWriter out, Notification o) throws IOException
	{
		out.beginObject();
		out.name("type").value(idOf(o));
		out.name("data"); outTyped(out, o);
		out.endObject();
	}

	@Override
	public Notification read(JsonReader in) throws IOException
	{
		int notificationType = -1;
		Notification notification = null;

		in.beginObject();
		while (in.hasNext())
		{
			switch (in.nextName())
			{
				case "type":
					notificationType = in.nextInt();
					break;
				case "data":
					notification = ofType(in, notificationType);
					break;
				default:
					in.skipValue();
			}
		}
		in.endObject();

		notification.setPlugin(plugin);
		return notification;
	}

	private Notification ofType(JsonReader in, int type) throws IOException
	{
		switch (type)
		{
			case 0:
				return gson.fromJson(in, ItemNotification.class);
			case 1:
				return gson.fromJson(in, EmptyNotification.class);
			case 2:
				return gson.fromJson(in, NotificationGroup.class);
			default:
				return null;
		}
	}

	private void outTyped(JsonWriter out, Notification o) throws IOException
	{
		if (o instanceof ItemNotification) gson.toJson(o, ItemNotification.class, out);
		else if (o instanceof EmptyNotification) gson.toJson(o, EmptyNotification.class, out);
		else if (o instanceof NotificationGroup) gson.toJson(o, NotificationGroup.class, out);
	}

	private int idOf(Notification o)
	{
		if (o instanceof ItemNotification) return 0;
		if (o instanceof EmptyNotification) return 1;
		if (o instanceof NotificationGroup) return 2;
		return -1;
	}
}
