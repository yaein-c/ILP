package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Orders from json are deserialised into this class.
 * Order class has field to check the status of an order
 * as well as a method to calculate total cost of items inside an order including delivery cost.
 */
public class Order {
    public enum OrderOutcome {
        Delivered,
        ValidButNotDelivered,
        InvalidCardNumber,
        InvalidExpiryDate,
        InvalidCvv,
        InvalidTotal,//priceTotalInPence from json could be incorrect
        InvalidPizzaNotDefined,
        InvalidPizzaCount,
        InvalidPizzaCombinationMultipleSuppliers,
        Invalid
    }

    private final String orderNum;
    private final String orderDate;
    private final String customer;
    private final String cardNum;
    private final String cardExpiry;
    private final String cardCvv;
    private final int total;
    private final String[] orderItems;
    private OrderOutcome status;

    private Order(@JsonProperty("orderNo") String orderNum,
                  @JsonProperty("orderDate") String orderDate,
                  @JsonProperty("customer") String customer,
                  @JsonProperty("creditCardNumber") String cardNum,
                  @JsonProperty("creditCardExpiry") String cardExpiry,
                  @JsonProperty("cvv") String cardCvv,
                  @JsonProperty("priceTotalInPence") int total,
                  @JsonProperty("orderItems") String[] orderItems, OrderOutcome status) {
        this.orderNum = orderNum;
        this.orderDate = orderDate;
        this.customer = customer;
        this.cardNum = cardNum;
        this.cardExpiry = cardExpiry;
        this.cardCvv = cardCvv;
        this.total = total;
        this.orderItems = orderItems;
    }

    // GETTERS AND SETTERS

    public OrderOutcome getStatus() {
        return status;
    }
    public void setStatus(OrderOutcome status) {
        this.status = status;
    }

    public String getOrderNum() { return orderNum; }

    public String getStringStatus() { return status.toString(); }

    public int getCostInPence() { return total; }

    // OTHER METHODS

    /**
     * Retrieves the orders for a given day from the server
     *
     * @param serverAddress
     * @return array of orders for a given date
     */
    public static Order[] getOrdersFromRestServer(URL serverAddress, String date) {
        try {
            return new ObjectMapper().readValue( new URL(serverAddress + "/orders/" + date), Order[].class);
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for orders");
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error while processing JSON data for orders");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Finds and returns the restaurant associated with the first order.
     *
     * @param restaurants
     * @param orders
     * @return restaurant associated with first order
     */
    public Restaurant getCurrentRestaurant(Restaurant[] restaurants, String[] orders) {
        Restaurant currentRestaurant = null;

        mainLoop:
        for (Restaurant r : restaurants) {
            for (Menu item : r.getMenu()) {
                if (item.getName().equalsIgnoreCase(orders[0])) {
                    currentRestaurant = r;
                    break mainLoop;
                }
            }
        }
        return currentRestaurant;
    }

    /**
     * Check if pizza combination can be delivered by the same restaurant
     * @param restaurants
     * @param orders
     * @return true if pizza combo cannot be delivered by the same restaurant
     */
    public Boolean checkPizzaCombination(Restaurant[] restaurants, String[] orders) {
        var currentRestaurant = getCurrentRestaurant(restaurants, orders);
        //check if all orders come from same restaurant
        int counter = 0;
        for (String order : orders) {
            for (Menu item : currentRestaurant.getMenu()) {
                if (item.getName().equalsIgnoreCase(order)) {
                    counter += 1;
                    break;
                }
            }
        }
        return counter != orders.length;
    }

    /**
     * Checks if all pizzas are defined regardless of which restaurant the pizza is served by.
     *
     * @param restaurants
     * @param orders
     * @return true if one or more pizzas undefined
     */
    public Boolean checkPizzaDefined(Restaurant[] restaurants, String[] orders) {

        //loop through all menu items and check if order matches
        int validPizzaCount = 0;
        for (String order : orders) {
            Loop:
            for (Restaurant r : restaurants) {
                for (Menu item : r.getMenu()) {
                    if (item.getName().equalsIgnoreCase(order)) {
                        validPizzaCount += 1;
                        break Loop;
                    }
                }
            }
        }
        return validPizzaCount != orders.length;
    }

    /**
     * @return true if card number invalid
     */
    public Boolean checkCardNumber() {
        //check if 13-19 digits
        return cardNum.length() <= 13 || cardNum.length() >= 19;
    }

    /**
     * @return true if card expired
     */
    public Boolean checkExpiryDate() {
        //convert to date and check that card still valid
        var formatter = DateTimeFormatter.ofPattern("MM/yy");
        var expiry = LocalDate.from(formatter.parse(cardExpiry));
        return LocalDate.now().isAfter(expiry);
    }

    /**
     * @return true if cvv invalid
     */
    public Boolean checkCvv() {
        return cardCvv.length() != 3;
    }

    /**
     * Calculates total cost of order plus 100p delivery charge.
     * @param restaurants array of the participating restaurants in the order
     * @param orders array of names of each individual pizza ordered
     * @return total cost plus delivery in pence
     */
    public int getDeliveryCost(Restaurant[] restaurants, String[] orders) {
        int totalCost = 100; //cost in pence
        var currentRestaurant = getCurrentRestaurant(restaurants, orders);
        for (String order : orders) {
            for (Menu item : currentRestaurant.getMenu()) {
                if (item.getName().equalsIgnoreCase(order)) {
                    totalCost += item.getPriceInPence();
                    break;
                }
            }
        }
        return totalCost;
    }

    /**
     * validates an order and assigns an appropriate order outcome.
     * @param restaurants array of the participating restaurants
     * @return 0 if the order is valid, else returns 1
     */
    public int process(Restaurant[] restaurants) {
        if (checkCardNumber()) {
            setStatus(OrderOutcome.InvalidCardNumber);
            return 1;
        } else if (checkCvv()) {
            setStatus(OrderOutcome.InvalidCvv);
            return 1;
        } else if (checkExpiryDate()) {
            setStatus(OrderOutcome.InvalidExpiryDate);
            return 1;
        } else if (restaurants == null || restaurants.length < 1) {
            setStatus(OrderOutcome.Invalid);
            return 1;
        } else if ((orderItems == null || orderItems.length < 1) || (orderItems.length > 4)) {
            setStatus(OrderOutcome.InvalidPizzaCount);
            return 1;
        } else if (checkPizzaCombination(restaurants, orderItems)) {
            setStatus(OrderOutcome.InvalidPizzaCombinationMultipleSuppliers);
            return 1;
        } else if (checkPizzaDefined(restaurants, orderItems)) {
            setStatus(OrderOutcome.InvalidPizzaNotDefined);
            return 1;
        } else if (getDeliveryCost(restaurants, orderItems) != total) {
            setStatus(OrderOutcome.InvalidTotal);
            return 1;
        }
        return 0;
    }
}

