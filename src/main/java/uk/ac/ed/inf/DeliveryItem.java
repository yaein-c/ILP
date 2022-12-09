package uk.ac.ed.inf;

/**
 * Used to serialise delivery items into json
 * Contains methods to assign an order outcome to Delivery Item
 */
public record DeliveryItem(String orderNo, String outcome, int costInPence) {
    /**
     * Create Delivery item with outcome "ValidButNotDelivered"
     * @param orderNo order number
     * @param costInPence cost of order in pence
     * @return Delivery item
     */
    public static DeliveryItem createInvalid(String orderNo, int costInPence) {
        return new DeliveryItem(orderNo, Order.OrderOutcome.ValidButNotDelivered.toString() , costInPence);
    }

    /**
     * Create Delivery item with outcome "Delivered"
     * @param orderNo order number
     * @param costInPence cost of order in pence
     * @return Delivery item
     */
    public static DeliveryItem createValid(String orderNo, int costInPence) {
        return new DeliveryItem(orderNo, Order.OrderOutcome.Delivered.toString(), costInPence);
    }
}
