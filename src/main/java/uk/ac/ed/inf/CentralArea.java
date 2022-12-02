package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CentralArea {
    private static CentralArea instance;

    private CentralAreaPoint[] points;

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

    public static CentralArea getInstance(URL serverUrl) {
        if (instance == null) {
            instance = new CentralArea(serverUrl);
        }
        return instance;
    }

    /**
     * Getter method for the points that delineate the central area.
     * @return central area points
     */
    public CentralAreaPoint[] getPoints() { return points; }
}
