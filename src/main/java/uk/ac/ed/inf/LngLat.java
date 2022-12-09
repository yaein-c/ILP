package uk.ac.ed.inf;

/**
 * Creates a LngLat record.
 * A LngLat represents the coordinate of a location.
 * This class contains methods for calculating the geometry between LngLat points.
 * @param lng longitude of location in degrees
 * @param lat latitude latitude in degrees
 */
public record LngLat(double lng, double lat){

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
        return !(distanceTo(otherLocation) < 0.00015);
    }

    /**
     * Calculate angle to a given location and return the angle rounded to the closest compass direction.
     * @param otherLocation coordinates of another location
     * @return closest compass direction to given location
     */
    public float angleTo(LngLat otherLocation) {
        double x2 = otherLocation.lng();
        double y2 = otherLocation.lat();
        double angle = Math.toDegrees(Math.atan2(y2 - lat, x2 - lng)); //atan2 gives angle (-180,180]
        angle = (float) (angle + 360)%360; //convert back to [0,360)
        return (float) (angle - (angle%22.5)); //round down to closest compass direction
    }

    /**
     * Returns LngLat object coordinates of new location after a move in a compass direction
     * @param direction given in degrees from 0 to 359
     * @return LngLat coordinates of the new location of the drone
     */
    public LngLat nextPosition(float direction){
        double r = 0.00015;
        double x = r * Math.cos(Math.toRadians(direction));
        double y = r * Math.sin(Math.toRadians(direction));
        return new LngLat(lng + x, lat + y);
    }

    /**
     * Returns LngLat coordinates of new location after a given number of moves in a compass direction
     * @param direction given in degrees from 0 to 359
     * @param moves number of moves
     * @return LngLat coordinates of new location of drone
     */
    public LngLat nextPosition(float direction, int moves){
        LngLat currentPosition = new LngLat(lng,lat);
        for (int i = 0; i < moves; i++) {
            currentPosition = currentPosition.nextPosition(direction);
        }
        return currentPosition;
    }

    /**
     * Checks the orientation of 2 line segments AB and BC
     * @param a LngLat
     * @param b LngLat
     * @param c LngLat
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
     * @param a LngLat
     * @param b LngLat
     * @param c LngLat
     * @return true if b is in line segment ac.
     */
    public static Boolean onSegment(LngLat a, LngLat b, LngLat c) {
        return b.lng <= Math.max(a.lng, c.lng) && b.lng >= Math.min(a.lng, c.lng) &&
                b.lat <= Math.max(a.lat, c.lat) && b.lat >= Math.min(a.lat, c.lat);
    }

    /**
     * Check if the line segment ab intersects with qr.
     * @param a LngLat
     * @param b LngLat
     * @param q LngLat
     * @param r LngLat
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
        return o4 == 0 && onSegment(q, b, r);
    }
}
