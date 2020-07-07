package net.runelite.client.plugins.runecafecashflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.api.GrandExchangeOfferState;
import static net.runelite.api.GrandExchangeOfferState.BOUGHT;
import static net.runelite.api.GrandExchangeOfferState.BUYING;
import static net.runelite.api.GrandExchangeOfferState.CANCELLED_BUY;
import static net.runelite.api.GrandExchangeOfferState.CANCELLED_SELL;
import static net.runelite.api.GrandExchangeOfferState.EMPTY;
import static net.runelite.api.GrandExchangeOfferState.SELLING;
import static net.runelite.api.GrandExchangeOfferState.SOLD;
import net.runelite.api.events.GrandExchangeOfferChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GEEventDebouncer implements Consumer<GrandExchangeOfferChanged>
{
	private final Logger log = LoggerFactory.getLogger(GEEventDebouncer.class);
	private final Map<Integer, GrandExchangeOffer> state;
	private final Consumer<GrandExchangeOfferChanged> downstream;

	private static final Map<GrandExchangeOfferState, Set<GrandExchangeOfferState>> VALID_TRANSITIONS;

	static
	{
		VALID_TRANSITIONS = new HashMap<>();
		Set<GrandExchangeOfferState> tmp;

		tmp = new HashSet<>();
		tmp.add(BUYING);
		tmp.add(SELLING);
		VALID_TRANSITIONS.put(EMPTY, tmp);

		tmp = new HashSet<>();
		tmp.add(CANCELLED_BUY);
		tmp.add(BOUGHT);
		VALID_TRANSITIONS.put(BUYING, tmp);

		tmp = new HashSet<>();
		tmp.add(CANCELLED_SELL);
		tmp.add(SOLD);
		VALID_TRANSITIONS.put(SELLING, tmp);

		tmp = new HashSet<>();
		tmp.add(EMPTY);
		VALID_TRANSITIONS.put(CANCELLED_BUY, tmp);
		VALID_TRANSITIONS.put(CANCELLED_SELL, tmp);
		VALID_TRANSITIONS.put(BOUGHT, tmp);
		VALID_TRANSITIONS.put(SOLD, tmp);
	}

	public GEEventDebouncer(Consumer<GrandExchangeOfferChanged> downstream)
	{
		this.downstream = downstream;
		state = new HashMap<>();
	}


	@Override
	public void accept(GrandExchangeOfferChanged event)
	{
		if (!state.containsKey(event.getSlot()))
		{
			digest(event);
			return;
		}

		if (event.getOffer() == null)
		{
			log.warn("Got an event for slot {} where offer == null. Is this normal?", event.getSlot());
			this.state.remove(event.getSlot());
		}

		GrandExchangeOffer cur = state.get(event.getSlot());
		GrandExchangeOffer next = event.getOffer();

		if (cur.getState() != EMPTY && next.getState() != EMPTY)
		{
			if (cur.getItemId() != next.getItemId())
			{
				log.warn("Last item id for slot {} was {}, but just got an event with item id {}",
					event.getSlot(), cur.getItemId(), next.getItemId());
				digest(event);
				return;
			}

			if (cur.getTotalQuantity() != next.getTotalQuantity())
			{
				log.warn("Last offer for slot {} had total qty {}, but just got an event with total qty {}.",
					event.getSlot(), cur.getTotalQuantity(), next.getTotalQuantity());
				digest(event);
				return;
			}
		}

		if (cur.getState() == next.getState())
		{
			// this is only valid if we're in BUYING or SELLING
			if (cur.getState() == BUYING || cur.getState() == SELLING)
			{
				if (cur.getQuantitySold() == next.getQuantitySold())
				{
					// a true duplicate. swallow.
				}
				else
				{
					digest(event);
				}
			}
			else
			{
				// a duplicate. swallow.
			}
		}
		else
		{
			if (VALID_TRANSITIONS.get(cur.getState()).contains(next.getState()))
			{
				digest(event);
			}
			else
			{
				log.warn("Weird transition! {} -> {}", cur.getState(), next.getState());
				digest(event);
			}
		}
	}

	private void digest(GrandExchangeOfferChanged event)
	{
		state.put(event.getSlot(), event.getOffer());
		this.downstream.accept(event);
	}
}
