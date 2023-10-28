import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;


public class Demo {
    public static void main(String[] args) throws TelegramApiException, IOException {
//        UserCurrency userCurrency = new UserCurrency();
//        userCurrency.getCours(getPrivat(""),"EUR");
//        System.out.println(userCurrency.toString());
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new CurrencyBot());
    }
}
