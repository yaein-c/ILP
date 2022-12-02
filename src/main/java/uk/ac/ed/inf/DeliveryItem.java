package uk.ac.ed.inf;

/**
 * Used to serialise delivery items into json
 */
public record DeliveryItem(String orderNo, String outcome, int costInPence) {}
