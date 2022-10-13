package uk.ac.ed.inf;

public record LngLat(double lng, double lat){

    /**
     * Takes central area definition from REST server and calculates if the current location is within central area.
     * @return true if current location is within central area and false otherwise
     */
    public boolean inCentralArea(){
        //check if point inside polygon using winding number method
        //calculate winding number by adding the angles subtended by sides of the polygon with the point
        //points are given counter-clockwise

        var centralArea = CentralArea.getInstance();
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
     * Returns the angle of the given LngLat in relation to current location.
     * @param otherLocation
     * @return angle between the 2 points measured from the x-axis
     */
    public double angleTo(LngLat otherLocation) {
        double x1 = lng;
        double x2 = otherLocation.lng();
        double y1 = lat;
        double y2 = otherLocation.lat();
        double angle = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)); //atan2 gives angle (-180,180]
        return (angle + 360)%360; //convert back to [0,360)
    }

    /**
     * Returns LngLat object coordinates of new location after a move in a compass direction
     * @param direction given in degrees from 0 to 360
     * @return LngLat coordinates of the new location of the drone
     */
    public LngLat nextPosition(int direction){
        double r = 0.00015;
        double x = r * Math.cos(Math.toRadians(direction));
        double y = r * Math.sin(Math.toRadians(direction));
        return new LngLat(lng + x, lat + y);
    }
}
