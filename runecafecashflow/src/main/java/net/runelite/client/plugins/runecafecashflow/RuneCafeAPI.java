package net.runelite.client.plugins.runecafecashflow;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import net.runelite.api.GrandExchangeOffer;
import net.runelite.http.api.RuneLiteAPI;
import static net.runelite.http.api.RuneLiteAPI.JSON;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RuneCafeAPI
{
	private final String PROD_API_BASE_URL = "https://api.rune.cafe/api/gehistory/";
	private final String QA_API_BASE_URL = "http://localhost:8081/api/gehistory/";
	private final String apiKey;
	private final boolean qa;

	public RuneCafeAPI(String apiKey, boolean qa)
	{
		this.apiKey = apiKey;
		this.qa = qa;
	}

	private String getBaseUrl()
	{
		if (qa)
		{
			return QA_API_BASE_URL;
		}
		else
		{
			return PROD_API_BASE_URL;
		}
	}

	public void postGEHistorySnapshot(String osrsName,
									List<GEHistoryRecord> records,
									Consumer<Response> onResponse,
									Consumer<Exception> onError)
	{
		String urlString;
		urlString = getBaseUrl() + URLEncoder.encode(osrsName, StandardCharsets.UTF_8) + "/snapshot";
		;

		this.post(urlString, records, onResponse, onError);
	}

	public void postLiveTrade(String osrsName,
							GrandExchangeOffer o,
							Consumer<Response> onResponse,
							Consumer<Exception> onError)
	{
		String urlString;
		urlString = getBaseUrl() + URLEncoder.encode(osrsName, StandardCharsets.UTF_8) + "/trade";
		;

		this.post(urlString, new GEHistoryRecord(o), onResponse, onError);

	}

	private void post(String url, Object body, Consumer<Response> onResponse, Consumer<Exception> onError)
	{
		if (this.apiKey == null || this.apiKey.isEmpty() || this.apiKey.matches("\\s+"))
		{
			onError.accept(new IllegalStateException("API key is blank. An API key from rune.cafe is necessary."));
			return;
		}
		Gson gson = new Gson();
		Request request = new Request.Builder()
			.header("Authorization", "Bearer " + this.apiKey)
			.header("Content-Type", "application/json")
			.post(RequestBody.create(JSON, gson.toJson(body)))
			.url(HttpUrl.parse(url))
			.build();

		RuneLiteAPI.CLIENT.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				onError.accept(e);
			}

			@Override
			public void onResponse(Call call, Response response)
			{
				onResponse.accept(response);
				response.close();
			}
		});
	}
}