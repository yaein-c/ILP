package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RestaurantClient {
    public static void main(String [] args ) {
            if ( args.length != 0 ) {
                System.err.println ( "Client Base−URL Echo−Parameter" ) ;
                System.err.println ( "you must supply the base address of the ILP REST Service \n" +
                        " e.g. http://restservice.somewhere and a string to be echoed" ) ;
                System.exit( 1 ) ;
            }
            try {
                /**
                 * the Jackson JSON library provides helper methods which can directly
                 * take a URL, perform the GET request convert the result to the specified class
                 **/

                Restaurant restaurant[] = new ObjectMapper().readValue(
                        new URL("https://ilp-rest.azurewebsites.net/restaurants" ) , Restaurant[].class) ;

                /**
                 * some error checking − only needed for the sample ( if the JSON data is
                 * not correct usually an exception i s thrown )
                 **/
                System.out.println( "Something happened :") ;
                for (Restaurant r : restaurant) {
                    System.out.println(r);
                }
            } catch ( MalformedURLException e ) {
                e.printStackTrace ( ) ;
            } catch ( IOException e ) {
                e.printStackTrace ( ) ;
            }
        }
    }

