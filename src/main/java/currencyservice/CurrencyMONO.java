package currencyservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Builder
@JsonIgnoreProperties({ "currencyCodeB", "rateCross" })
public class CurrencyMONO {
    @JsonProperty("currencyCodeA")
    private String  currency;
    @JsonProperty("date")
    private String  date;
    @JsonProperty("rateSell")
    private String  rateSell;
    @JsonProperty("rateBuy")
    private String  rateBuy;


    public String getCurrency() {
        return currency;
    }

    public String getDate() {
        return date;
    }

    public String getRateSell() {
        return rateSell;
    }

    public String getRateBuy() {
        return rateBuy;
    }
}
