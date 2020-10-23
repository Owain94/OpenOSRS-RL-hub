package melky.resourcepacks.event;

import java.util.List;
import lombok.Value;
import melky.resourcepacks.hub.ResourcePackManifest;
import net.runelite.api.events.Event;

@Value
public class ResourcePacksChanged implements Event
{
	List<ResourcePackManifest> newManifest;
}
