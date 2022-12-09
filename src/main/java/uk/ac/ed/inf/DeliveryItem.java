package uk.ac.ed.inf;

/**
 * Used to serialise delivery items into json
 */
public record DeliveryItem(String orderNo, String outcome, int costInPence) {
    public static DeliveryItem createInvalid(String orderNo, int costInPence) {
        return new DeliveryItem(orderNo, Order.OrderOutcome.ValidButNotDelivered.toString() , costInPence);
    }

    public static DeliveryItem createValid(String orderNo, int costInPence) {
        return new DeliveryItem(orderNo, Order.OrderOutcome.Delivered.toString(), costInPence);
    }
}
