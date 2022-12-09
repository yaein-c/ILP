package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Loads in all the data necessary to calculate flightpaths.
 * Flightpaths are calculated for each restaurant and indexed.
 * Will try to deliver all valid orders in the order that they appear in the array.
 * If a flightpath to a restaurant was not found, the order outcome will be "ValidButNotDelivered"
 *
 */
public class Flightpath {

    public LngLat AT = new LngLat(-3.186874, 55.944494);
    public Deliveries deliveries;
    public NoFlyZones noFlyZones;
    public CentralArea centralArea;

    public Restaurant[] restaurants;

    public int battery;

    public HashMap<String,LngLat[]> flightpaths;

    /**
     * Constructor initialises the class and loads in all the data from the server to populate the class fields
     * @param serverURL
     * @param date
     */
    public Flightpath(URL serverURL,
                      String date)
    {
        this.noFlyZones = NoFlyZones.getInstance(serverURL);
        this.centralArea = CentralArea.getInstance(serverURL);
        this.deliveries = new Deliveries(Order.getOrdersFromRestServer(serverURL, date), Restaurant.getRestaurantsFromRestServer(serverURL));
        this.restaurants = Restaurant.getRestaurantsFromRestServer(serverURL);
        this.battery = 2000;
        this.flightpaths = getFlightpaths();
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
     * Checks path segments until the segment that collides with NoFlyZone is found.
     * If there is more than one collision, find the first collision.
     * @param a LngLat of start
     * @param b LngLat of destination
     * @return return LngLat close to NoFlyZone
     */
    public LngLat findNoFlyZoneCollision(LngLat a, LngLat b) {
        LngLat end = moveLine(a,b);
        float direction = a.angleTo(end);
        LngLat position = a;
        LngLat nextPosition = position.nextPosition(direction);
        while (!checkNoFlyZoneCollision(position, nextPosition)) {
            position = nextPosition;
            nextPosition = position.nextPosition(direction);
        }
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
        int runCounter = 0;
        while (checkNoFlyZoneCollision(position, b, zone)) {
            runCounter += 1;
            if (position == null || runCounter > 100) {
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
            return moveAroundNoFlyZone(a,b,zone);
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
            return moveAround(a,b);
        // no collision
        } else {
            LngLat end = moveLine(a,b); //move in straight line to destination
            return new LngLat[]{end};
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
        while (position.closeTo(b)) {
            LngLat[] path = move(position,b);
            if (path == null) {
                return null;
            }
            position = path[path.length-1];
            Collections.addAll(pathSegments, path);
        }
        LngLat[] finalPath = new LngLat[pathSegments.size()];
        return pathSegments.toArray(finalPath);
    }

    /**
     * Find flightpaths for all participating restaurants.
     * Flightpath is null if no flightpath found.
     */
    public HashMap<String, LngLat[]> getFlightpaths() {
        HashMap<String,LngLat[]> paths = new HashMap<>();
        for (Restaurant r: restaurants) {
            LngLat[] path = flightpath(AT, r.getLngLat());
            paths.put(r.name, path);
        }
        return paths;
    }

    /**
     * Given path to destination, check if it is a valid flightpath.
     * @param a LngLat of start
     * @param b LngLat of destination
     * @param path path from start to destination
     * @return true if valid, else false
     */
    public boolean validFlightPath(LngLat a, LngLat b, LngLat[] path) {
        int length = path.length;
        if (!(path[0].lng() == a.lng() & path[0].lat() == a.lat())) { //check drone starts at start
            return false;
        } else if (path[length - 1].closeTo(b)) { //check that drone stops close to destination
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
     * Given path segment AB, move the drone along this path
     * @param a LngLat of segment start
     * @param b LngLat of segment end
     * @return array of Moves that the drone made
     */
    public Move[] moves(String orderNum, LngLat a, LngLat b, long startTime) {
        ArrayList<Move> moves = new ArrayList<>();
        LngLat position = a;
        LngLat nextPosition;
        float angle;
        while (position.closeTo(b)) {
            angle = position.angleTo(b);
            nextPosition = position.nextPosition(angle);
            Move move = new Move(orderNum, position.lng(), position.lat(),
                                 angle, nextPosition.lng(), nextPosition.lat(),
                                 System.nanoTime()-startTime);
            moves.add(move);
            position = nextPosition;
        }
        Move[] totalMoves = new Move[moves.size()];
        return moves.toArray(totalMoves);
    }

    /**
     * Given an order and a flightpath, calculate all the moves for that flightpath.
     * @param orderNum order number
     * @param flightpaths array of the LngLats that make up the flightpath
     * @param destination LngLat of destination
     * @param startTime start time of programme execution
     * @return ArrayList of the moves along flightpath
     */
    public ArrayList<Move> flightpathMoves(String orderNum, LngLat[] flightpaths, LngLat destination, long startTime) {
        //given flightpath, move drone along entire path
        ArrayList<Move> moves = new ArrayList<>();
        LngLat start = flightpaths[0];
        for (int i = 1; i < flightpaths.length; i++) {
            Move[] segMoves = moves(orderNum, start, flightpaths[i], startTime);
            start = flightpaths[i];
            Collections.addAll(moves, segMoves);
        }
        LngLat currentLocation = moves.get(moves.size()-1).getEnd();
        LngLat nextPosition;
        while (currentLocation.closeTo(destination)) {
            float angle = currentLocation.angleTo(destination);
            nextPosition = currentLocation.nextPosition(currentLocation.angleTo(destination));
            Move move = new Move(orderNum,currentLocation.lng(), currentLocation.lat(),
                                 angle, nextPosition.lng(), nextPosition.lat(),
                                 System.nanoTime()-startTime);
            moves.add(move);
            currentLocation = nextPosition;
        }
        return moves;
    }

    /**
     * Deliver a given order.
     * Finds the flightpath for the given order and calculates all the moves for that flightpath
     * then reverse the flightpath to return to AT.
     * Hovers for one move at restaurant and hovers for one move
     * after return to AT.
     * @param order item of class Order
     * @param startTime the start time of the programme execution
     * @return ArrayList of all the moves made while delivering order
     */
    public ArrayList<Move> deliveryMoves(Order order, long startTime) {
        Restaurant restaurant = Order.getCurrentRestaurant(restaurants, order.getOrderItems());
        LngLat[] forwardPath = flightpaths.get(restaurant.name);
        ArrayList<Move> deliveryMoves;
        String orderNum = order.getOrderNum();
        //go from AT to restaurant
        deliveryMoves = flightpathMoves(orderNum, forwardPath, restaurant.getLngLat(), startTime);
        //hover for one move
        int size = deliveryMoves.size();
        LngLat currentPosition = deliveryMoves.get(size-1).getEnd();
        Move restaurantHover = new Move(orderNum, currentPosition.lng(), currentPosition.lat(), -1,
                                        currentPosition.lng(), currentPosition.lat(),
                                        System.nanoTime()-startTime);

        //go from restaurant to AT
        ArrayList<Move> backwards = new ArrayList<>(deliveryMoves.size());
        for (int i = deliveryMoves.size()-1; i >= 0; i--) {
            Move reverse = deliveryMoves.get(i).reverse(startTime);
            backwards.add(reverse);
        }
        deliveryMoves.add(restaurantHover);
        //hover for one move
        currentPosition = backwards.get(size-1).getEnd();
        Move ATHover = new Move(orderNum, currentPosition.lng(), currentPosition.lat(), -1,
                currentPosition.lng(), currentPosition.lat(),
                System.nanoTime()-startTime);
        //finished
        deliveryMoves.addAll(backwards);
        deliveryMoves.add(ATHover);
        return deliveryMoves;
    }

    /**
     * Go through all valid orders and try to deliver them until the battery runs out.
     * @param startTime the start time of the programme execution
     */
    public void deliverAll(long startTime) {
        ArrayList<Order> invalid = new ArrayList<>();
        ArrayList<Order> valid = new ArrayList<>();
        for (Order o: deliveries.deliveries) {
            //check if flightpath null
            Restaurant restaurant = Order.getCurrentRestaurant(restaurants, o.getOrderItems());
            if (flightpaths.get(restaurant.name) == null) {
                invalid.add(o);
            } else {
                ArrayList<Move> delivery = deliveryMoves(o, startTime);
                //check battery
                if (battery - delivery.size() < 0) {
                    invalid.add(o);
                } else {
                    //add to flightpath
                    valid.add(o);
                    deliveries.flightpath.addAll(delivery);
                    battery = battery - delivery.size();
                }
            }
        }
        //process orders
        for (Order o: invalid) {
            deliveries.processInvalidOrder(o);
        }
        for (Order o: valid) {
            deliveries.processValidOrder(o);
        }
        System.out.println(":)");
    }

    /**
     * @param date the date that the files will be written for
     * @throws IOException exception while writing files
     */
    public void writeFiles(String date) throws IOException {
        deliveries.writeDeliveries(date);
        deliveries.writeFlightpath(date);
        deliveries.writeDrone(date);
    }
}
