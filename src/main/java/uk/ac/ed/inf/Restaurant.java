package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Used for deserialising the json data for restaurant and its menu items.
 * Contains method to retrieve restaurants data from the server.
 */
public class Restaurant {
    public final String name;
    public final double longitude;
    public final double latitude;
    public final Menu[] menu;

    /**
     * Deserialises a json entry for a restaurant and populates the class fields.
     * @param name name of restaurant
     * @param longitude longitude
     * @param latitude latitude
     * @param menu array of Menu items associated with the restaurant
     */
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

    /**
     * Getter methods for the restaurant name.
     * @return restaurant name
     */
    public Menu[] getMenu(){ return menu; }

    /**
     * Getter method for the LngLat coordinate of the restaurant.
     * @return return LngLat of restaurant
     */
    public LngLat getLngLat() { return new LngLat(longitude, latitude); }

    /**
     * Given a valid url to the REST server, returns array of participating restaurants with their menus.
     * @param serverBaseAddress
     * @return array of restaurants
     * @throws IOException IO exception from invalid URL
     * @throws MalformedURLException exception from invalid URL
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
