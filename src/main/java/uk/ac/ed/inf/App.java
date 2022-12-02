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
        //load in restaurants
        //load in orders
        //load in central area




        System.out.println( "Hello World!" );
        var O = new LngLat(0,0);
        var A = new LngLat(0,1);
        var B = new LngLat(-3.19,55.943);

        System.out.println("dist(O,B) = " + O.distanceTo(B));
        System.out.println("A should be false: " + A.inCentralArea(new URL("https://ilp-rest.azurewebsites.net")));
        System.out.println("B should be true: " + B.inCentralArea(new URL("https://ilp-rest.azurewebsites.net")));
        System.out.println(B.nextPosition(180));

        Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites.net/restaurants"));

        var date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        String formattedDate = date.format(formatter);
        System.out.println(formattedDate);

    }
}
