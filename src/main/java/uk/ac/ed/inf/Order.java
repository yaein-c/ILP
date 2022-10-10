package uk.ac.ed.inf;

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

    public OrderOutcome getStatus() {
        return status;
    }

    public void setStatus(OrderOutcome status) {
        this.status = status;
    }


    /**
     *
     * @param restaurants array of the participating restaurants in the order
     * @param orders array of names of each individual pizza ordered
     * @return total cost including the pizzas and the delivery charge.
     */

    /**
     * Throws exception if the ordered pizza combination cannot be delivered by the same restaurant
     * @param restaurants
     * @param orders
     * @throws InvalidPizzaCombinationException
     */
     public void validate (Restaurant[] restaurants, String[] orders) throws InvalidPizzaCombinationException {
        //if pizzas aren't all from same restaurant
        if (age < 18) {

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
            try{
                validate(restaurants, orders);

                // check if invalid pizza count
                //check if multiple suppliers
                //check if pizza exists

            } catch (InvalidPizzaCombinationException e){
                e.printStackTrace();
            }
            int cost = 1; //delivery charge

            //more stuff
            
            return cost;
        }

    }

