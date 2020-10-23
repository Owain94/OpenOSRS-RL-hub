package com.playtime;

import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.RuneLiteAPI;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
@Singleton
public class TimeRecordWriter {
    private static final String FILE_EXTENSION = ".log";
    private static final File PLAY_TIME_DIR = new File(RUNELITE_DIR, "playTime");
    private String playerName;

    @Inject
    public TimeRecordWriter() {
        PLAY_TIME_DIR.mkdir();
    }

    public void setPlayerUsername(final String username)
    {
        playerName = username;
    }

    public synchronized ArrayList<PlayTimeRecord> loadPlayTimeRecords()
    {
        if (playerName == null) {
            return new ArrayList();
        }
        final String fileName = playerName.trim() + FILE_EXTENSION;
        final File file = new File(PLAY_TIME_DIR, fileName);
        final ArrayList<PlayTimeRecord> data = new ArrayList<>();
        if (!file.exists()) {
            return new ArrayList();
        }
        try (final BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                // Skips the empty line at end of file
                if (line.length() > 0)
                {
                    final PlayTimeRecord r = RuneLiteAPI.GSON.fromJson(line, PlayTimeRecord.class);
                    data.add(r);
                }
            }

        }
        catch (FileNotFoundException e)
        {
            log.debug("File not found: {}", fileName);
        }
        catch (IOException e)
        {
            log.warn("IOException for file {}: {}", fileName, e.getMessage());
        }

        return data;
    }

    public synchronized boolean writePlayTimeFile(final ArrayList<PlayTimeRecord> times)
    {
        final File timeFile = new File(PLAY_TIME_DIR, playerName.trim() + FILE_EXTENSION);

        try
        {
            final BufferedWriter file = new BufferedWriter(new FileWriter(String.valueOf(timeFile), false));
            for (final PlayTimeRecord rec : times)
            {
                // Convert entry to JSON
                final String dataAsString = RuneLiteAPI.GSON.toJson(rec);
                file.append(dataAsString);
                file.newLine();
            }
            file.close();

            return true;
        }
        catch (IOException ioe)
        {
            log.warn("Error rewriting loot data to file {}: {}", playerName.trim() + FILE_EXTENSION, ioe.getMessage());
            return false;
        }
    }
}
