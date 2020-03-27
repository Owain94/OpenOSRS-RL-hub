package net.runelite.client.plugins.advancednotifications;

import lombok.Value;

@Value
public class InventoryEvent
{
	int itemID;
	int count;
	int previousCount;
}