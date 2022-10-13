package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CentralAreaPoint {
    private String name;
    private LngLat point;

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
