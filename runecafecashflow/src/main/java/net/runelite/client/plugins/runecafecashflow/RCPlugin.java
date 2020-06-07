package net.runelite.client.plugins.runecafecashflow;

import com.google.inject.Provides;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import okhttp3.Response;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "RuneCafe Cash Flow",
	description = "Plugin providing RuneLite integration for rune.cafe.",
	tags = {"external", "integration", "prices", "trade"},
	type = PluginType.MISCELLANEOUS,
	enabledByDefault = false
)
public class RCPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private RCPluginConfig config;

	@Provides
	RCPluginConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RCPluginConfig.class);
	}

	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged offerEvent)
	{
		RuneCafeAPI apiClient = new RuneCafeAPI(config.apiKey(), false);
		GrandExchangeOffer offer = offerEvent.getOffer();

		switch (offer.getState())
		{
			case BUYING:
			case SELLING:
			case EMPTY:
				return;
			case CANCELLED_BUY:
			case CANCELLED_SELL:
				if (offer.getQuantitySold() == 0)
				{
					return;
				}
		}

		apiClient.postLiveTrade(client.getLocalPlayer().getName(),
			offer,
			r -> onResponse(r,
				"Successfully sent a trade to rune.cafe!",
				"Something went wrong while submitting a trade to rune.cafe!"),
			e -> onError(e, "Something went wrong (client-side) while submitting a trade to rune.cafe!")
		);
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
		if (!optionalHistoryTitleWidget.isPresent())
		{
			return;
		}

		Widget historyTitleWidget = optionalHistoryTitleWidget.get();
		clientThread.invokeLater(() -> {
			RuneCafeAPI apiClient = new RuneCafeAPI(config.apiKey(), false);
			Widget[] geHistoryData = historyTitleWidget.getParent().getParent().getStaticChildren()[2].getDynamicChildren();

			List<GEHistoryRecord> records = new ArrayList<>();
			for (int i = 0; i < geHistoryData.length; i += 6)
			{
				records.add(new GEHistoryRecord(geHistoryData, i));
			}

			apiClient.postGEHistorySnapshot(client.getLocalPlayer().getName(),
				records,
				r -> onResponse(r,
					"Successfully sent a GE history snapshot to rune.cafe!",
					"Something went wrong while submitting ge history to rune.cafe!"),
				e -> onError(e, "Something went wrong (client-side) while submitting ge history to rune.cafe!")
			);

		});
	}

	private void onResponse(Response r, String successMessage, String errorMessage)
	{
		clientThread.invokeLater(() -> {
			if (r.isSuccessful() && config.echoUploads())
			{
				if (config.echoUploads())
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "rune.cafe",
						successMessage,
						"rune.cafe");
				}
			}
			else if (!r.isSuccessful() && config.echoErrors())
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "rune.cafe",
					errorMessage,
					"rune.cafe");

				String body;
				try
				{
					body = r.body().string();
					if (body.isEmpty())
					{
						body = "<empty>";
					}
				}
				catch (IOException | IllegalStateException e)
				{
					body = "<error reading response body>";
				}

				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "rune.cafe",
					"HTTP " + r.code() + ": " + body,
					"rune.cafe");
			}
		});
	}

	private void onError(Exception e, String message)
	{
		if (!config.echoErrors())
		{
			return;
		}

		clientThread.invokeLater(() -> {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "rune.cafe",
				message,
				"rune.cafe");
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "rune.cafe",
				e.getClass().getSimpleName() + ": " + e.getMessage(),
				"rune.cafe");
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