package net.runelite.client.plugins.resourcepacks.event;

import java.util.List;
import lombok.Value;
import net.runelite.api.events.Event;
import net.runelite.client.plugins.resourcepacks.hub.ResourcePackManifest;

@Value
public class ResourcePacksChanged implements Event
{
	List<ResourcePackManifest> newManifest;
}