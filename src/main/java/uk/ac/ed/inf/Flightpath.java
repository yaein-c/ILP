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

    public int battery;

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
        b = moveLine(a,b);
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
     * Check segments until the segment that collides with NoFlyZone is found.
     * If there is more than one collision, find the first collision.
     * @param a
     * @param b
     * @return return LngLat close to NoFlyZone
     */
    public LngLat findNoFlyZoneCollision(LngLat a, LngLat b) {
        LngLat end = moveLine(a,b);
        Float direction = a.angleTo(end);
        LngLat position = a;
        LngLat nextPosition = position.nextPosition(direction);
        int moves = 0;
        while (!checkNoFlyZoneCollision(position, nextPosition)) {
            position = nextPosition;
            nextPosition = position.nextPosition(direction);
            moves += 1;
        }
        System.out.println("collision " + moves + " moves away");
        return position;
    }

    /**
     * Check if changing the angle will avoid collision with a given NoFlyZone
     * @param a start
     * @param b destination
     * @param zone NoFlyZone
     * @return path to destination that is collision-free, else null
     */
    public LngLat tryAngles(LngLat a, LngLat b, NoFlyZone zone) {
        //find path
        float angle = a.angleTo(b);
        LngLat nextPosition;
        ArrayList<LngLat> paths = new ArrayList<>();
        //positive directions
        for (int i = 1; i < 15; i++) {
            angle = (float) (angle + (i*22.5))%360;
            nextPosition = a.nextPosition(angle);
            if (!checkNoFlyZoneCollision(a, nextPosition,zone)) {
                paths.add(nextPosition);
            }
        }
        //negative directions
        for (int i = 1; i < 15; i++) {
            angle = (float) (angle + (i * -22.5) + 360) % 360;
            nextPosition = a.nextPosition(angle);
            if (!checkNoFlyZoneCollision(a, nextPosition,zone)) {
                paths.add(nextPosition);
            }
        }
        if (paths.size() < 1) {
            return null;
        } else {
            //if there is more than one path remaining pick shortest distance to destination
            var shortest = paths.get(0);
            for (LngLat l : paths) {
                if (l.distanceTo(b) < shortest.distanceTo(b)) {
                    shortest = l;
                }
            }
            return shortest;
        }
    }

    /**
     * Given a start point and destination, try to find a path around the NoFlyZone
     * @param a start
     * @param b destination
     * @param zone NoFlyZone
     * @return path around NoFlyZone
     */
    public LngLat[] moveAroundNoFlyZone(LngLat a, LngLat b, NoFlyZone zone) {
        //keep applying tryAngles() until NoFlyZone is cleared
        ArrayList<LngLat> path = new ArrayList<>();
        LngLat position = tryAngles(a,b,zone);
        path.add(position);
        while (checkNoFlyZoneCollision(position, b, zone)) {
            if (position == null) {
                return null;
            }
            position = tryAngles(position, b, zone);
            path.add(position);
        }
        LngLat[] pathSegs = new LngLat[path.size()];
        return path.toArray(pathSegs);
    }

    /**
     * Given a start and destination, find a collision and NoFlyZone
     * and call moveAroundNoFlyZone()
     * @param a start
     * @param b destination
     * @return the path around a NoFlyZone
     */
    public LngLat[] moveAround(LngLat a, LngLat b) {
        //collision detected
        //find collision
        var zone = findNoFlyZone(a,b);
        var collision = findNoFlyZoneCollision(a,b);
        if (collision == a) {
            return null;
        } else {
            LngLat[] path = moveAroundNoFlyZone(a,b,zone);
            return path;
        }
    }

    /**
     * Given starting location and destination fly in straight line to destination.
     * If there is a NoFlyZone, go around.
     * @param a LngLat of start location
     * @param b LngLat of destination
     * @return paths segments from a + move, up to b, or up until a NoFlyZone is cleared
     */
    public LngLat[] move(LngLat a, LngLat b) {
        //check collisions
        if (checkNoFlyZoneCollision(a, b)) {
            LngLat[] path = moveAround(a,b);
            if (path == null) {
                return null;
            } else {
                return path;
            }
        // no collision
        } else {
            LngLat end = moveLine(a,b); //move in straight line to destination
            LngLat[] path = {end};
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
            if (path == null) {
                return null;
            }
            position = path[path.length-1];
            for (int i = 0; i < path.length; i++) {
                pathSegments.add(path[i]);
            }
        }
        LngLat[] finalPath = new LngLat[pathSegments.size()];
        return pathSegments.toArray(finalPath);
    }

    /**
     * For all restaurants, find a valid flightpath and save into class field
     * call deliver() to deliver an order
     * field
     */
    public flightpaths() {

    }

    /**
     * Given path to destination, check if it is a valid flightpath.
     * @param a LngLat of start
     * @param b LngLat of destination
     * @param path
     * @return true if valid, else false
     */
    public boolean validFlightPath(LngLat a, LngLat b, LngLat[] path) {
        int length = path.length;
        if (path == null) {
            return false;
        }
        if (!(path[0].lng() == a.lng() & path[0].lat() == a.lat())) { //check drone starts at start
            return false;
        } else if (!path[length-1].closeTo(b)) { //check that drone stops close to destination
            return false;
        }
        int centralAreaCollision = 0;
        for (int i = 0; i < length-1; i++) { //check that path doesn't collide with NoFlyZones
            // and check that entire path only intersects central area at most once
            if (checkNoFlyZoneCollision(path[i], path[i+1])) {
                return false;
            }
            if (checkCentralAreaCollision(path[i], path[i+1])) {
                centralAreaCollision += 1;
                if (centralAreaCollision > 1) {
                    return false;
                }
            }
        }
        return true;
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
     * TODO
     *
     */
    public void deliver() {
    }

    /**
     * TODO
     */
    public void deliverAll() {}
}
