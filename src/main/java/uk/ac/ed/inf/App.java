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

        Flightpath flightpath = new Flightpath(new URL("https://ilp-rest.azurewebsites.net"), "2023-02-21");

        var outCA = new LngLat(-3,55);
        var outCA2 = new LngLat(-2,55);
        var dominos = new LngLat(-3.1838572025299072, 55.94449876875712);
        var civerinos = new LngLat(-3.1913,55.9455);

        var inCA = new LngLat(-3.19, 55.944);
        var inCA2 = new LngLat(-3.19,55.945);
        var AT = new LngLat(-3.1869,55.9445);

        //check if straight line from AT to civerino bumps into NFZ
        System.out.println("AT-civer has NFZ: " + flightpath.checkNoFlyZoneCollision(AT, civerinos));
        //now check where they collide
        var collision = flightpath.findNoFlyZoneCollision(AT, civerinos);
        System.out.println("collision at " + collision);
        System.out.println("distance from collision to dest: " + collision.distanceTo(civerinos));
        //find out which NoFlyZone this is
        var inNFZ = collision.nextPosition(collision.angleTo(civerinos));
        var NFZ = flightpath.findNoFlyZone(collision, inNFZ);
        System.out.println("NoFlyZone: "+NFZ.getName());
        //try to go around
        var nearBristo = new LngLat(-3.1883, 55.9452);
        var try1 = flightpath.tryClockwise(nearBristo, civerinos, NFZ);
        var try2 = flightpath.tryClockwise(try1, civerinos, NFZ);
        System.out.println("path to dest?: " + !flightpath.checkNoFlyZoneCollision(try2, civerinos, NFZ));









    }
}
