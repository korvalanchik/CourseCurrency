import lombok.Builder;
import lombok.Data;

    @Data
    @Builder
public class Currency {
    private Integer currency;
    private String date;
    private float rateSell;
    private float rateBuy;
}
