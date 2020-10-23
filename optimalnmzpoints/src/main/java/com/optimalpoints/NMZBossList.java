package com.optimalpoints;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class NMZBossList {
    @Getter
    private ArrayList<NMZBoss> nmzBossList;

    NMZBossList(Path bossCSVPath) {
        this.nmzBossList = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(bossCSVPath, StandardCharsets.US_ASCII)) {
            String currentLine = br.readLine();
            while (currentLine != null) {
                String[] attributes = currentLine.split(",");
                this.nmzBossList.add(new NMZBoss(attributes[0],
                        Integer.parseInt(attributes[1]),
                        Integer.parseInt(attributes[2])));
                currentLine = br.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
