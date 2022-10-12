package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CentralAreaPoint {
    public String name;
    public LngLat point;

    private CentralAreaPoint(@JsonProperty("name") String name,
                             @JsonProperty("longitude") double lng,
                             @JsonProperty("latitude") double lat)
    {
        this.name = name;
        point = new LngLat(lng, lat);
    }
}
