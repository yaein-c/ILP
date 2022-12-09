package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

/**
 * Used to serialise flightpath moves into json or to convert a move into a Point.
 */
public class Move {
    public final String orderNo;
    public final double fromLongitude;
    public final double fromLatitude;
    public final double angle;
    public final double toLongitude;
    public final double toLatitude;
    public long ticksSinceStartOfCalculation;

    /**
     * Default constructor
     * @param orderNo order number
     * @param fromLongitude longitude of start position before move
     * @param fromLatitude latitude of start position before move
     * @param angle angle of move
     * @param toLongitude longitude of end position after move
     * @param toLatitude latitude of end position after move
     * @param ticksSinceStartOfCalculation counter to time taken for calculations
     */
    public Move(String orderNo,
                double fromLongitude,
                double fromLatitude,
                double angle,
                double toLongitude,
                double toLatitude,
                long ticksSinceStartOfCalculation)
    {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
        this.ticksSinceStartOfCalculation = ticksSinceStartOfCalculation;
    }

    /**
     * @return Return LngLat of the start position of the move
     */
    public LngLat getStart() { return new LngLat(fromLongitude, fromLatitude); }

    /**
     * @return Return LngLat of the end position of the move
     */
    public LngLat getEnd() { return new LngLat(toLongitude, toLatitude); }

    /**
     * Reverses the direction of movement of the Move so that it starts at the end position
     * and goes back to the start position.
     * Used for reconstructing a path back after the drone makes a delivery.
     * @param startTime start time of the programme execution
     * @return reversed Move
     */
    public Move reverse(long startTime) {
        LngLat start = getStart();
        LngLat end = getEnd();
        float reverseAngle = (float) (angle + 180)%360;
        return new Move(orderNo, end.lng(), end.lat(), reverseAngle, start.lng(), start.lat(),
                        System.nanoTime()-startTime );
    }

    /**
     * Converts the start position of the Move into a geojson Point.
     * @return start position of Move as a Point
     */
    public Point startToPoint() {
        return Point.fromLngLat(getStart().lng(), getStart().lat());
    }

    /**
     * Converts the end position of the Move into a geojson Point.
     * @return end position of Move as a Point
     */
    public Point endToPoint() {
        return Point.fromLngLat(getEnd().lng(), getEnd().lat());
    }
}
