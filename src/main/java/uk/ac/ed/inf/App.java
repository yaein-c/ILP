package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URL;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        var O = new LngLat(0,0);
        var A = new LngLat(0,1);
        var B = new LngLat(-3.19,55.943);

        System.out.println("dist(O,B) = " + O.distanceTo(B));
        System.out.println("A should be false: " + A.inCentralArea());
        System.out.println("B should be true: " + B.inCentralArea());

        Restaurant[] restaurants = Restaurant.getRestaurantsFromRestServer(new URL("https://ilp-rest.azurewebsites.net/restaurants"));
        Order order = new Order();
        System.out.println("If no order yet then : " + order.getStatus());
        order.getDeliveryCost(restaurants, null);
        System.out.println("If orders null then : " + order.getStatus());
        String[] pizzas = new String[]{"Margarita"};
        order.getDeliveryCost(null, pizzas);
        System.out.println("If restaurants null then : " + order.getStatus());
        int cost = order.getDeliveryCost(restaurants, pizzas);
        System.out.println("Cost of Margarita and delivery is " + cost);

    }
}
