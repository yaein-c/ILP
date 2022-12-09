package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class has methods for accessing the no-fly-zones following a singleton design pattern.
 * The no-fly-zones are retrieved and deserialised into the class fields on initialisation.
 */
public class NoFlyZones {
    private static NoFlyZones instance;
    private NoFlyZone[] zones;

    /**
     * Retrieves and deserialises the no-fly-zone data from the server and populates the class fields.
     * @param serverUrl base URL of the server
     */
    private NoFlyZones(URL serverUrl){
        var objectMapper = new ObjectMapper();
        try {
            this.zones = objectMapper.readValue( new URL(serverUrl + "/noFlyZones"),
                    NoFlyZone[].class);
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for central area");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error while processing JSON data for restaurants");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Retrieves a NoFlyZones class instance following a singleton design pattern.
     * @param serverUrl base URL of server
     * @return NoFlyZones instance
     */
    public static NoFlyZones getInstance(URL serverUrl) {
        if (instance == null) {
            instance = new NoFlyZones(serverUrl);
        }
        return instance;
    }

    /**
     * Getter method for the array of NoFlyZone belonging to the class
     * @return NoFlyZone[] array
     */
    public NoFlyZone[] getZones() { return zones; }

}
