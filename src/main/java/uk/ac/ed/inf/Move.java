package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

/**
 * Used to serialise flightpath moves into json
 */
public class Move {
    public final String orderNo;
    public final double fromLongitude;
    public final double fromLatitude;
    public final double angle;
    public final double toLongitude;
    public final double toLatitude;
    public long ticksSinceStartOfCalculation;

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

    public LngLat getStart() { return new LngLat(fromLongitude, fromLatitude); }
    public LngLat getEnd() { return new LngLat(toLongitude, toLatitude); }

    public Move reverse(long startTime) {
        LngLat start = getStart();
        LngLat end = getEnd();
        float reverseAngle = (float) (angle + 180)%360;
        var move = new Move(orderNo, end.lng(), end.lat(), reverseAngle,
                            start.lng(), start.lat(),
                            System.nanoTime()-startTime );
        return move;
    }

    public Point startToPoint() {
        return Point.fromLngLat(getStart().lng(), getStart().lat());
    }

    public Point endToPoint() {
        return Point.fromLngLat(getEnd().lng(), getEnd().lat());
    }
}
