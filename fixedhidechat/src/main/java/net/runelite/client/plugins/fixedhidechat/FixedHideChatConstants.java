package net.runelite.client.plugins.fixedhidechat;

import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;

public class FixedHideChatConstants
{
	private static final Map.Entry<Integer, Integer> CHATBOX_MESSAGES_DIALOG = new AbstractMap.SimpleEntry<>(
		WidgetID.CHATBOX_GROUP_ID,
		561
	);

	private static final Map.Entry<Integer, Integer> CHATBOX_MESSAGES_CONTAINER = new AbstractMap.SimpleEntry<>(
		WidgetInfo.CHATBOX_CONTAINER.getGroupId(),
		WidgetInfo.CHATBOX_CONTAINER.getChildId()
	);

	private static final Map.Entry<Integer, Integer> FIXED_VIEWPORT_BANK_POPUP_CONTAINER = new AbstractMap.SimpleEntry<>(
		WidgetInfo.BANK_CONTAINER.getGroupId(),
		0
	);

	static final Map.Entry<Integer, Integer> FIXED_MAIN = new AbstractMap.SimpleEntry<>(
		WidgetID.FIXED_VIEWPORT_GROUP_ID,
		12
	);

	static final int DEFAULT_VIEW_HEIGHT = 334;
	static final int EXPANDED_VIEW_HEIGHT = 476;

	static final Set<Map.Entry<Integer, Integer>> AUTO_EXPAND_WIDGETS = ImmutableSet
		.<Map.Entry<Integer, Integer>>builder()
		.add(CHATBOX_MESSAGES_DIALOG)
		.add(CHATBOX_MESSAGES_CONTAINER)
		.build();

	static final Set<Map.Entry<Integer, Integer>> TO_CONTRACT_WIDGETS = ImmutableSet
		.<Map.Entry<Integer, Integer>>builder()
		.add(FIXED_VIEWPORT_BANK_POPUP_CONTAINER)
		.build();
}