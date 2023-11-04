package keyboard;

import user.UserSession;
import enums.BankName;
import enums.CurrencyName;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class KeyboadSet {
    public static ReplyKeyboardMarkup setupTimeReminderKeyboard(String hour, String minute) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        KeyboardRow row5 = new KeyboardRow();
        row1.add("Встановити чвс"); row1.add("Вимкнути сповіщення");
        row2.add("\u25b2"); row2.add("\u25b3");
        row3.add(hour + " год"); row3.add(minute + " хв");
        row4.add("\u25bc"); row4.add("\u25bd");


        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;

    }

    public static ReplyKeyboardMarkup setupBitDepthKeyboard(long chatId, Map<Long, UserSession> userContext) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for(int i=2;i<6;i++) {
            if (userContext.get(chatId).getBitDepth()==i) row.add(i + " \u2705");
            else row.add(String.valueOf(i));
        }
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }


    public static ReplyKeyboardMarkup setupBankKeyboard(long chatId, Map<Long, UserSession> userContext) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        for(BankName bankName: BankName.values()) {
            if (userContext.get(chatId).getBank().contains(bankName)) {
                row1.add(bankName + " \u2705");
            } else {
                row1.add(String.valueOf(bankName));
            }
        }
        row2.add("Назад");
        rows.add(row1);
        rows.add(row2);
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup setupCurrencyKeyboard(long chatId, Map<Long, UserSession> userContext) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        int countRow = 0;
        for(CurrencyName currencyName: CurrencyName.values()) {
            if (userContext.get(chatId).getCurrency().toString().contains(String.valueOf(currencyName))) {
                if(countRow < 3) row1.add(currencyName + " \u2705");
                else row2.add(currencyName + " \u2705");
            } else {
                if(countRow < 3) row1.add(String.valueOf(currencyName));
                else row2.add(String.valueOf(currencyName));
            }
            countRow++;
        }
        rows.add(row1);
        rows.add(row2);
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }


    public static ReplyKeyboardMarkup setupSettingKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("ФОРМАТ");
        row1.add("БАНК");
        row1.add("ВАЛЮТА");
        row2.add("СПОВІЩЕННЯ");
        row2.add("НАЗАД");
        rows.add(row1);
        rows.add(row2);
        keyboardMarkup.setKeyboard(rows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
    public static ReplyKeyboardMarkup setupBeginButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("Отримати інфо");
        row.add("Налаштування");
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

}
