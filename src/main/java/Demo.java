import currencyservice.CourseCurrency;
import currencyservice.CurrencyMONO;
import currencyservice.CurrencyNBU;

import java.io.IOException;
import java.util.Arrays;


@Deprecated
public class Demo {
    public static void main(String[] args) throws IOException { // throws TelegramApiException
 //       CourseCurrency mono = new CourseCurrency();
        CurrencyMONO[] mono  = CourseCurrency.getMONO("");
        System.out.println(Arrays.toString(mono));
        System.out.println("Ok");
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        botsApi.registerBot(new CurrencyBot());
    }
}