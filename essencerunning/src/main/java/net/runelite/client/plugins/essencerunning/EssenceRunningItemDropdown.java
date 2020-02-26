package net.runelite.client.plugins.essencerunning;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class EssenceRunningItemDropdown
{
	@Getter
	@RequiredArgsConstructor
	public enum BindingNecklace
	{
		WEAR("Wear"),
		CHECK("Check"),
		USE("Use"),
		DESTROY("Destroy");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum CraftingCape
	{
		WEAR("Wear"),
		TELEPORT("Teleport"),
		USE("Use"),
		DROP("Drop");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum EarthTalisman
	{
		USE("Use"),
		LOCATE("Locate"),
		DROP("Drop");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum EssencePouch
	{
		FILL("Fill"),
		EMPTY("Empty"),
		CHECK("Check"),
		USE("Use"),
		DROP("Drop");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum PureEssence
	{
		USE("Use"),
		DROP("Drop");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum RingOfDueling
	{
		WEAR("Wear"),
		USE("Use"),
		RUB("Rub"),
		DROP("Drop");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum StaminaPotion
	{
		DRINK("Drink"),
		USE("Use"),
		EMPTY("Empty"),
		DROP("Drop");

		private final String option;
	}

	@Getter
	@RequiredArgsConstructor
	public enum HighlightBindingNecklace
	{
		OFF("Off"),
		EQUIP("Equip"),
		TWENTY_FIVE("25"),
		TWENTY_SIX("26");

		private final String option;

		@Override
		public String toString()
		{
			return this.option;
		}
	}
}
