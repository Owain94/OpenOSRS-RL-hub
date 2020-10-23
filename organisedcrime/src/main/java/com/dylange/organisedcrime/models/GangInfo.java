package com.dylange.organisedcrime.models;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GangInfo implements Comparable<GangInfo> {
    private static Pattern timeRegex = Pattern.compile("([0-9]+)");
    private static Pattern nowRegex = Pattern.compile(".*(now|imminently).*");

    private String locationMessage;
    private int world;
    private GangExpectedTime expectedTime;
    private OrganisedCrimeLocation correspondingLocation;

    public GangInfo(String locationMessage, String timeMessage, int world) {
        this.locationMessage = locationMessage;
        this.world = world;

        long timeRead = System.currentTimeMillis();

        Matcher timeMatcher = timeRegex.matcher(timeMessage);
        if (timeMatcher.find()) {
            this.expectedTime = new GangExpectedTime(
                    timeRead,
                    Integer.parseInt(timeMatcher.group())
            );
        } else {
            Matcher nowMatcher = nowRegex.matcher(timeMessage);
            if (nowMatcher.find()) {
                this.expectedTime = new GangExpectedTime(timeRead, 0);
            } else {
                throw new IllegalArgumentException(String.format("Failed to parse time from \"%s\"", timeMessage));
            }
        }
    }

    public String getLocationMessage() {
        return locationMessage;
    }

    public int getWorld() {
        return world;
    }

    public GangExpectedTime getExpectedTime() {
        return expectedTime;
    }

    public OrganisedCrimeLocation getLocation() throws IllegalStateException {
        if (correspondingLocation != null) return correspondingLocation;

        for (OrganisedCrimeLocation loc : OrganisedCrimeLocations.allLocations) {
            if (loc.getLocationMessage().equals(this.locationMessage)) {
                correspondingLocation = loc;
            }
        }
        if (correspondingLocation != null) {
            return correspondingLocation;
        } else {
            throw new IllegalStateException("Corresponding location info not found for data read from information board.");
        }
    }

    @Override
    public int compareTo(GangInfo gangInfo) {
        return 0;
    }
}
