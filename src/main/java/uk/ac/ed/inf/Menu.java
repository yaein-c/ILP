package uk.ac.ed.inf;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Menu {
    private final String name;
    private final int priceInPence;

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
