package com.patchpayment;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("patchpayment")
public interface PatchPaymentConfig extends Config {

	@ConfigItem(
			keyName = "examinetext",
			name = "Check Payment With Examine",
			description = "Examine an item to see what the payment is and remove the 'Check' menu option.",
			position = 1
	)
	default boolean checkWithExamine() { return false; }

	@ConfigItem(
			keyName = "bankmenuitem",
			name = "Enable Check in Bank",
			description = "Enables the ability to check payments while in the bank interface.",
			position = 2
	)
	default boolean checkInBank() { return false; }
}
