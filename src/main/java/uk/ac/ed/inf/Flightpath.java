package uk.ac.ed.inf;

/**
 * This class will calculate the flightpath (and write the files)
 */
public class Flightpath {
    public Deliveries deliveries;
    public NoFlyZones noFlyZones;
    public CentralArea centralArea;

    public Flightpath(Order[] orders,
                      Restaurant[] restaurants,
                      NoFlyZones noFlyZones,
                      CentralArea centralArea)
    {
        this.noFlyZones = noFlyZones;
        this.centralArea = CentralArea.getInstance();
        this.deliveries = new Deliveries(orders, restaurants);
    }
    /**
     * TODO
     * Given a path segment, check if it will collide with a no-fly-zone
     * @return true if path collides with no-fly-zone
     */
    public Boolean checkCollision() {
        return true;
    }

    /**
     * Given a path segment
     * @return true if path collides with central area
     */
    public Boolean checkCentralAreaCollision() {
        return true;
    }

}
