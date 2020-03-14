package net.runelite.client.plugins.advancednotifications;

import lombok.Getter;
import lombok.Setter;

public class EmptyNotification extends Notification
{
	@Getter
	@Setter
	private InventoryComparator comparator;

	@Getter
	@Setter
	private int comparatorParam;

	public EmptyNotification(AdvancedNotificationsPlugin plugin)
	{
		super(plugin);
		this.comparator = InventoryComparator.COMPARATORS[0];
		this.comparatorParam = 0;
	}

	@Override
	public void notify(Object event)
	{
		if (!(event instanceof InventoryEvent))
		{
			return;
		}

		InventoryEvent e = (InventoryEvent) event;

		if (e.getItemID() == -1 && comparator.shouldNotify(e.getPreviousCount(), e.getCount(), comparatorParam))
		{
			doNotification(comparator.notification("empty space", comparatorParam));
		}
	}
}