package uk.ac.ed.inf;

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
        InvalidTotal ,
        InvalidPizzaNotDefined ,
        InvalidPizzaCount ,
        InvalidPizzaCombinationMultipleSuppliers ,
        Invalid
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

