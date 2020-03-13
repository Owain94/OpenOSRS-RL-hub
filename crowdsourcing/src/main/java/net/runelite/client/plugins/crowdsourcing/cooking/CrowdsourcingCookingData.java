package net.runelite.client.plugins.crowdsourcing.cooking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrowdsourcingCookingData
{
	private final String message;
	private final boolean hasCookingGauntlets;
	private final boolean inHosidiusKitchen;
	private final boolean kourendElite;
	private final int lastGameObjectClicked;
	private final int level;
}