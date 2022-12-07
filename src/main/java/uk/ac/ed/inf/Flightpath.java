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

    //
    //
    //TODO could try to refactor all the NoFlyZone methods into NoFlyZones class...
    //
    //
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
     * Given a path segment AB and a NoFlyZone, check if the segment collides with the NoFlyZone
     * @param a start LngLat
     * @param b end LngLat
     * @param zone NoFlyZone
     * @return true if the path segment collides with the given NoFlyZone
     */
    public Boolean checkNoFlyZoneCollision(LngLat a, LngLat b, NoFlyZone zone) {
        int size = zone.getLngLats().length;
        for (int i = 0; i < size; i++) {
            LngLat q = zone.getLngLats()[i];
            LngLat r = zone.getLngLats()[(i + 1) % size];
            if (LngLat.checkIntersect(a, b, q, r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a path segment, find which NoFlyZone it collides with.
     * Choose a small segment to avoid more collision with more than one NoFlyZone.
     * @param a LngLat of segment start
     * @param b LngLat of segment end
     * @return the NoFlyZone that the path segment collides with
     */
    public NoFlyZone findNoFlyZone(LngLat a, LngLat b) {
        NoFlyZone[] zones = noFlyZones.getZones();
        for (NoFlyZone z : zones){
            int size = z.getLngLats().length;
            for (int i = 0; i < size; i++){
                LngLat q = z.getLngLats()[i];
                LngLat r = z.getLngLats()[(i+1)%size];
                if (LngLat.checkIntersect(a,b,q,r)) { return z; }
            }
        }
        return null;
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
     * Recursively check smaller segments until the segment that collides with NoFlyZone is found.
     * If there is more than one collision, find the first collision.
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

    /**
     * TODO provisionally working, do more testing and change anticlock as well
     * Change the angle until the drone can move at least once without colliding with the NoFlyZone
     * @param a current location
     * @param b desired final destination of drone
     * @return LngLat of the next position of the drone
     */
    public LngLat tryClockwise(LngLat a, LngLat b, NoFlyZone zone) {
        float direction = a.angleTo(b);
        LngLat nextPosition = a.nextPosition(direction);
        while ( checkNoFlyZoneCollision(a, nextPosition, zone)) {
            direction = (float) ((direction + 22.5)%360);
            nextPosition = a.nextPosition(direction);
        }
        //keep moving in this direction until a collision or path towards destination found
        System.out.println("angle: " + direction);
        var position = nextPosition;
        System.out.println("move");
        Boolean NoClearPath = checkNoFlyZoneCollision(position, b, zone);
        Boolean collision = checkNoFlyZoneCollision(position, position.nextPosition(direction));
        while (NoClearPath & !collision) {
            position = position.nextPosition(direction);
            System.out.println("move");
            NoClearPath = checkNoFlyZoneCollision(position, b, zone);
            collision = checkNoFlyZoneCollision(position, position.nextPosition(direction));
        }
        return position;
    }

    /**
     * TODO debug
     * Change the angle until the drone can move at least once without colliding with the NoFlyZone
     * @param a current location
     * @param b desired final destination of drone
     * @return LngLat of the next position of the drone
     */
    public LngLat tryAntiClockwise(LngLat a, LngLat b, NoFlyZone zone) {
        float direction = a.angleTo(b);
        LngLat nextPosition = a.nextPosition(direction);
        while ( checkNoFlyZoneCollision(a, nextPosition, zone)) {
            direction = (float) ((direction - 22.5 + 360)%360);
            System.out.println("angle is" + direction);
            nextPosition = a.nextPosition(direction);
        }
        return nextPosition;
    }

//        System.out.println("direction found");
//        //direction found
//        //keep moving in this direction until a collision or path towards destination found
//        LngLat position = nextPosition;
//        nextPosition = position.nextPosition(direction);
//        Boolean collision = checkNoFlyZoneCollision(position, nextPosition, zone);
//        Boolean clearPath = checkNoFlyZoneCollision(position, position.nextPosition(position.angleTo(b)));
//        while ( !collision || !clearPath ){
//            System.out.println("distance from dest:" + position.distanceTo(b));
//            System.out.println("move once");
//            position = nextPosition;
//            nextPosition = position.nextPosition(direction);
//            collision = checkNoFlyZoneCollision(position, nextPosition, zone);
//            clearPath = checkNoFlyZoneCollision(position, position.nextPosition(position.angleTo(b)));
//        }
//        return position;



}
