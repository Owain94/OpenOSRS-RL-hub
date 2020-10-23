package com.github.zakru.advancednotifications;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ChatMessageType;

import java.util.Arrays;

public class ItemNotification extends Notification
{
	@Getter
	@Setter
	private String item = "Coins";
	@Getter
	@Setter
	private InventoryComparator.Pointer comparator = new InventoryComparator.Pointer(InventoryComparator.COMPARATORS[0]);
	@Getter
	@Setter
	private int comparatorParam = 0;

	public ItemNotification(AdvancedNotificationsPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public void notify(Object event)
	{
		if (!(event instanceof InventoryEvent)) return;

		InventoryEvent e = (InventoryEvent)event;

		if (getPlugin().getItemManager().getItemDefinition(e.getItemID()).getName().equalsIgnoreCase(item)
			&& comparator.object.shouldNotify(e.getPreviousCount(), e.getCount(), comparatorParam))
		{
			doNotification(comparator.object.notification(item, comparatorParam));
		}
	}
}
