package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NoFlyZone {
    public final String name;
    public final double[][] coordinates;
    public final LngLat[] lngLats;

    public NoFlyZone(@JsonProperty("name") String name,
                     @JsonProperty("coordinates") double[][] coordinates)
    {
        this.name = name;
        this.coordinates = coordinates;
        lngLats = new LngLat[coordinates.length];
        for (int i = 0; i < lngLats.length; i++) {
            double x = coordinates[i][0];
            double y = coordinates[i][1];
            lngLats[i] = new LngLat(x,y);
        }
    }

    public String getName() { return name; }
    public LngLat[] getLngLats() { return lngLats; }
}
