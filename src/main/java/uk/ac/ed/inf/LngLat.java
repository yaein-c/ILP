package uk.ac.ed.inf;

import java.net.URL;

public record LngLat(double lng, double lat){

    /**
     * Takes central area definition from REST server and calculates if the current location is within central area.
     * @return true if current location is within central area and false otherwise
     */
    public boolean inCentralArea(URL serverURL){
        //check if point inside polygon using winding number method
        //calculate winding number by adding the angles subtended by sides of the polygon with the point
        //points are given counter-clockwise

        var centralArea = CentralArea.getInstance(serverURL);
        double angleCounter = 0; //in degrees
        CentralAreaPoint[] points = centralArea.getPoints();
        LngLat[] edges = new LngLat[points.length];

        //get centralArea LngLat points
        for (int i = 0; i < points.length; i++ ) {
            edges[i] = new LngLat(points[i].getLngLat().lng, points[i].getLngLat().lat);
        }
        int n = edges.length;
        for (int i = 0; i < n; i++) {
            //use cosine rule to get angle
            double a = edges[i].distanceTo(edges[(i+1)%n]);
            double b = distanceTo(edges[i]);
            double c = distanceTo(edges[(i+1)%n]);
            double x = (b*b + c*c - a*a)/(2*b*c);
            double angle = Math.toDegrees(Math.acos( x ) );

            //cross prod of both vectors to check if counter-clockwise(CC)
            LngLat v1 = new LngLat(edges[i].lng - lng, edges[i].lat - lat);
            LngLat v2 = new LngLat(edges[(i+1)%n].lng - lng, edges[(i+1)%n].lat - lat);
            boolean CC = v1.lng*v2.lat - v1.lat*v2.lng > 0;

            //add up positive and negative angles
            if (CC) {
                angleCounter += angle;
            } else {
                angleCounter -= angle;
            }
        }
        if (angleCounter > 359) { //angle should be 360 but choose 359 to allow for inaccuracy
            return true;
        } else {
            return false; //if point outside of central area then angleCounter is approx. equal to zero
        }
    }

    /**
     * @param otherLocation coordinates of another location
     * @return distance from current location to other location
     */
    public double distanceTo(LngLat otherLocation){
        double dx = (lng - otherLocation.lng) * (lng - otherLocation.lng);
        double dy = (lat - otherLocation.lat) * (lat - otherLocation.lat);
        return Math.sqrt(dx + dy);
    }

    /**
     * Checks whether current location is close to other location
     * @param otherLocation coordinates of another location
     * @return true if current location and other location are close and false otherwise
     */
    public boolean closeTo(LngLat otherLocation){
        return distanceTo(otherLocation) < 0.00015;
    }

    /**
     * Calculate angle to a given location and return the angle rounded to the closest compass direction.
     * @param otherLocation
     * @return closest compass direction to given location
     */
    public float angleTo(LngLat otherLocation) {
        double x1 = lng;
        double x2 = otherLocation.lng();
        double y1 = lat;
        double y2 = otherLocation.lat();
        double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)); //atan2 gives angle (-180,180]
        angle = (float) (angle + 360)%360; //convert back to [0,360)
        return (float) (angle - (angle%22.5)); //round down to closest compass direction
    }

    /**
     * Returns LngLat object coordinates of new location after a move in a compass direction
     * @param direction given in degrees from 0 to 360
     * @return LngLat coordinates of the new location of the drone
     */
    public LngLat nextPosition(float direction){
        double r = 0.00015;
        double x = r * Math.cos(Math.toRadians(direction));
        double y = r * Math.sin(Math.toRadians(direction));
        return new LngLat(lng + x, lat + y);
    }

    /**
     * Checks the orientation of 2 line segments AB and BC
     * @param a
     * @param b
     * @param c
     * @return 0 = colinear, 1 = clockwise, 2 = anticlockwise
     */
    public static int orientation(LngLat a, LngLat b, LngLat c){
        double delSlope = (b.lat - a.lat)*(c.lng - b.lng) - (b.lng - a.lng)*(c.lat - b.lat);
        delSlope = (float) (delSlope);
        if (delSlope == 0) { return 0; }
        if (delSlope > 0) { return 1; }
        return 2;
    }

    /**
     * Check if b lies on line segment AC given 3 colinear points
     * @param a
     * @param b
     * @param c
     * @return true if b is in line segment ac.
     */
    public static Boolean onSegment(LngLat a, LngLat b, LngLat c) {
        if (b.lng <= Math.max(a.lng, c.lng) && b.lng >= Math.min(a.lng, c.lng) &&
                b.lat <= Math.max(a.lat, c.lat) && b.lat >= Math.min(a.lat, c.lat)){
            return true;
        }
        return false;
    }

    /**
     * Check if the line segment ab intersects with qr.
     * @param a
     * @param b
     * @param q
     * @param r
     * @return true if the line segments intersect
     */
    public static Boolean checkIntersect(LngLat a, LngLat b, LngLat q, LngLat r) {
        int o1 = orientation(a,b,q);
        int o2 = orientation(a,b,r);
        int o3 = orientation(q,r,a);
        int o4 = orientation(q,r,b);

        if (o1 != o2 && o3 != o4) { return true; }
        if (o1 == 0 && onSegment(a,q,b)) { return true; }
        if (o2 == 0 && onSegment(a,r,q)) { return true; }
        if (o3 == 0 && onSegment(q,a,r)) { return true; }
        if (o4 == 0 && onSegment(q,b,r)) { return true; }

        return false;
    }
}
