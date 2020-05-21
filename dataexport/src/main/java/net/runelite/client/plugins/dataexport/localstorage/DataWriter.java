package net.runelite.client.plugins.dataexport.localstorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import net.runelite.client.plugins.dataexport.DataExportConfig;
import net.runelite.client.plugins.dataexport.DataExportItem;
import net.runelite.http.api.RuneLiteAPI;

@Slf4j
public class DataWriter
{
	private static final String FILE_EXTENSION_JSON = ".json";

	private static final String FILE_EXTENSION_CSV = ".csv";

	private static final File LOOT_RECORD_DIR = new File(RUNELITE_DIR, "Data Exports");

	private File playerFolder = LOOT_RECORD_DIR;

	private String name;

	private DataExportConfig config;

	public DataWriter(DataExportConfig config)
	{
		this.config = config;
		LOOT_RECORD_DIR.mkdir();
	}

	private static String fileNameJSON(final String dataContainerName)
	{
		return dataContainerName.toLowerCase().trim() + FILE_EXTENSION_JSON;
	}

	private static String fileNameCSV(final String dataContainerName)
	{
		return dataContainerName.toLowerCase().trim() + FILE_EXTENSION_CSV;
	}

	public void setPlayerUsername(final String username)
	{
		if (username.equalsIgnoreCase(name))
		{
			return;
		}

		playerFolder = new File(LOOT_RECORD_DIR, username);
		playerFolder.mkdir();
		name = username;
	}

	public void writeFile(String dataContainer, Map<Integer, DataExportItem> items)
	{
		if (config.downloadJSON())
		{
			writeJSON(dataContainer, items);
		}
		if (config.downloadCSV())
		{
			writeCSV(dataContainer, items);
		}
	}

	public synchronized boolean writeJSON(String dataContainer, Map<Integer, DataExportItem> items)
	{
		final String fileName = fileNameJSON(dataContainer);
		final File lootFile = new File(playerFolder, fileName);

		try
		{
			final BufferedWriter file = new BufferedWriter(new FileWriter(String.valueOf(lootFile), false));
			for (Map.Entry<Integer, DataExportItem> item : items.entrySet())
			{
				// Convert entry to JSON
				final String dataAsString = RuneLiteAPI.GSON.toJson(item.getValue());
				file.append(dataAsString);
				file.newLine();
			}
			file.close();

			return true;
		}
		catch (IOException ioe)
		{
			log.warn("Error rewriting data to file {}: {}", fileName, ioe.getMessage());
			return false;
		}
	}

	public synchronized boolean writeCSV(String dataContainer, Map<Integer, DataExportItem> items)
	{
		final String fileName = fileNameCSV(dataContainer);
		final File lootFile = new File(playerFolder, fileName);

		try
		{
			final BufferedWriter file = new BufferedWriter(new FileWriter(String.valueOf(lootFile), false));
			for (Map.Entry<Integer, DataExportItem> item : items.entrySet())
			{
				// Convert entry to CSV
				final String line = item.getValue().getCSV();
				file.append(line);
				file.newLine();
			}
			file.close();

			return true;
		}
		catch (IOException ioe)
		{
			log.warn("Error rewriting data to file {}: {}", fileName, ioe.getMessage());
			return false;
		}
	}
}