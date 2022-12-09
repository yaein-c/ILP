package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URL;

/**
 * Contains the main method of this programme.
 * The runtime arguments are used to call the flightpath algorithm to construct flightpaths for all orders of the day.
 * The methods to create the output files deliveries-YYYY-MM-DD, flightpaths-YYYY-MM-DD, drone-YYYY-MM-DD are called
 * after the algorithm is finished calculating.
 */
public class App
{
    /**
     * Uses the runtime arguments to initialise the Flightpath class.
     * The Flightpath class loads in all the data from the server and calculates flightpaths for all orders of the given day.
     * Flightpath then calls the methods to produce the output files.
     * @param args date in format YYYY-MM-DD and the base server URL
     * @throws IOException exception for invalid URL
     */
    public static void main( String[] args ) throws IOException {
        String date = args[0];
        String serverURL = args[1];

        Flightpath flightpath = new Flightpath(new URL(serverURL), date);
        flightpath.deliverAll(System.nanoTime());
        flightpath.writeFiles(date);
    }
}
