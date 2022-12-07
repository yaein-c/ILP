package uk.ac.ed.inf;

import java.net.URL;
import java.util.ArrayList;

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
        b = moveLine(a,b);
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
        b = moveLine(a,b);
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
     * TODO
     * Given path segment AB, move the drone along this path
     * @param a
     * @param b
     * @return array of Moves that the drone made
     */
    public Move[] moves(LngLat a, LngLat b) {
        return null;
    }

    /**
     * Recursively check smaller segments until the segment that collides with NoFlyZone is found.
     * If there is more than one collision, find the first collision.
     * @param a
     * @param b
     * @return return LngLat close to NoFlyZone
     */
    public LngLat findNoFlyZoneCollision(LngLat a, LngLat b) {
        b = moveLine(a,b);
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
        var position = nextPosition;
        Boolean NoClearPath = checkNoFlyZoneCollision(position, b, zone);
        Boolean collision = checkNoFlyZoneCollision(position, position.nextPosition(direction), zone);
        Boolean NoChangeDirection = checkNoFlyZoneCollision(position, position.nextPosition(position.angleTo(b)), zone);
        while (NoClearPath & !collision & NoChangeDirection ) {
            position = position.nextPosition(direction);
            NoClearPath = checkNoFlyZoneCollision(position, b, zone);
            collision = checkNoFlyZoneCollision(position, position.nextPosition(direction));
            NoChangeDirection = checkNoFlyZoneCollision(position, position.nextPosition(position.angleTo(b)), zone);
        }
        return position;
    }

    /**
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
            nextPosition = a.nextPosition(direction);
        }
        //keep moving in this direction until a collision or path towards destination found
        var position = nextPosition;
        Boolean NoClearPath = checkNoFlyZoneCollision(position, b, zone);
        Boolean collision = checkNoFlyZoneCollision(position, position.nextPosition(direction));
        Boolean NoChangeDirection = checkNoFlyZoneCollision(position, position.nextPosition(position.angleTo(b)), zone);
        while (NoClearPath & !collision & NoChangeDirection) {
            position = position.nextPosition(direction);
            NoClearPath = checkNoFlyZoneCollision(position, b, zone);
            collision = checkNoFlyZoneCollision(position, position.nextPosition(direction));
            NoChangeDirection = checkNoFlyZoneCollision(position, position.nextPosition(position.angleTo(b)), zone);
        }
        return position;

    }

    /**
     * Given point of collision and destination, find a path around the given NoFlyZone
     * @param a LngLat where the drone collides with a NoFlyZone
     * @param b LngLat of destination
     * @param zone the NoFlyZone that the drone collides with
     * @return path going around NoFlyZone
     */
    public LngLat[] moveAroundNoFlyZone(LngLat a, LngLat b, NoFlyZone zone) {
        //keep applying tryClockwise() and tryAntiClockwise() until a clear path to destination is found
        //choose the path that uses the smallest number of moves
        //return all the path segments travelled
        ArrayList<LngLat> clockwise = new ArrayList<>();
        ArrayList<LngLat> anticlockwise = new ArrayList<>();
        Boolean NoClockwisePath = checkNoFlyZoneCollision(a, b, zone);
        Boolean NoAnticlockwisePath = checkNoFlyZoneCollision(a, b, zone);
        LngLat clockwisePosition = a;
        LngLat anticlockwisePosition = a;
        clockwise.add(clockwisePosition);
        anticlockwise.add(anticlockwisePosition);
        while (NoClockwisePath) {
            clockwisePosition = tryClockwise(clockwisePosition,b,zone);
            clockwise.add(clockwisePosition);
            NoClockwisePath = checkNoFlyZoneCollision(clockwisePosition, b, zone);
        }
        while (NoAnticlockwisePath) {
            anticlockwisePosition = tryAntiClockwise(anticlockwisePosition, b, zone);
            anticlockwise.add(anticlockwisePosition);
            NoAnticlockwisePath = checkNoFlyZoneCollision(anticlockwisePosition, b, zone);
        }
        Double clockwiseDistance = clockwisePosition.distanceTo(b);
        for (int i = 1; i < clockwise.size(); i++) {
            clockwiseDistance += clockwise.get(i-1).distanceTo(clockwise.get(i));
        }
        Double anticlockwiseDistance = anticlockwisePosition.distanceTo(b);
        for (int i = 1; i < anticlockwise.size(); i++) {
            anticlockwiseDistance += anticlockwise.get(i-1).distanceTo(anticlockwise.get(i));
        }
        if (clockwiseDistance < anticlockwiseDistance) {
            LngLat[] path = new LngLat[clockwise.size()];
            return clockwise.toArray(path);
        } else {
            LngLat[] path = new LngLat[anticlockwise.size()];
            return anticlockwise.toArray(path);
        }
    }

    /**
     * Given starting location and destination fly in straight line to destination.
     * If there is a NoFlyZone, go around.
     * @param a LngLat of start location
     * @param b LngLat of destination
     * @return paths segments from a up to b, or up until a NoFlyZone is cleared
     */
    public LngLat[] move(LngLat a, LngLat b) {
        //check collisions
        if (checkNoFlyZoneCollision(a, b)) {
            ArrayList<LngLat> pathSegments = new ArrayList<>();
            pathSegments.add(a);
            LngLat collision = findNoFlyZoneCollision(a, b);//find closest collision with NoFlyZone and move there
            LngLat inNoFlyZone = collision.nextPosition(collision.angleTo(b));
            System.out.println("collision:" + collision);
            System.out.println("inNFZ:" + inNoFlyZone);
            NoFlyZone zone = findNoFlyZone(collision, inNoFlyZone);//find which NoFlyZone
            LngLat[] segs = moveAroundNoFlyZone(collision,b,zone);//move around NoFlyZone
            for (LngLat l : segs) {
                pathSegments.add(l);
            }
            LngLat[] path = new LngLat[pathSegments.size()];
            return pathSegments.toArray(path);

        // no collision
        } else {
            LngLat end = moveLine(a,b); //move in straight line to destination
            LngLat[] path = {a, end};
            return path;
        }
    }

    /**
     * Given starting location and destination, move drone to destination while avoiding NoFlyZones
     * @param a LngLat of start location
     * @param b LngLat of destination
     * @return paths segments from a to b
     */
    public LngLat[] flightpath(LngLat a, LngLat b) {
        LngLat position = a;
        ArrayList<LngLat> pathSegments = new ArrayList<>();
        pathSegments.add(a);
        while (!position.closeTo(b)) {
            LngLat[] path = move(position,b);
            position = path[path.length-1];
            for (int i = 1; i < path.length; i++) {
                pathSegments.add(path[i]);
            }
        }
        LngLat[] finalPath = new LngLat[pathSegments.size()];
        return pathSegments.toArray(finalPath);
    }
}
