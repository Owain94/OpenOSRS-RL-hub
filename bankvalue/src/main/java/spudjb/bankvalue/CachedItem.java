package spudjb.bankvalue;

import lombok.Value;

@Value
public class CachedItem
{
	int id;
	int quantity;
	String name;
	int value;
}
