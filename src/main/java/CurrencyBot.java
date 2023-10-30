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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static config.BotConfig.*;
import static currencyservice.CourseCurrency.getPrivat;
import static enums.BankName.*;
import static enums.ConversationState.*;
import static enums.CurrencyName.*;

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
        System.out.println(update);

        SendMessage message = new SendMessage();
        UserCurrency userCurrency = new UserCurrency();
        Long chatId;
        if(isMessagePresent(update)) {
            chatId = update.getMessage().getChatId();
            logUserActivity(update.getMessage().getChat());
        } else if(update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getFrom().getId();
        } else chatId = 0L;
//---------------------------------------------------------------
//        var privat = InlineKeyboardButton.builder().text("Приват \u2705").callbackData("Privat").build();
//        var mono = InlineKeyboardButton.builder().text("Моно").callbackData("Mono").build();
//        var nbu = InlineKeyboardButton.builder().text("НБУ").callbackData("Nbu").build();
//
//        var gbr = InlineKeyboardButton.builder().text("Фунт").callbackData("gbu").build();
//        var usd = InlineKeyboardButton.builder().text("Долар \u2705").callbackData("usd").build();
//        var eur = InlineKeyboardButton.builder().text("Євро").callbackData("eur").build();
//        var pln = InlineKeyboardButton.builder().text("Злотий").callbackData("pln").build();
//        var jpy = InlineKeyboardButton.builder().text("Єна").callbackData("jpy").build();
//
//        var bitdepth1 = InlineKeyboardButton.builder().text("2 \u2705").callbackData("2").build();
//        var bitdepth2 = InlineKeyboardButton.builder().text("3").callbackData("3").build();
//        var bitdepth3 = InlineKeyboardButton.builder().text("4").callbackData("4").build();
//
//        keyboardBitdepth = InlineKeyboardMarkup.builder()
//                .keyboardRow(List.of(bitdepth1))
//                .keyboardRow(List.of(bitdepth2))
//                .keyboardRow(List.of(bitdepth3))
//                .build();
//
//        keyboardBank = InlineKeyboardMarkup.builder()
//                .keyboardRow(List.of(privat))
//                .keyboardRow(List.of(mono))
//                .keyboardRow(List.of(nbu))
//                .build();
//
//        keyboardCurrency = InlineKeyboardMarkup.builder()
//                .keyboardRow(List.of(InlineKeyboardButton.builder().text("Долар \u2705").callbackData("usd").build()))
//                .keyboardRow(List.of(eur))
//                .keyboardRow(List.of(gbr))
//                .keyboardRow(List.of(pln))
//                .keyboardRow(List.of(jpy))
//                .build();

//        sendMenu(chatId, "Виберіть банк", keyboardBank);
//        sendMenu(chatId, "Виберіть валюту", keyboardCurrency);
//        sendMenu(chatId, "Виберіть разрядність", keyboardBitdepth);

//--------------------------------------------------------------

        if(!userContext.containsKey(chatId)) {
            List<BankName> bn = new ArrayList<>();
            bn.add(PRIVAT);
            userContext.put(chatId, new UserSession(chatId, CONVERSATION_STARTED, bn, USD, 2));
        }
        ConversationState state = userContext.get(chatId).getState();
        message.setChatId(chatId);

        switch (state) {
            case CONVERSATION_STARTED -> {
                message.setText("Ласкаво просимо!\n" +
                        "Цей бот допоможе відслідковувати актуальні та архівні курси валют");
                message.setReplyMarkup(setupBeginButton());
                userContext.get(chatId).setState(WAITING_FOR_CHOISE);
            }
            case WAITING_FOR_CHOISE -> {
                if (update.getMessage().getText().equalsIgnoreCase("Налаштування")) {
                    System.out.println(update.getMessage().getText());
                    message.setText("Зміна налаштувань");
                    message.setReplyMarkup(setupSettingKeyboard());
                    userContext.get(chatId).setState(WAITING_FOR_SETTING);
                } else {


                    try {
                        userCurrency.getCours(getPrivat("10.01.2019"), "USD");
                        System.out.println(userCurrency);
                        message.setText(String.format("Course USD/UAH in %s bank: sale %s, buy %s",
                                "Privat", userCurrency.getRateSell(), userCurrency.getRateBuy()));
                        message.setReplyMarkup(setupBeginButton());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            case WAITING_FOR_SETTING -> {
                if (update.getMessage().getText().equalsIgnoreCase("НАЗАД")) {
                    System.out.println(update.getMessage().getText());
                    message.setText("Виберіть \u261f");
                    message.setReplyMarkup(setupBeginButton());
                    userContext.get(chatId).setState(WAITING_FOR_CHOISE);
                } else if (update.getMessage().getText().equalsIgnoreCase("ВАЛЮТА")) {
                    message.setText("Виберіть валюту");
                    message.setReplyMarkup(setupCurrencyKeyboard(chatId));
                    userContext.get(chatId).setState(GETTING_CURRENCY);
                } else if (update.getMessage().getText().equalsIgnoreCase("БАНК")) {
                    message.setText("Виберіть банк. Можна декілька");
                    message.setReplyMarkup(setupBankKeyboard(chatId));
                    userContext.get(chatId).setState(GETTING_BANK);
                } else if (update.getMessage().getText().equalsIgnoreCase("ФОРМАТ")) {
                    message.setText("Виберіть разрядність значення курсу");
                    message.setReplyMarkup(setupBitDepthKeyboard(chatId));
                    userContext.get(chatId).setState(GETTING_FORMAT);
                }
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
                    System.out.println(update.getMessage().getText().split(" ")[0]);
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
                    message.setReplyMarkup(setupBankKeyboard(chatId));
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
            default -> {
                message.setText("Не треба нічого вводити. Тільки клацайте");
                System.out.println("State not set");
            }
//                return;
        }
        try {
            System.out.println("Execute");
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup setupBitDepthKeyboard(long chatId) {
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


    private ReplyKeyboardMarkup setupBankKeyboard(long chatId) {
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

    private ReplyKeyboardMarkup setupCurrencyKeyboard(long chatId) {
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


    private ReplyKeyboardMarkup setupSettingKeyboard() {
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
    private static ReplyKeyboardMarkup setupBeginButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(BEGIN_INFO);
        row.add(BEGIN_SETTING);
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }


//    public void sendMenu(Long who, String txt, InlineKeyboardMarkup kb){
//        SendMessage sm = SendMessage.builder().chatId(who)
//                .text(txt)
//                .replyMarkup(kb).build();
////        sm.setChatId(who);
////        System.out.println(sm);
//        try {
//            execute(sm);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
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


//    private void sendMessage(Long chatId, String textToSend, Update update){
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(String.valueOf(chatId));
//        sendMessage.setText(textToSend);
//
//        try {
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }


//    private void sendNextMessage(Long chatId, SendMessage message) {
//        message.setChatId(chatId);
//        long quiz = userContext.get(chatId);
//        List<String> quizList = List.of(new String[]{"22", "33"});
//        int counter = 1;
//        if (quizList.size() > counter.get()) {
//            QuizResponseApiDto currentQuestion = quizList.get(counter.addAndGet(1));
//            message.setText(currentQuestion.getQuestion());
//            message.setReplyMarkup(setupQuizKeyboard(currentQuestion));
//            String correctAnswerNumber = findCorrectAnswer(currentQuestion.getCorrectAnswers());
//            quiz.setLastCorrectAnswer(currentQuestion.getAnswers().get(correctAnswerNumber));
//        } else {
//            message.setText("Quiz is completed");
//            quiz.setLastCorrectAnswer(null);
//        }
//    }
//




}

