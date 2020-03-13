package net.runelite.client.plugins.advancednotifications;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public class NotificationAdapter extends TypeAdapter<Notification>
{
	private final AdvancedNotificationsPlugin plugin;
	private final ItemNotificationAdapter itemNotificationAdapter;
	private final EmptyNotificationAdapter emptyNotificationAdapter;

	public NotificationAdapter(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
		this.itemNotificationAdapter = new ItemNotificationAdapter();
		this.emptyNotificationAdapter = new EmptyNotificationAdapter();
	}

	@Override
	public void write(JsonWriter out, Notification o) throws IOException
	{
		out.beginObject();
		out.name("type").value(idOf(o));
		out.name("enabled").value(o.isEnabled());
		out.name("data");
		outTyped(out, o);
		out.endObject();
	}

	@Override
	public Notification read(JsonReader in) throws IOException
	{
		int notificationType = -1;
		Notification notification = null;
		boolean enabled = true;

		in.beginObject();
		while (in.hasNext())
		{
			switch (in.nextName())
			{
				case "type":
					notificationType = in.nextInt();
					break;
				case "enabled":
					enabled = in.nextBoolean();
					break;
				case "data":
					notification = ofType(in, notificationType);
					break;
			}
		}
		in.endObject();

		notification.setEnabled(enabled);
		return notification;
	}

	private Notification ofType(JsonReader in, int type) throws IOException
	{
		switch (type)
		{
			case 0:
				return itemNotificationAdapter.read(plugin, in);
			case 1:
				return emptyNotificationAdapter.read(plugin, in);
			default:
				return null;
		}
	}

	private void outTyped(JsonWriter out, Notification o) throws IOException
	{
		if (o instanceof ItemNotification)
		{
			itemNotificationAdapter.write(plugin, out, (ItemNotification) o);
		}
		else if (o instanceof EmptyNotification)
		{
			emptyNotificationAdapter.write(plugin, out, (EmptyNotification) o);
		}
	}

	private int idOf(Notification o)
	{
		if (o instanceof ItemNotification)
		{
			return 0;
		}
		if (o instanceof EmptyNotification)
		{
			return 1;
		}
		return -1;
	}
}