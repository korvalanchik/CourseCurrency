import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    @Builder
    @JsonIgnoreProperties({ "r030", "txt", "LCurrency" })
public class CurrencyNBU {
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
                ", date='" + date + '\'' +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                '}';
    }

        public float getRateSell() {
            return rateSell;
        }

        public void setRateBuy(float rateBuy) {
            this.rateBuy = rateBuy;
        }
    }
