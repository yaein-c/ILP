package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NoFlyZones {
    private static NoFlyZones instance;
    private NoFlyZone[] zones;

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
    public static NoFlyZones getInstance(URL serverUrl) {
        if (instance == null) {
            instance = new NoFlyZones(serverUrl);
        }
        return instance;
    }

    public NoFlyZone[] getZones() { return zones; }

}
