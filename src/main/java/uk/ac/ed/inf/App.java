package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        //TODO
        //load in restaurants
        //load in orders
        //load in central area
        //assign runtime args

        Flightpath flightpaths = new Flightpath(new URL("https://ilp-rest.azurewebsites.net"), "2023-02-21");

        var outCA = new LngLat(-3,55);
        var outCA2 = new LngLat(-2,55);
        var inCA = new LngLat(-3.19, 55.944);
        var inCA2 = new LngLat(-3.19,55.945);
        System.out.println(flightpaths.checkCentralAreaCollision(inCA, inCA2));

    }
}
