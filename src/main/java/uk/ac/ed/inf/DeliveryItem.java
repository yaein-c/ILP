package uk.ac.ed.inf;

/**
 * Used to serialise delivery items into json
 */
public class DeliveryItem {
    public final String orderNo;
    public final String outcome;
    public final int costInPence;

    public DeliveryItem(String orderNo,
                        String outcome,
                        int costInPence)
    {
        this.orderNo = orderNo;
        this.outcome = outcome;
        this.costInPence = costInPence;
    }
}
