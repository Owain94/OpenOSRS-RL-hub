package net.runelite.client.plugins.pushnotification;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import java.io.IOException;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.http.api.RuneLiteAPI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Push Notifications",
	description = "Send notifications to your phone or other devices",
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class PushNotificationsPlugin extends Plugin
{
	@Inject
	private PushNotificationsConfig config;

	@Provides
	PushNotificationsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PushNotificationsConfig.class);
	}

	@Subscribe
	public void onNotificationFired(NotificationFired event)
	{
		if (Strings.isNullOrEmpty(config.pushbullet()))
		{
			return;
		}

		HttpUrl url = new HttpUrl.Builder()
			.scheme("https")
			.host("api.pushbullet.com")
			.addPathSegment("v2")
			.addPathSegment("pushes")
			.build();

		RequestBody push = new FormBody.Builder()
			.add("body", "You should probably do something about that..")
			.add("title", event.getMessage())
			.add("type", "note")
			.build();

		Request request = new Request.Builder()
			.header("User-Agent", "OpenOSRS")
			.header("Access-Token", config.pushbullet())
			.header("Content-Type", "application/json")
			.post(push)
			.url(url)
			.build();

		sendRequest("Pushbullet", request);
	}

	private static void sendRequest(String platform, Request request)
	{
		RuneLiteAPI.CLIENT.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Error sending {} notification, caused by {}.", platform, e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				response.close();
			}
		});
	}
}