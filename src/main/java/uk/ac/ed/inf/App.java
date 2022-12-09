package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URL;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        String date = args[0];
        String serverURL = args[1];

        Flightpath flightpath = new Flightpath(new URL(serverURL), date);
        flightpath.deliverAll(System.nanoTime());
        flightpath.writeFiles(date);
    }
}
