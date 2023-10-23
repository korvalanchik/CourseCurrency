import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    @Builder
    @JsonIgnoreProperties({ "r030", "txt", "LCurrency" })
public class Currency {
    @JsonProperty("cc")
    private String currency;
    @JsonProperty("exchangedate")
    private String date;
    @JsonProperty("rate")
    private float rateSell;
    private float rateBuy;


    @Override
    public String toString() {
        return "Currency{" +
                "currency=" + currency +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                ", date='" + date + '\'' +
                '}';
    }
}
