import User.UserCurrency;
import User.UserSession;
import enums.BankName;
import enums.ConversationState;
import enums.CurrencyName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reminder.Reminder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;


import static calendar.CustomTime.hourCustom;
import static calendar.CustomTime.minuteCustom;
import static config.BotConfig.*;
import static currencyservice.CourseCurrency.getPrivat;
import static enums.BankName.*;
import static enums.ConversationState.*;
import static enums.CurrencyName.*;
import static keyboard.KeyboadSet.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Deprecated
public class CurrencyBot extends TelegramLongPollingBot {
    private Map<Long, UserSession> userContext = new ConcurrentHashMap<>();

    private boolean screaming = false;

    private InlineKeyboardMarkup keyboardBitdepth, keyboardBank, keyboardCurrency, keyboardMarkup;


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
    @Override
    public String getBotToken() { return TOKEN; }

    @Override
    public void onUpdateReceived(Update update) {
//        System.out.println(update);

        SendMessage message = new SendMessage();
        UserCurrency userCurrency = new UserCurrency();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        List<ScheduledFuture<?>> remindHandler = new ArrayList<>();

        Long chatId;
        if(isMessagePresent(update)) {
            chatId = update.getMessage().getChatId();
            logUserActivity(update.getMessage().getChat());
        } else if(update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
        } else chatId = 0L;

        if(!userContext.containsKey(chatId)) {
            List<BankName> bn = new ArrayList<>();
            bn.add(PRIVAT);
            userContext.put(chatId, new UserSession(chatId, CONVERSATION_STARTED, bn, USD, 2, "08", "00", false));
        }
        ConversationState state = userContext.get(chatId).getState();
        message.setChatId(chatId);
        System.out.println(state);
        switch (state) {
            case CONVERSATION_STARTED -> {
                message.setText("Ласкаво просимо!\n" +
                        "Цей бот допоможе відслідковувати актуальні та архівні курси валют");
                message.setReplyMarkup(setupBeginButton());
                userContext.get(chatId).setState(WAITING_FOR_CHOISE);
            }
            case WAITING_FOR_CHOISE -> {
                if (update.getMessage().getText().equalsIgnoreCase("Налаштування")) {
//                    System.out.println(update.getMessage().getText());
                    message.setText("Зміна налаштувань");
                    message.setReplyMarkup(setupSettingKeyboard());
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                } else if (update.getMessage().getText().equalsIgnoreCase("Отримати інфо")) {

                    try {
                        userCurrency.getCours(getPrivat("30.10.2023"), "USD");
//                        System.out.println(userCurrency);
                        message.setText(String.format("Course USD/UAH in %s bank: sale %s, buy %s",
                                "Privat", userCurrency.getRateSell(), userCurrency.getRateBuy()));
                        message.setReplyMarkup(setupBeginButton());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } else return;
            }
            case WAITING_FOR_SETTING -> {
                if (update.getMessage().getText().equalsIgnoreCase("НАЗАД")) {
//                    System.out.println(update.getMessage().getText());
                    message.setText("Виберіть \u261f");
                    message.setReplyMarkup(setupBeginButton());
                    userContext.get(chatId).setState(WAITING_FOR_CHOISE);
                } else if (update.getMessage().getText().equalsIgnoreCase("ВАЛЮТА")) {
                    message.setText("Виберіть валюту");
                    message.setReplyMarkup(setupCurrencyKeyboard(chatId, userContext));
                    userContext.get(chatId).setState(GETTING_CURRENCY);
                } else if (update.getMessage().getText().equalsIgnoreCase("БАНК")) {
                    message.setText("Виберіть банк. Можна декілька");
                    message.setReplyMarkup(setupBankKeyboard(chatId, userContext));
                    userContext.get(chatId).setState(GETTING_BANK);
                } else if (update.getMessage().getText().equalsIgnoreCase("ФОРМАТ")) {
                    message.setText("Виберіть разрядність значення курсу");
                    message.setReplyMarkup(setupBitDepthKeyboard(chatId, userContext));
                    userContext.get(chatId).setState(GETTING_FORMAT);
                } else if (update.getMessage().getText().equalsIgnoreCase("СПОВІЩЕННЯ")) {
                    message.setText("Коли Ви хочете бачити гарний курс?");
                    message.setReplyMarkup(setupTimeReminderKeyboard(userContext.get(chatId).getHour(), userContext.get(chatId).getMinute()));
                    userContext.get(chatId).setState(GETTING_REMINDER);
                } else return;
            }
            case GETTING_CURRENCY -> {
                userContext.get(chatId).setCurrency(CurrencyName.valueOf(update.getMessage().getText().split(" ")[0]));
                message.setText("Обрано: " + userContext.get(chatId).getCurrency());
                message.setReplyMarkup(setupSettingKeyboard());
                userContext.get(chatId).setState(WAITING_FOR_SETTING);
            }
            case GETTING_BANK -> {
                if (update.getMessage().getText().equalsIgnoreCase("НАЗАД")) {
                    int count = userContext.get(chatId).getBank().size();
                    message.setText("Обрано: " + ((count > 1) ? count + " банки" : count + " банк"));
                    message.setReplyMarkup(setupSettingKeyboard());
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                } else {
//                    System.out.println(update.getMessage().getText().split(" ")[0]);
                    if (userContext.get(chatId).getBank().contains(BankName.valueOf(update.getMessage().getText().split(" ")[0]))) {
                        userContext.get(chatId).getBank().remove(BankName.valueOf(update.getMessage().getText().split(" ")[0]));
                    } else {
                        userContext.get(chatId).getBank().add(BankName.valueOf(update.getMessage().getText().split(" ")[0]));
                    }

                    if (userContext.get(chatId).getBank().isEmpty()) {
                        userContext.get(chatId).getBank().add(PRIVAT);
                        message.setText("За замовчуванням вибрано: " + PRIVAT);
                    }
                    message.setText("Ok");
                    message.setReplyMarkup(setupBankKeyboard(chatId, userContext));
                    userContext.get(chatId).setState(GETTING_BANK);
                }
            }
            case GETTING_FORMAT -> {
                if(Integer.parseInt(update.getMessage().getText().split(" ")[0]) < 5) {
                    userContext.get(chatId).setBitDepth(Integer.parseInt(update.getMessage().getText().split(" ")[0]));
                    message.setText("Обрано: " + userContext.get(chatId).getBitDepth() + " знаки після коми");
                } else {
                    userContext.get(chatId).setBitDepth(4);
                    message.setText("Це вже занадто. Вистачить і 4-ох знаки після коми");
                }
                message.setReplyMarkup(setupSettingKeyboard());
                userContext.get(chatId).setState(WAITING_FOR_SETTING);

            }
            case GETTING_REMINDER -> {
                if(update.getMessage().getText().equals("\u25bc")){
                    int next = hourCustom.indexOf(userContext.get(chatId).getHour()) + 1;
                    if(next > 23) next = 0;
                    userContext.get(chatId).setHour(hourCustom.get(next));
                    message.setText("+1 год.");
                }
                if(update.getMessage().getText().equals("\u25bd")){
                    int next = minuteCustom.indexOf(userContext.get(chatId).getMinute()) + 1;
                    if(next > 11) next = 0;
                    userContext.get(chatId).setMinute(minuteCustom.get(next));
                    message.setText("+5 хв.");
                }
                if(update.getMessage().getText().equals("\u25b2")){
                    int next = hourCustom.indexOf(userContext.get(chatId).getHour()) - 1;
                    if(next <0) next = 23;
                    userContext.get(chatId).setHour(hourCustom.get(next));
                    message.setText("-1 год.");
                }
                if(update.getMessage().getText().equals("\u25b3")){
                    int next = minuteCustom.indexOf(userContext.get(chatId).getMinute()) - 1;
                    if(next < 0) next = 11;
                    userContext.get(chatId).setMinute(minuteCustom.get(next));
                    message.setText("-5 хв.");
                }
                System.out.println(userContext.get(chatId).getHour() + ":" + userContext.get(chatId).getMinute());
                message.setReplyMarkup(setupTimeReminderKeyboard(userContext.get(chatId).getHour(), userContext.get(chatId).getMinute()));
                if(update.getMessage().getText().equals("Встановити чвс")) {

//                    Iterator<ScheduledFuture<?>> remindIterator;
//                    remindIterator = remindHandler.iterator();
//                    while(remindIterator.hasNext()) {//до тех пор, пока в списке есть элементы
//                        ScheduledFuture<?> nextTask = remindIterator.next();
//                        if (nextTask.equals(chatId)) {
//                            nextTask.cancel(true);
//                            remindIterator.remove();//удаляем кота с нужным именем
//                        }
//                    }

                    userContext.get(chatId).setReminded(true);
                    message.setText("Час сповіщення кожного дня о " + userContext.get(chatId).getHour() + ":" + userContext.get(chatId).getMinute());
                    message.setReplyMarkup(setupSettingKeyboard());
                    String userMessage = "Сьогодні банк не працює. Сидіть вдома. Пийте чай";
                    remindHandler.add(scheduler.scheduleAtFixedRate(new ReminderTask(userMessage, chatId), 3, 7, TimeUnit.SECONDS));
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                } //else return;
                if(update.getMessage().getText().equals("Вимкнути сповіщення")) {
                    message.setText("Сповіщення за часом вимкнуто");
                    userContext.get(chatId).setReminded(false);
                    message.setReplyMarkup(setupSettingKeyboard());
                    System.out.println("Size of remind " + remindHandler.size());
                    Iterator<ScheduledFuture<?>> remindIterator;
                    remindIterator = remindHandler.iterator();
                    while(remindIterator.hasNext()) {//до тех пор, пока в списке есть элементы
                        ScheduledFuture<?> nextTask = remindIterator.next();
                        if (nextTask.equals(chatId)) {
                            nextTask.cancel(true);
                            remindIterator.remove();//удаляем кота с нужным именем
                        }
                    }

//                    threatService.stopRemind();
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                } //else return;

            }
            case FROM_REMiNDER -> {
                return;
            }
            default -> {
                message.setText("Не треба нічого вводити. Тільки тицяйте кнопки");
                System.out.println("State not set");
                message.setReplyMarkup(setupSettingKeyboard());
                userContext.get(chatId).setState(WAITING_FOR_SETTING);
            }
        }
        try {
//            System.out.println("Execute");
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    private ReplyKeyboardMarkup setupTimeReminderKeyboard(String hour, String minute) {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> rows = new ArrayList<>();
//        KeyboardRow row1 = new KeyboardRow();
//        KeyboardRow row2 = new KeyboardRow();
//        KeyboardRow row3 = new KeyboardRow();
//        KeyboardRow row4 = new KeyboardRow();
//        KeyboardRow row5 = new KeyboardRow();
//        row1.add("Встановити чвс");
//        row2.add("\u25b2"); row2.add("\u25b3");
//        row3.add(hour + " год"); row3.add(minute + " хв");
//        row4.add("\u25bc"); row4.add("\u25bd");
//        row5.add("Відключити сповіщення");
//
//        rows.add(row1);
//        rows.add(row2);
//        rows.add(row3);
//        rows.add(row4);
//        rows.add(row5);
//        keyboardMarkup.setKeyboard(rows);
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//
//    }
//
//    private ReplyKeyboardMarkup setupBitDepthKeyboard(long chatId) {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> rows = new ArrayList<>();
//        KeyboardRow row = new KeyboardRow();
//        for(int i=2;i<6;i++) {
//            if (userContext.get(chatId).getBitDepth()==i) row.add(i + " \u2705");
//            else row.add(String.valueOf(i));
//        }
//        rows.add(row);
//        keyboardMarkup.setKeyboard(rows);
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//    }
//
//
//    private ReplyKeyboardMarkup setupBankKeyboard(long chatId) {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> rows = new ArrayList<>();
//        KeyboardRow row1 = new KeyboardRow();
//        KeyboardRow row2 = new KeyboardRow();
//        for(BankName bankName: BankName.values()) {
//            if (userContext.get(chatId).getBank().contains(bankName)) {
//                row1.add(bankName + " \u2705");
//            } else {
//                row1.add(String.valueOf(bankName));
//            }
//        }
//        row2.add("Назад");
//        rows.add(row1);
//        rows.add(row2);
//        keyboardMarkup.setKeyboard(rows);
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//    }
//
//    private ReplyKeyboardMarkup setupCurrencyKeyboard(long chatId) {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> rows = new ArrayList<>();
//        KeyboardRow row1 = new KeyboardRow();
//        KeyboardRow row2 = new KeyboardRow();
//        int countRow = 0;
//        for(CurrencyName currencyName: CurrencyName.values()) {
//            if (userContext.get(chatId).getCurrency().toString().contains(String.valueOf(currencyName))) {
//                if(countRow < 3) row1.add(currencyName + " \u2705");
//                else row2.add(currencyName + " \u2705");
//            } else {
//                if(countRow < 3) row1.add(String.valueOf(currencyName));
//                else row2.add(String.valueOf(currencyName));
//            }
//            countRow++;
//        }
//        rows.add(row1);
//        rows.add(row2);
//        keyboardMarkup.setKeyboard(rows);
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//    }
//
//
//    private ReplyKeyboardMarkup setupSettingKeyboard() {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> rows = new ArrayList<>();
//        KeyboardRow row1 = new KeyboardRow();
//        KeyboardRow row2 = new KeyboardRow();
//        row1.add("ФОРМАТ");
//        row1.add("БАНК");
//        row1.add("ВАЛЮТА");
//        row2.add("СПОВІЩЕННЯ");
//        row2.add("НАЗАД");
//        rows.add(row1);
//        rows.add(row2);
//        keyboardMarkup.setKeyboard(rows);
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//    }
//    private static ReplyKeyboardMarkup setupBeginButton() {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        KeyboardRow row = new KeyboardRow();
//        row.add(BEGIN_INFO);
//        row.add(BEGIN_SETTING);
//        keyboardMarkup.setKeyboard(List.of(row));
//        keyboardMarkup.setResizeKeyboard(true);
//        return keyboardMarkup;
//    }



    private void logUserActivity(Chat contact) {
        System.out.println("Request from: " + contact.getUserName());
    }

    private void logUserState(Long chatId) {
        System.out.println("with state: " + userContext.get(chatId).getState());
    }

    private static boolean isMessagePresent(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }



    public class ReminderTask implements Runnable{
        private final String userMessage;
        private final long chatId;

        public ReminderTask(String userMessage, long chatId) {
            this.userMessage = userMessage;
            this.chatId = chatId;
        }


        @Override
        public void run() {
            SendMessage userMes = new SendMessage();
            System.out.println(userMessage);
            userMes.setChatId(chatId);
            userMes.setText(userMessage);
//            userMes.setReplyMarkup(setupBeginButton());
//            userContext.get(chatId).setState(FROM_REMiNDER);
            try {
//            System.out.println("Execute");
                execute(userMes);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        @Override
        public int hashCode() {
            return Objects.hash(chatId);
        }

        public long getChatId() {
            return chatId;
        }
    }




}




