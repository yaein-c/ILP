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
     * Given a path segment, check if it will collide with a no-fly-zone
     * @return true if path collides with no-fly-zone
     */
    public Boolean checkNoFlyZoneCollision(LngLat a, LngLat b) {
        NoFlyZone[] zones = noFlyZones.getZones();
        for (NoFlyZone z : zones){
            int size = z.getLngLats().length;
            for (int i = 0; i < size; i++){
                LngLat q = z.getLngLats()[i];
                LngLat r = z.getLngLats()[(i+1)%size];
                if (LngLat.checkIntersect(a,b,q,r)) {
                    return true; }
            }
        }
        return false;
    }

    /**
     * Given a path segment check if it intersects or collides with central area
     * @return true if path collides with central area
     */
    public Boolean checkCentralAreaCollision(LngLat a, LngLat b) {
        //load in all ca segments and check if path segment intersects with any of them
        CentralAreaPoint[] points = centralArea.getPoints();
        for (int i = 0; i < points.length; i++) {
            LngLat q = points[i].getLngLat();
            LngLat r = points[(i+1)% points.length].getLngLat();
            if (LngLat.checkIntersect(a,b,q,r)) {
                return true; }
        }
        return false;
    }

    /**
     * Given two points, moves the drone in a straight line along that path using the minimum number of moves.
     * Returns a point close to b.
     * Assumes that the drone travels at a valid angle and has enough battery.
     * Might need to call more than once to get distance < 0.00015
     * @param a start point
     * @param b desired end point
     * @return point close to b where the drone stopped
     */
    public LngLat moveLine(LngLat a, LngLat b) {
        float angle = a.angleTo(b); //angle rounded to nearest compass direction
        double r = a.distanceTo(b);
        r = r - (r%0.00015); //round distance to min num of moves
        int moves = (int) (r/0.00015);
        return a.nextPosition(angle, moves);
    }

    /**
     * Recursively check smaller segments until the segment that collides with NoFlyZone is found
     * @param a
     * @param b
     * @return return LngLat close to NoFlyZone
     */
    public LngLat findNoFlyZoneCollision(LngLat a, LngLat b) {
        double d = a.distanceTo(b);
        System.out.println("distance: " + d/0.00015 );
        if (d <= 0.00015) {
            return a;
        }
        LngLat midpointAB = moveLine(a, new LngLat((a.lng()+b.lng())/2, (a.lat()+b.lat())/2));
        if (checkNoFlyZoneCollision(a ,midpointAB)) {
            return findNoFlyZoneCollision(a, midpointAB); }
        else {
            return findNoFlyZoneCollision(midpointAB, b); }
    }

}
