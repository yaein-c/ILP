package uk.ac.ed.inf;

/**
 * Used to serialise flightpath moves into json
 */
public class Move {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final double toLatitude;
    private int ticksSinceStartOfCalculation;

    public Move(String orderNo,
                double fromLongitude,
                double fromLatitude,
                double angle,
                double toLongitude,
                double toLatitude,
                int ticksSinceStartOfCalculation)
    {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
        this.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation;

    }
}
