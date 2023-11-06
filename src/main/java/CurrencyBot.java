import currencyservice.CourseCurrency;
import currencyservice.CurrencyMONO;
import currencyservice.CurrencyNBU;
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
import user.UserCurrency;
import user.UserInfo;
import user.UserSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
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
import static user.UserInfo.getInfo;

@EqualsAndHashCode(callSuper = true)
@Data
@Deprecated
public class CurrencyBot extends TelegramLongPollingBot {
    private Map<Long, UserSession> userContext = new ConcurrentHashMap<>();

//    private boolean screaming = false;

//    private InlineKeyboardMarkup keyboardBitdepth, keyboardBank, keyboardCurrency, keyboardMarkup;


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
    @Override
    public String getBotToken() { return TOKEN; }

    @Override
    public void onUpdateReceived(Update update) {

        SendMessage message = new SendMessage();
//        UserCurrency userCurrency = new UserCurrency();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
            userContext.put(chatId, new UserSession(chatId, CONVERSATION_STARTED, bn, USD, 2, "08", "00", null,false));
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
                    message.setText("Зміна налаштувань");
                    message.setReplyMarkup(setupSettingKeyboard());
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                } else if (update.getMessage().getText().equalsIgnoreCase("Отримати інфо")) {
                    message.setText(getInfo(userContext,chatId));
                    message.setReplyMarkup(setupBeginButton());

                } else return;
            }
            case WAITING_FOR_SETTING -> {
                if (update.getMessage().getText().equalsIgnoreCase("НАЗАД")) {
                    message.setText("Виберіть ☟");
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
                System.out.println(userContext.get(chatId).getBank());
            }
            case GETTING_FORMAT -> {
                if(Integer.parseInt(update.getMessage().getText().split(" ")[0]) < 5) {
                    userContext.get(chatId).setBitDepth(Integer.parseInt(update.getMessage().getText().split(" ")[0]));
                    System.out.println("BitDepth " + userContext.get(chatId).getBitDepth());
                    message.setText("Обрано: " + userContext.get(chatId).getBitDepth() + " знаки після коми");
                } else {
                    userContext.get(chatId).setBitDepth(4);
                    message.setText("Це вже занадто. Вистачить і 4-ох знаки після коми");
                }
                message.setReplyMarkup(setupSettingKeyboard());
                userContext.get(chatId).setState(WAITING_FOR_SETTING);

            }
            case GETTING_REMINDER -> {
                if(update.getMessage().getText().equals("▼")){
                    int next = hourCustom.indexOf(userContext.get(chatId).getHour()) + 1;
                    if(next > 23) next = 0;
                    userContext.get(chatId).setHour(hourCustom.get(next));
                    message.setText("+1 год.");
                }
                if(update.getMessage().getText().equals("▽")){
                    int next = minuteCustom.indexOf(userContext.get(chatId).getMinute()) + 1;
                    if(next > 11) next = 0;
                    userContext.get(chatId).setMinute(minuteCustom.get(next));
                    message.setText("+5 хв.");
                }
                if(update.getMessage().getText().equals("▲")){
                    int next = hourCustom.indexOf(userContext.get(chatId).getHour()) - 1;
                    if(next <0) next = 23;
                    userContext.get(chatId).setHour(hourCustom.get(next));
                    message.setText("-1 год.");
                }
                if(update.getMessage().getText().equals("△")){
                    int next = minuteCustom.indexOf(userContext.get(chatId).getMinute()) - 1;
                    if(next < 0) next = 11;
                    userContext.get(chatId).setMinute(minuteCustom.get(next));
                    message.setText("-5 хв.");
                }
                System.out.println(userContext.get(chatId).getHour() + ":" + userContext.get(chatId).getMinute());
                message.setReplyMarkup(setupTimeReminderKeyboard(userContext.get(chatId).getHour(), userContext.get(chatId).getMinute()));
                if(update.getMessage().getText().equals("Встановити чвс")) {

                    userContext.get(chatId).setReminded(true);
                    //                    String userMessage = getInfo(userContext,chatId) + " старий";

                    LocalTime time = LocalTime.now();
                    LocalTime timeSet = LocalTime.of(Integer.parseInt(userContext.get(chatId).getHour()), Integer.parseInt(userContext.get(chatId).getMinute()));
                    int diff = timeSet.toSecondOfDay() - time.toSecondOfDay();
                    long delay = timeSet.isAfter(time) ? diff : 86400 + diff;
                    System.out.println("Time now " + time + ", timeset=" + timeSet);

                    userContext.get(chatId).setReminder(scheduler.scheduleAtFixedRate(new ReminderTask(chatId), delay, 86400, TimeUnit.SECONDS));
                    message.setText("Час сповіщення кожного дня о " + userContext.get(chatId).getHour() + ":" + userContext.get(chatId).getMinute());
                    message.setReplyMarkup(setupSettingKeyboard());
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                }
                if(update.getMessage().getText().equals("Вимкнути сповіщення")) {
                    message.setText("Сповіщення за часом вимкнуто");
                    if(userContext.get(chatId).isReminded()){
                        userContext.get(chatId).getReminder().cancel(true);
                    }
                    message.setReplyMarkup(setupSettingKeyboard());
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                }

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
            System.out.println(message.getText());
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void logUserActivity(Chat contact) {
        System.out.println("Request from: " + contact.getFirstName());
    }


    private static boolean isMessagePresent(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public class ReminderTask implements Runnable{
        private final long chatId;

        public ReminderTask(long chatId) {
            this.chatId = chatId;
        }


        @Override
        public void run() {
            SendMessage userMes = new SendMessage();
            userMes.setChatId(chatId);
            String info = getInfo(userContext,chatId);
            System.out.println(info);
            userMes.setText(info);
            try {
                execute(userMes);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        @Override
        public int hashCode() {
            return Objects.hash(chatId);
        }

    }
}




