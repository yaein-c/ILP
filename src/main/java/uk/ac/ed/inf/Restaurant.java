package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
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

    public LngLat getLngLat() { return new LngLat(longitude, latitude); }

    /**
     * Given a valid url to the REST server, returns array of participating restaurants with their menus.
     * @param serverBaseAddress
     * @return array of restaurants
     * @throws IOException
     * @throws MalformedURLException
     */
    public static Restaurant[] getRestaurantsFromRestServer(URL serverBaseAddress) {
        try {
            return new ObjectMapper().readValue(new URL(serverBaseAddress + "/restaurants"), Restaurant[].class );
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for restaurants");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error while processing JSON data for restaurants");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
