package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for deserialising json data for points within the central area.
 */
public class CentralAreaPoint {
    private String name;
    private LngLat point;

    /**
     * Deserialises a json entry for a central area point and populates the class fields
     * @param name name of the point
     * @param lng longitude of point
     * @param lat latitude of point
     */
    private CentralAreaPoint(@JsonProperty("name") String name,
                             @JsonProperty("longitude") double lng,
                             @JsonProperty("latitude") double lat)
    {
        this.name = name;
        point = new LngLat(lng, lat);
    }

    /**
     * Getter method for a central area point.
     * @return point
     */
    public LngLat getLngLat() { return point; }

    /**
     * Getter method for the name of a central area point.
     * @return name
     */
    public String getName() { return name; }
}
