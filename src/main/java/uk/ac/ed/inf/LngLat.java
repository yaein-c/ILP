package uk.ac.ed.inf;

public record LngLat(double lng, double lat){

    /**
     * @return true if current location is within central area and false otherwise
     */
    public boolean inCentralArea(){
        return false;
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
     * @param otherLocation coordinates of next location
     * @return true if current location and other location are close and false otherwise
     */
    public boolean closeTo(LngLat otherLocation){
        return false;
    }

    /**
     * Returns coordinates of new location after a move in a compass direction
     * @param direction given in degrees from 0 to 360
     * @return coordinates of the new location of the drone
     */
    public LngLat nextPosition(int direction){
        return null;
    }
}
