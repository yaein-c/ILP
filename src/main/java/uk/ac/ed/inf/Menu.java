package uk.ac.ed.inf;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for deserialising json data from the server
 * for the Menu items associated with each of the participating restaurants.
 */
public class Menu {
    private final String name;
    private final int priceInPence;

    /**
     * Deserialises a json entry for a menu item and populates the class fields
     * @param name name of the menu item
     * @param priceInPence price of the menu item in pence
     */
    private Menu( @JsonProperty("name") String name,
                  @JsonProperty("priceInPence") int priceInPence)
    {
        this.name = name;
        this.priceInPence = priceInPence;
    }

    /**
     * Getter method for name of menu item
     * @return name
     */
    public String getName() { return name; }

    /**
     * Getter method for priceInPence
     * @return priceInPence
     */
    public int getPriceInPence() { return priceInPence; }
}
