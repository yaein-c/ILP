package uk.ac.ed.inf;

public record LngLat(double lng, double lat){

    /**
     * @return true if current location is within central area and false otherwise
     */
    public boolean inCentralArea(){
        CentralArea CA = CentralArea.getInstance();
        return (lng > CA.lng[0] && lng < CA.lng[1]) && (lat < CA.lat[0] && lat > CA.lat[1]);
    }

    /**
     * @param otherLocation coordinates of another location
     * @return distance from current location to other location
     */
    public double distanceTo(LngLat otherLocation){
        return Math.sqrt((lng-otherLocation.lng)*(lng-otherLocation.lng)+(lat-otherLocation.lat)*(lat-otherLocation.lat));
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
     * Returns LngLat object coordinates of new location after a move in a compass direction
     * @param direction given in degrees from 0 to 360
     * @return LngLat coordinates of the new location of the drone
     */
    public LngLat nextPosition(int direction){
        double r = 0.00015;
        double x = r * Math.cos(direction);
        double y = r * Math.sin(direction);
        return new LngLat(x, y);
    }
}
