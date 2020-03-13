package net.runelite.client.plugins.advancednotifications;

import lombok.Getter;
import lombok.Setter;

public class ItemNotification extends Notification
{
	@Getter
	@Setter
	private String item;

	@Getter
	@Setter
	private InventoryComparator comparator;

	@Getter
	@Setter
	private int comparatorParam;

	public ItemNotification(AdvancedNotificationsPlugin plugin)
	{
		super(plugin);
		item = "Coins";
		comparator = InventoryComparator.COMPARATORS[0];
		comparatorParam = 0;
	}

	@Override
	public void notify(Object event)
	{
		if (!(event instanceof InventoryEvent))
		{
			return;
		}

		InventoryEvent e = (InventoryEvent) event;

		if (getPlugin().getItemManager().getItemDefinition(e.getItemID()).getName().equalsIgnoreCase(item)
			&& comparator.shouldNotify(e.getPreviousCount(), e.getCount(), comparatorParam))
		{
			doNotification(comparator.notification(item, comparatorParam));
		}
	}
}