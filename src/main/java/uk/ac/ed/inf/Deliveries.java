package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Used to store processed orders and flightpaths.
 * Contains methods for processing orders flightpaths and writing the output files.
 */
public class Deliveries {
    public ArrayList<DeliveryItem> validDeliveries;
    public ArrayList<DeliveryItem> invalidDeliveries;
    public ArrayList<Order> deliveries;
    public ArrayList<Move> flightpath;

    /**
     * Processes orders on initialisation and populates the class fields.
     * @param orders array of orders
     * @param restaurants array of participating restaurants
     */
    public Deliveries(Order[] orders, Restaurant[] restaurants)
    {
        flightpath = new ArrayList<>();
        deliveries = new ArrayList<>();
        validDeliveries = new ArrayList<>();
        invalidDeliveries = new ArrayList<>();
        processOrders(orders, restaurants);
    }

    /**
     * Given array of orders and array of participating restaurants,
     * separate the valid and invalid orders and update the class fields
     * @param orders
     * @param restaurants
     */
    public void processOrders(Order[] orders, Restaurant[] restaurants){
        for (Order o: orders) {
            int value = o.process(restaurants);
            if (value == 1) {
                DeliveryItem d = new DeliveryItem(o.getOrderNum(), o.getStringStatus(), o.getCostInPence());
                invalidDeliveries.add(d);
            } else {
                deliveries.add(o);
            }
        }
    }

    /**
     * Given an invalid order, update the list of invalid deliveries and remove from list of pending deliveries.
     * @param order invalid order to be added to list
     */
    public void processInvalidOrder(Order order) {
        var item = DeliveryItem.createInvalid(order.getOrderNum(), order.getCostInPence());
        invalidDeliveries.add(item);
        deliveries.remove(order);
    }

    /**
     * Given an valid order, update the list of valid deliveries and remove from list of pending deliveries.
     * @param order valid order to be added to list
     */
    public void processValidOrder(Order order) {
        var item = DeliveryItem.createValid(order.getOrderNum(), order.getCostInPence());
        validDeliveries.add(item);
        deliveries.remove(order);
    }

    /**
     * Convert the calculated flightpath of the drone into a LineString object for serialising into geojson.
     * @return LineString of flightpath
     */
    public LineString toLineString() {
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(flightpath.get(0).startToPoint());
        for (Move m: flightpath) {
            points.add(m.endToPoint());
        }
        return LineString.fromLngLats(points);
    }

    /**
     * Called after all orders processed, validated and delivered
     * Writes the json file for deliveries
     * @param date orders will be processed for this date
     */
    public void writeDeliveries(String date) throws IOException {
        ArrayList<DeliveryItem> allDeliveries = new ArrayList<>();
        allDeliveries.addAll(validDeliveries);
        allDeliveries.addAll(invalidDeliveries);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get("resultFiles/deliveries-" + date + ".json" ).toFile(),allDeliveries);
        } catch (IOException e) {
            System.err.println("Error while writing " + "deliveries-" + date + ".json");
            e.printStackTrace();
        }
    }

    /**
     * Called after all orders processed, validated and delivered
     * Writes the json file for drone path
     * @param date orders will be processed for this date
     */
    public void writeDrone(String date) {
        LineString lineString = toLineString();
        Feature feature = Feature.fromGeometry(lineString);
        FeatureCollection collection = FeatureCollection.fromFeature(feature);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("resultFiles/drone-" + date + ".geojson"));
            writer.write(collection.toJson());
            writer.close();

        } catch (IOException e) {
            System.err.println("Error while writing " + "drone-" + date + ".geojson");
            e.printStackTrace();
        }
    }

    /**
     * Called after all orders processed, validated and delivered
     * Writes the json file for flightpath
     * @param date orders will be processed for this date
     */
    public void writeFlightpath(String date) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get("resultFiles/flightpath-" + date + ".json" ).toFile(),flightpath);
        } catch (IOException e) {
            System.err.println("Error while writing " + "flightpath-" + date + ".json");
            e.printStackTrace();
        }
    }
}
