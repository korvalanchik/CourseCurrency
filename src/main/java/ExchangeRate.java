import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonIgnoreProperties({"baseCurrency", "saleRateNB", "purchaseRateNB"})
public class ExchangeRate {
    @JsonProperty("date")
    private String date;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("saleRate")
    private float rateSell;
    @JsonProperty("purchaseRate")
    private float rateBuy;

    public ExchangeRate(String date, String currency, float rateSell, float rateBuy) {
        this.date = date;
        this.currency = currency;
        this.rateSell = rateSell;
        this.rateBuy = rateBuy;
    }


    @Override
    public String toString() {
        return "ExchangeRate{" +
                "date='" + date + '\'' +
                ", currency='" + currency + '\'' +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                '}';
    }
}
