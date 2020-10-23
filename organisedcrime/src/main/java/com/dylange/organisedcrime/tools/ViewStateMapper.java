package com.dylange.organisedcrime.tools;

import com.dylange.organisedcrime.config.OrganisedCrimeConfig;
import com.dylange.organisedcrime.models.GangExpectedTime;
import com.dylange.organisedcrime.models.GangInfo;
import com.dylange.organisedcrime.models.OrganisedCrimeLocation;
import com.dylange.organisedcrime.ui.LocationViewState;

import java.util.*;

public class ViewStateMapper {
    public static List<LocationViewState> gangInfoMapToLocationListItems(Map<Integer, GangInfo> gangInfoMap, OrganisedCrimeConfig config) {

        // A mapping of unique locations to a list of collected gang info
        HashMap<OrganisedCrimeLocation, List<GangInfo>> locationToInfoMap = new HashMap<>();

        // Go through the provided input of gang info per world checked, and add it to the above mapping if it is allowed
        // per the provided configuration. i.e. If Arceeus is not ticked, Arceeus locations will not be added.
        gangInfoMap.forEach((world, gangInfo) -> {
            if (!gangInfo.getLocation().isMultiCombat() && config.multiCombatOnly()) return;
            if (gangInfo.getLocation().getArea() == OrganisedCrimeLocation.Area.Arceeus && !config.trackArceeus()) return;
            if (gangInfo.getLocation().getArea() == OrganisedCrimeLocation.Area.Hosidius && !config.trackHosidius()) return;
            if (gangInfo.getLocation().getArea() == OrganisedCrimeLocation.Area.Lovakengj && !config.trackLovakengj()) return;
            if (gangInfo.getLocation().getArea() == OrganisedCrimeLocation.Area.Piscarilius && !config.trackPiscarilius()) return;
            if (gangInfo.getLocation().getArea() == OrganisedCrimeLocation.Area.Shayzien && !config.trackShayzien()) return;
            if (gangInfo.getLocation().getArea() == OrganisedCrimeLocation.Area.Other && !config.trackOther()) return;

            List<GangInfo> existingInfo = locationToInfoMap.get(gangInfo.getLocation());
            if (existingInfo == null) {
                locationToInfoMap.put(gangInfo.getLocation(), Collections.singletonList(gangInfo));
            } else {
                List<GangInfo> updatedList = new ArrayList<>(existingInfo);
                updatedList.add(gangInfo);
                locationToInfoMap.put(gangInfo.getLocation(), updatedList);
            }
        });

        ArrayList<LocationViewState> viewStates = new ArrayList<>();
        locationToInfoMap.forEach((organisedCrimeLocation, gangInfoForLocation) -> {
            SortedMap<GangExpectedTime, Integer> worldToExpectedTime = new TreeMap<>();
            gangInfoForLocation.forEach(gangInfo -> worldToExpectedTime.put(gangInfo.getExpectedTime(), gangInfo.getWorld()));

            viewStates.add(
                    new LocationViewState(
                            organisedCrimeLocation.getDescription(),
                            organisedCrimeLocation.getImage(),
                            worldToExpectedTime
                    )
            );
        });

        return viewStates;
    }
}
