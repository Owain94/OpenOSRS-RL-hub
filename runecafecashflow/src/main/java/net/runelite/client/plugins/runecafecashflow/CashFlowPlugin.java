package net.runelite.client.plugins.runecafecashflow;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.http.api.RuneLiteAPI;
import static net.runelite.http.api.RuneLiteAPI.JSON;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "RuneCafe Cash Flow",
	description = "RuneCafe plugin providing RuneLite integration to track your cash flow on the GE.",
	tags = {"external", "integration", "prices", "trade"},
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
@Slf4j
public class CashFlowPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private CashFlowConfig config;

	@Provides
	CashFlowConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CashFlowConfig.class);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoadedEvent)
	{
		if (widgetLoadedEvent.getGroupId() != 383)
		{
			return;
		}

		Widget topGEWidget = client.getWidget(383, 0);
		Optional<Widget> optionalHistoryTitleWidget = findChildDepthFirst(topGEWidget, cw -> cw.getText().contains("Grand Exchange Trade History"));
		if (optionalHistoryTitleWidget.isEmpty())
		{
			return;
		}

		Widget historyTitleWidget = optionalHistoryTitleWidget.get();
		clientThread.invokeLater(() -> {
			Widget[] geHistoryData = historyTitleWidget.getParent().getParent().getStaticChildren()[2].getDynamicChildren();

			List<GEHistoryRecord> records = new ArrayList<>();
			for (int i = 0; i < geHistoryData.length; i += 6)
			{
				records.add(new GEHistoryRecord(geHistoryData, i));
			}

			Gson gson = new Gson();
			System.out.println(gson.toJson(records));

			String urlString;
			urlString = "https://api.rune.cafe/api/gehistory/" + URLEncoder.encode(client.getLocalPlayer().getName(), StandardCharsets.UTF_8) + "/snapshot";
			;

			Request request = new Request.Builder()
				.header("User-Agent", "OpenOSRS")
				.header("Authorization", "Bearer " + config.apiKey())
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.post(RequestBody.create(JSON, gson.toJson(records)))
				.url(HttpUrl.parse(urlString))
				.build();

			RuneLiteAPI.CLIENT.newCall(request).enqueue(new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					log.error("Error sending snapshot.", e);
				}

				@Override
				public void onResponse(Call call, Response response)
				{
					response.close();
				}
			});

		});
	}

	private Optional<Widget> findChildDepthFirst(Widget root, Predicate<Widget> p)
	{
		if (p.test(root))
		{
			return Optional.of(root);
		}


		Stream<Widget> children = Stream.concat(Stream.concat(
			Stream.of(root.getStaticChildren()),
			Stream.of(root.getNestedChildren())),
			Stream.of(root.getDynamicChildren()));

		return children
			.map(c -> findChildDepthFirst(c, p))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();

	}
}