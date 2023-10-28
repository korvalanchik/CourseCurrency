import User.UserCurrency;
import User.UserSession;
import enums.ConversationState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
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
import static enums.ConversationState.CONVERSATION_STARTED;
import static enums.ConversationState.WAITING_FOR_CHOISE;

@EqualsAndHashCode(callSuper = true)
@Data
public class CurrencyBot extends TelegramLongPollingBot {
    private Map<Long, UserSession> userContext = new ConcurrentHashMap<>();
    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        logUserActivity(update.getMessage().getChat());


        SendMessage message = new SendMessage();
        UserCurrency userCurrency = new UserCurrency();
        Long chatId = update.getMessage().getChatId();
        if(!userContext.containsKey(chatId)) {
            userContext.put(chatId, new UserSession(chatId, CONVERSATION_STARTED, "Privat", "USD"));
        }
        ConversationState state = userContext.get(chatId).getState();
        logUserState(update.getMessage().getChatId());
        message.setChatId(chatId);
        if (isMessagePresent(update) && state.equals(CONVERSATION_STARTED)) {
            message.setText("Ласкаво просимо!\n" +
                            "Цей бот допоможе відслідковувати актуальні та архівні курси валют");
            message.setReplyMarkup(setupBeginButton());
            userContext.get(chatId).setState(WAITING_FOR_CHOISE);
        } else {
            if (isMessagePresent(update) && state.equals(WAITING_FOR_CHOISE)) {
                System.out.println(update.getMessage().getText());

                if (update.getMessage().getText().equalsIgnoreCase("Setting")) {
                    System.out.println(update.getMessage().getText());
                    message.setText("Зміна налаштувань");
                    message.setReplyMarkup(setupSettingKeyboard());
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
            } else {
                message.setText("Incorrect answer. Что будем делать? Please try again");
            }
        }
        try {
            System.out.println("Execute");
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
        row.add("Setting");
        keyboardMarkup.setKeyboard(List.of(row));
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private void logUserActivity(Chat contact) {

        System.out.println("Request from: " + contact.getUserName());
    }

    private void logUserState(Long chatId) {
        System.out.println("with state: " + userContext.get(chatId).getState());
    }

    private static boolean isMessagePresent(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }
}
