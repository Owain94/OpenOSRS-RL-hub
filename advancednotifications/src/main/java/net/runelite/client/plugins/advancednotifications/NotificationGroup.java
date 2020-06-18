package net.runelite.client.plugins.advancednotifications;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class NotificationGroup extends Notification implements DraggableContainer
{
	@Getter
	@Setter
	private String name = "Group";
	@Getter
	@Setter
	private boolean collapsed = false;
	@Getter
	private final List<Notification> notifications = new ArrayList<>();

	public NotificationGroup(AdvancedNotificationsPlugin plugin)
	{
		super(plugin);
	}

	@Override
	protected void notify(Object event)
	{
		for (Notification n : notifications)
		{
			n.tryNotify(event);
		}
	}
}