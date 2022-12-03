package uk.ac.ed.inf;

import java.net.URL;

/**
 * This class will calculate the flightpath (and write the files)
 */
public class Flightpath {
    public Deliveries deliveries;
    public NoFlyZones noFlyZones;
    public CentralArea centralArea;

    public Flightpath(URL serverURL,
                      String date)
                      //int seed)
    {
        this.noFlyZones = NoFlyZones.getInstance(serverURL);
        this.centralArea = CentralArea.getInstance(serverURL);
        this.deliveries = new Deliveries(Order.getOrdersFromRestServer(serverURL, date), Restaurant.getRestaurantsFromRestServer(serverURL));
    }
    /**
     * TODO
     * Given a path segment, check if it will collide with a no-fly-zone
     * @return true if path collides with no-fly-zone
     */
    public Boolean checkCollision() {

        return true;
    }

    /**
     * Given a path segment check if it intersects or collides with central area
     * @return true if path collides with central area
     */
    public Boolean checkCentralAreaCollision(LngLat a, LngLat b) {
        //load in all ca segments and check if path segment intersects with any of them
        CentralAreaPoint[] points = centralArea.getPoints();
        for (int i = 0; i < points.length; i++) {
            var q = points[i].getLngLat();
            var r = points[(i+1)% points.length].getLngLat();
            if (LngLat.checkIntersect(a,b,q,r)) {
                return true; }
        }
        return false;
    }

}
