package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;

public class CentralArea {
    private static CentralArea instance;

    private CentralAreaPoint[] points;

    private CentralArea() {
        var objectMapper = new ObjectMapper();
        try {
            points = objectMapper.readValue( new URL("https://ilp-rest.azurewebsites.net/centralArea"),
                     CentralAreaPoint[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public static CentralArea getInstance()
    {
        if (instance == null)
        {
            instance = new CentralArea();
        }
        return instance;
    }

    /**
     * Getter method for the points that delineate the central area.
     * @return central area points
     */
    public CentralAreaPoint[] getPoints() { return points; }
}
