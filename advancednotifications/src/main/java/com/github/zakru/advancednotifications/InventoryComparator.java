package com.github.zakru.advancednotifications;

public interface InventoryComparator
{
	public static class Pointer
	{
		public InventoryComparator object;

		public Pointer(InventoryComparator object)
		{
			this.object = object;
		}
	}

	boolean shouldNotify(int previousCount, int newCount, int param);
	boolean takesParam();
	String notification(String item, int param);

	InventoryComparator COUNT_CHANGED = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount != previousCount;
		}

		@Override
		public boolean takesParam()
		{
			return false;
		}

		@Override
		public String notification(String item, int param)
		{
			return "Your amount of " + item + " changed";
		}

		@Override
		public String toString()
		{
			return "+-";
		}
	};

	InventoryComparator COUNT_INCREASED = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount > previousCount;
		}

		@Override
		public boolean takesParam()
		{
			return false;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You got " + item;
		}

		@Override
		public String toString()
		{
			return "+";
		}
	};

	InventoryComparator COUNT_DECREASED = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount < previousCount;
		}

		@Override
		public boolean takesParam()
		{
			return false;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You lost " + item;
		}

		@Override
		public String toString()
		{
			return "-";
		}
	};

	InventoryComparator EQUAL = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount == param && previousCount != param;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You now have " + param + " " + item;
		}

		@Override
		public String toString()
		{
			return "=";
		}
	};

	InventoryComparator NOT_EQUAL = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount != param && previousCount == param;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You no longer have " + param + " " + item;
		}

		@Override
		public String toString()
		{
			return "≠";
		}
	};

	InventoryComparator LESS_THAN = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount < param && previousCount >= param;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You now have less than " + param + " " + item;
		}

		@Override
		public String toString()
		{
			return "<";
		}
	};

	InventoryComparator GREATER_THAN = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount > param && previousCount <= param;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You now have more than " + param + " " + item;
		}

		@Override
		public String toString()
		{
			return ">";
		}
	};

	InventoryComparator LESS_OR_EQUAL = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount <= param && previousCount > param;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You now have at most " + param + " " + item;
		}

		@Override
		public String toString()
		{
			return "≤";
		}
	};

	InventoryComparator GREATER_OR_EQUAL = new InventoryComparator()
	{

		@Override
		public boolean shouldNotify(int previousCount, int newCount, int param)
		{
			return newCount >= param && previousCount < param;
		}

		@Override
		public boolean takesParam()
		{
			return true;
		}

		@Override
		public String notification(String item, int param)
		{
			return "You now have at least " + param + " " + item;
		}

		@Override
		public String toString()
		{
			return "≥";
		}
	};

	InventoryComparator[] COMPARATORS = new InventoryComparator[]
	{
			COUNT_CHANGED, COUNT_INCREASED, COUNT_DECREASED, EQUAL, NOT_EQUAL,
			LESS_THAN, GREATER_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL,
	};
}
