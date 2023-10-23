import java.io.IOException;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) throws IOException {
        CourseCurrency cur = new CourseCurrency();

//        for(Currency c:cur.getNBU("")) {
//            System.out.println(c);
//        }
        CurrencyPRB currencyPRB = cur.getPrivat("23.10.2023");
//        currencyPRB.getExchangeRate();
//        for(ExchangeRate cc: exchangeRate){
            System.out.println(currencyPRB);
//        }

    }
}
