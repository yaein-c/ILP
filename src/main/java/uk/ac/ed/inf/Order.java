package uk.ac.ed.inf;

import java.util.ArrayList;

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
     * Getter method for status of OrderOutcome
     * @return status
     */
    public OrderOutcome getStatus() {
        return status;
    }

    /**
     * Setter method for status of OrderOutcome
     * @param status
     */
    public void setStatus(OrderOutcome status) {
        this.status = status;
    }


    /**
     * Throws exception if the ordered pizza combination cannot be delivered by the same restaurant
     * @param restaurants
     * @param orders
     * @throws InvalidPizzaCombinationException
     */
     public void checkPizzaCombination(Restaurant[] restaurants, String[] orders) throws InvalidPizzaCombinationException {
         Restaurant currentRestaurant = null;

         //find restaurant associated with first order
         mainLoop:
         for (Restaurant r : restaurants) {
             for (Menu item : r.getMenu()) {
                 if (item.getName().equalsIgnoreCase(orders[0])) {
                     currentRestaurant = r;
                     break mainLoop;
                 }
             }
         }

         //check if all orders come from same restaurant as first
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
     *
     * @param restaurants array of the participating restaurants in the order
     * @param orders array of names of each individual pizza ordered
     * @return total cost including the pizzas and the delivery charge.
     */
        public int getDeliveryCost(Restaurant[] restaurants, String[] orders){

            //get array of all menu items
            Menu[] allMenus = getAllMenus(restaurants);
            String[] names = new String[allMenus.length];
            for (int i = 0; i < allMenus.length; i++) {
                names[i] = allMenus[i].getName();
            }

            //check if pizza exists and store in bool
            var list = new ArrayList<>();
            for (String i : orders) {
                for (String item : names) {
                    if (item.equalsIgnoreCase(i)) {
                        list.add(1);
                    }
                }

            }
            boolean pizzaExists = list.size() == orders.length;

            //invalid pizza count
            if ( (orders.length < 1) || (orders.length > 4) ) {
                setStatus(OrderOutcome.InvalidPizzaCount);
            }
            //pizza doesn't exist
            else if (pizzaExists) {
                setStatus(OrderOutcome.InvalidPizzaNotDefined);

            //all good calculate cost
            } else {
                int cost = 100; //cost in pence
                Restaurant currentRestaurant = null;
                Menu[] currentMenu;

                //find restaurant that does these items
                for (Restaurant r : restaurants) {
                    for (Menu m : r.getMenu() ) {
                        if (m.getName().equalsIgnoreCase(orders[0])) {
                            currentRestaurant = r;
                        }
                    }
                }

                //find cost of each order and add to delivery cost
                currentMenu = currentRestaurant.getMenu();
                for (String i : orders ) {
                    for (Menu j : currentMenu ) {
                        if ( j.getName().equalsIgnoreCase(i) ) {
                            cost += j.getPriceInPence();
                        }
                    }
                }
                return cost;
            }
            //something has gone wrong
            setStatus(OrderOutcome.Invalid);
            return 0;

        }

    }

