package user;

import currencyservice.CourseCurrency;
import currencyservice.CurrencyMONO;
import currencyservice.CurrencyNBU;
import enums.BankName;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static currencyservice.CourseCurrency.getPrivat;

public class UserInfo {
    public static String getInfo(Map<Long, UserSession> userContext, Long chatId) {
        UserCurrency userCurrency = new UserCurrency();
        try {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            String curDate = formatDate.format(today.getTime());
            StringBuilder messageBuilder = new StringBuilder();
            String template;
            template = "Курс на %s %s у %s : продаж %." + (userContext.get(chatId).getBitDepth()+3) + "s, покупка %." + (userContext.get(chatId).getBitDepth()+3) + "s\n";
            for(BankName bank: userContext.get(chatId).getBank()) {
                switch (bank) {
                    case PRIVAT -> {
                        userCurrency.getCoursPRB(getPrivat(curDate), userContext.get(chatId).getCurrency().toString());
                        messageBuilder.append(String.format(template,
                                curDate, userContext.get(chatId).getCurrency().toString(), bank, userCurrency.getRateSell(), userCurrency.getRateBuy()));
                    }
                    case MONO -> {
                        for(CurrencyMONO cur: CourseCurrency.getMONO(curDate)) {
                            if(cur.getCurrency().equals(userContext.get(chatId).getCurrency().getCur())) {
                                messageBuilder.append(String.format(template,
                                        curDate, userContext.get(chatId).getCurrency().toString(), bank, cur.getRateSell(), cur.getRateBuy()));
                            }
                        }
                    }
                    case NBU -> {
                        for(CurrencyNBU cur: CourseCurrency.getNBU(curDate)) {
                            if(cur.getCurrency().equals(userContext.get(chatId).getCurrency().toString())) {
                                messageBuilder.append(String.format(template,
                                        curDate, userContext.get(chatId).getCurrency().toString(), bank, cur.getRateSell(), cur.getRateBuy()));
                            }
                        }
                    }
                    default -> {
                        return "Мабуть, всі банки закриті";
                    }
                }
            }
            return String.valueOf(messageBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
