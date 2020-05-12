package net.runelite.client.plugins.influxdb.write;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.influxdb.dto.Point;

@Value
@Builder
public class Measurement
{
	Series series;

	@Builder.Default
	long time = System.currentTimeMillis();

	@Singular
	Map<String, String> stringValues;

	@Singular
	Map<String, Number> numericValues;

	// influx accepts Map<String, Object>, where object is String | Number
	@SuppressWarnings("unchecked")
	Point toInflux()
	{
		return Point.measurement(series.getMeasurement())
			.tag(series.getTags())
			.time(time, TimeUnit.MILLISECONDS)
			.fields((Map) getStringValues())
			.fields((Map) getNumericValues())
			.build();
	}
}