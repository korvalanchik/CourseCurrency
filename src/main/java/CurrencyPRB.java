import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonIgnoreProperties({"baseCurrency", "saleRateNB", "purchaseRateNB"})
public class CurrencyPRB {
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("date")
    private String date;
    @JsonProperty("saleRate")
    private float rateSell;
    @JsonProperty("purchaseRate")
    private float rateBuy;

    @Override
    public String toString() {
        return "Rate{" +
                "currency='" + currency + '\'' +
                ", date='" + date + '\'' +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                '}';
    }

    public void setDate(String date) {
        this.date = date;
    }
}
