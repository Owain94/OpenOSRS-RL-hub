package net.runelite.client.plugins.influxdb.write;

import java.util.Map;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class Series
{
	String measurement;

	@Singular
	Map<String, String> tags;
}