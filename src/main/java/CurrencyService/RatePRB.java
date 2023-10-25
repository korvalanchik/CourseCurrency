package CurrencyService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@Builder
@Data
@JsonIgnoreProperties({ "bank", "baseCurrency", "baseCurrencyLit" })
public class RatePRB {
    @JsonProperty("date")
    private String date;
    @JsonProperty("exchangeRate")
    public CurrencyPRB[] exchangeCurrencyPRB;

    @Override
    public String toString() {
        return "CurrencyPRB{" +
                "date='" + date + '\'' +
                ", exchangeRate=" + Arrays.toString(exchangeCurrencyPRB) +
                '}';
    }
}

