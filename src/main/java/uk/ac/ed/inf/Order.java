package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Order class has field to check the status of an order
 * as well as a method to calculate total cost of items inside an order including delivery cost
 */
public class Order {
    public enum OrderOutcome {
        Delivered ,
        ValidButNotDelivered ,
        InvalidCardNumber ,
        InvalidExpiryDate ,
        InvalidCvv ,
        InvalidTotal ,//priceTotalInPence from json could be incorrect
        InvalidPizzaNotDefined ,
        InvalidPizzaCount ,
        InvalidPizzaCombinationMultipleSuppliers ,
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

    private Order(@JsonProperty("orderNo") String orderNum,
                  @JsonProperty("orderDate") String orderDate,
                  @JsonProperty("customer") String customer,
                  @JsonProperty("creditCardNumber") String cardNum,
                  @JsonProperty("creditCardExpiry") String cardExpiry,
                  @JsonProperty("cvv") String cardCvv,
                  @JsonProperty("priceTotalInPence") int total,
                  @JsonProperty("orderItems") String[] orderItems)
    {
        this.orderNum = orderNum;
        this.orderDate = orderDate;
        this.customer = customer;
        this.cardNum = cardNum;
        this.cardExpiry = cardExpiry;
        this.cardCvv = cardCvv;
        this.total = total;
        this.orderItems = orderItems;
    }
    private OrderOutcome status;

    /**
     * Getter method for status of OrderOutcome.
     * @return status
     */
    public OrderOutcome getStatus() { return status; }

    /**
     * Setter method for status of OrderOutcome.
     * @param status
     */
    public void setStatus(OrderOutcome status) { this.status = status; }

    /**
     * Retrieves the orders for a given day from the server
     * @param serverAddress
     * @return array of orders for a given date
     */
    public static Order[] getOrdersFromRestServer(URL serverAddress) {
        try {
            return new ObjectMapper().readValue(serverAddress, Order[].class);
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
     * Throws exception if the ordered pizza combination cannot be delivered by the same restaurant.
     * @param restaurants
     * @param orders
     * @throws InvalidPizzaCombinationException
     */
    public void checkPizzaCombination(Restaurant[] restaurants, String[] orders) throws InvalidPizzaCombinationException {
         var currentRestaurant = getCurrentRestaurant(restaurants, orders);

         //check if all orders come from same restaurant
         int counter = 0;
         for (String order : orders) {
             for (Menu item : currentRestaurant.getMenu()) {
                 if (item.getName().equalsIgnoreCase(order) ) {
                     counter += 1;
                     break;
                 }
             }
         }
         Boolean invalidPizzaCombo = counter != orders.length;

        if (invalidPizzaCombo) {
            setStatus(OrderOutcome.InvalidPizzaCombinationMultipleSuppliers);
            throw new InvalidPizzaCombinationException("Combination of pizzas cannot be delivered by the same restaurant");
        }
    }

    /**
     * Checks if all pizzas are defined regardless of which restaurant the pizza is served by.
     * @param restaurants
     * @param orders
     * @return true if one or more pizzas undefined
     */
    public Boolean checkPizzaDefined(Restaurant[] restaurants, String[] orders){

         //loop through all menu items and check if order matches
         int validPizzaCount = 0;
         for (String order: orders) {
             Loop:
             for (Restaurant r: restaurants) {
                 for (Menu item : r.getMenu() ) {
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
        return cardCvv.length()!=3;
    }

    /**
     * TODO
     * @return true if priceTotalInPence does not match delivery cost
     */
    public Boolean checkTotal(Restaurant[] restaurants) {
        int cost = getDeliveryCost(restaurants, orderItems);
        return cost != total;
    }
    /**
     * TODO make this method work with updated app design
     * Checks if order is valid and calculates total cost of order plus delivery charge.
     * @param restaurants array of the participating restaurants in the order
     * @param orders array of names of each individual pizza ordered
     * @return total cost plus delivery in pence
     */
    public int getDeliveryCost(Restaurant[] restaurants, String[] orders){
            if (restaurants == null || restaurants.length < 1) {
                setStatus(OrderOutcome.Invalid);
                return 1;
            } else if ( (orders == null || orders.length < 1) || (orders.length > 4) ) {
                setStatus(OrderOutcome.InvalidPizzaCount);
                return 1;
            }
            try {
                checkPizzaCombination(restaurants, orders);
            } catch( InvalidPizzaCombinationException e) {
                e.printStackTrace();
                return 1;
            }
            if (checkPizzaDefined(restaurants, orders)) {
                setStatus(OrderOutcome.InvalidPizzaNotDefined);
                return 1;
            }
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
    }

