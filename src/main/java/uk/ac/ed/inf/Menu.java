package uk.ac.ed.inf;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Menu {
    private String name;
    private int priceInPence;

    private Menu( @JsonProperty("name") String name,
                  @JsonProperty("priceInPence") int priceInPence)
    {
        this.name = name;
        this.priceInPence = priceInPence;
    }
}
