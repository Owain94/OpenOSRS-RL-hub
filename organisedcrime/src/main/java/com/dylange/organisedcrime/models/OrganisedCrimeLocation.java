package com.dylange.organisedcrime.models;

import java.util.Objects;

public class OrganisedCrimeLocation {

    private Area area;
    private String locationMessage;
    private String description;
    private String image;
    private boolean isMultiCombat;

    public OrganisedCrimeLocation(Area area, String locationMessage, String description, String image, boolean isMultiCombat) {
        this.area = area;
        this.locationMessage = locationMessage;
        this.description = description;
        this.image = image;
        this.isMultiCombat = isMultiCombat;
    }

    public Area getArea() {
        return area;
    }

    public String getLocationMessage() {
        return locationMessage;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public boolean isMultiCombat() {
        return isMultiCombat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganisedCrimeLocation that = (OrganisedCrimeLocation) o;
        return isMultiCombat == that.isMultiCombat &&
                locationMessage.equals(that.locationMessage) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationMessage, description, isMultiCombat);
    }

    public enum Area {
        Arceeus, Hosidius, Lovakengj, Piscarilius, Shayzien, Other
    }
}
