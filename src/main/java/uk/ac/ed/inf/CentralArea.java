package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Loads in the central area definition from the server on initialisation using the constructor.
 * Contains methods for accessing the central area coordinates and for getting a CentralArea instance.
 * This class follows a singleton design pattern for getting or accessing the class instance.
 */
public class CentralArea {
    private static CentralArea instance;

    private CentralAreaPoint[] points;

    /**
     * Constructor for loading in the central area data from the server on initialisation.
     * @param serverUrl URL of the server
     */
    private CentralArea(URL serverUrl) {
        var objectMapper = new ObjectMapper();
        try {
            this.points = objectMapper.readValue( new URL(serverUrl + "/centralArea"),
                     CentralAreaPoint[].class);
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
     * Retrieves a CentralArea class instance following a singleton design pattern.
     * @param serverUrl URL of the server
     * @return instance of CentralArea class
     */
    public static CentralArea getInstance(URL serverUrl) {
        if (instance == null) {
            instance = new CentralArea(serverUrl);
        }
        return instance;
    }

    /**
     * Getter method for the points that delineate the central area.
     * @return array of CentralAreaPoint
     */
    public CentralAreaPoint[] getPoints() { return points; }
}
