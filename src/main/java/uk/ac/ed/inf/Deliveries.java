package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Used to store processed orders and possibly write them to a file
 */
public class Deliveries {
    public ArrayList<DeliveryItem> validDeliveries;
    public ArrayList<DeliveryItem> invalidDeliveries;
    public ArrayList<Order> deliveries;

    public Deliveries(Order[] orders, Restaurant[] restaurants)
    {
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
     * TODO
     * Called after all deliveries have been given an outcome
     * Writes the json file
     */
    public void write(String filename){}
}
