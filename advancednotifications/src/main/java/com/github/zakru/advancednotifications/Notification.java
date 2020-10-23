package com.github.zakru.advancednotifications;

import lombok.Getter;
import lombok.Setter;

public abstract class Notification
{
	@Getter
	@Setter
	private transient AdvancedNotificationsPlugin plugin;

	@Getter
	@Setter
	private  boolean enabled = true;

	public Notification(AdvancedNotificationsPlugin plugin)
	{
		this.plugin = plugin;
	}

	void tryNotify(Object event)
	{
		if (!enabled) return;

		notify(event);
	}

	protected abstract void notify(Object event);

	protected void doNotification(String message)
	{
		plugin.getNotifier().notify(message);
	}
}
