package thestonedturtle.runiterocks;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(RuniteRocksConfig.GROUP)
public interface RuniteRocksConfig extends Config
{
	String GROUP = "runiterocks";

	@ConfigItem(
		position = 0,
		keyName = "respawnCounter",
		name = "Respawn Counter",
		description = "<html>If enabled shows a ticking countdown to the respawn time" +
			"<br/>If disabled shows the time at which the rock should respawn</html>"
	)
	default boolean respawnCounter()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "visitCounter",
		name = "Last Visit Counter",
		description = "<html>If enabled shows a ticking timer for how long since you checked on that rock" +
			"<br/>If disabled shows the time at which you last checked on that rock</html>"
	)
	default boolean visitCounter()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "accurateRespawnPriority",
		name = "Accurate Respawn Priority",
		description = "<html>When enabled and sorting by respawn time Accurate times will be prioritized over Inaccurate times</html>"
	)
	default boolean accurateRespawnPriority()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "ignoreInaccurate",
		name = "Ignore Inaccurate",
		description = "<html>Should rocks that are inaccurate be ignored from the tracker?</html>"
	)
	default boolean ignoreInaccurate()
	{
		return false;
	}
}
