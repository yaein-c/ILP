package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;

public class Restaurant {
    public final String name;
    public final double longitude;
    public final double latitude;
    public final Menu[] menu;

    private Restaurant(@JsonProperty("name") String name,
                       @JsonProperty("longitude") double longitude,
                       @JsonProperty("latitude") double latitude,
                       @JsonProperty("menu") Menu[] menu)
    {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.menu = menu;
    }

    public Menu[] getMenu(){ return menu; }

    /**
     * Given a valid url to the REST server, returns array of participating restaurants with their menus.
     * @param serverBaseAddress
     * @return array of restaurants
     * @throws IOException
     */
    public static Restaurant[] getRestaurantsFromRestServer(URL serverBaseAddress) throws IOException {
        return new ObjectMapper().readValue(serverBaseAddress, Restaurant[].class );
    }


}
