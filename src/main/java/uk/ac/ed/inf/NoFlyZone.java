package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for deserialising json data for a no-fly-zone.
 */
public class NoFlyZone {
    public final String name;
    public final double[][] coordinates;
    public final LngLat[] lngLats;

    /**
     * Deserialises a json entry for a no-fly-zone and populates the class fields.
     * @param name name of the no-fly-zone
     * @param coordinates array of the LngLats that delineate the no-fly-zone
     */
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

    /**
     * Getter method for name.
     * @return name of no-fly-zone
     */
    public String getName() { return name; }

    /**
     * Getter method for the LngLat coordinates.
     * @return array of the LngLat coordinates that delineate the no-fly-zone
     */
    public LngLat[] getLngLats() { return lngLats; }
}
