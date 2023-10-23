import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
@Data
@JsonIgnoreProperties({ "bank", "baseCurrency", "baseCurrencyLit" })
public class CurrencyPRB {
    @JsonProperty("date")
    private String date;
    @JsonProperty("exchangeRate")
    private ExchangeRate[] exchangeRate;


    @Override
    public String toString() {
        return "CurrencyPRB{" +
                "date='" + date + '\'' +
                ", exchangeRate=" + Arrays.toString(exchangeRate) +
                '}';
    }
}

