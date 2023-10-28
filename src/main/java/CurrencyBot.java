import User.UserSession;
import currencyservice.CourseCurrency;
import currencyservice.CurrencyPRB;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static config.BotConfig.*;
import static enums.ConversationState.CONVERSATION_STARTED;

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
        Long chatId = update.getMessage().getChatId();
        message.setChatId(chatId);
        if (isMessagePresent(update) && update.getMessage().getText().equalsIgnoreCase(START)) {
            System.out.println(1);


            try {
                System.out.println(2);
                CourseCurrency defaultBank = new CourseCurrency();
                int i = defaultBank.getPrivat("").length;
                System.out.println(defaultBank);
//                        Arrays
//                    .stream(defaultBank)
//                    .filter(currencyPRB -> currencyPRB.getCurrency().equals("USD"))
//                    .findFirst()
//                    .orElse(null);
                UserSession userSession = new UserSession(chatId, CONVERSATION_STARTED, "Privat", "USD");
                userContext.put(chatId, userSession);
                message.setText(String.format("Course USD/UAH in %s bank: sale %s, buy %s", "Privat", "30", "35"));
                System.out.println("2");
                message.setReplyMarkup(setupBeginButton());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
                message.setText("Incorrect answer. Please try again");
        }

        try {
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
//    private ReplyKeyboardMarkup setupQuizKeyboard(QuizResponseApiDto currentQuestion) {
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> rows = new ArrayList<>();
//        currentQuestion.getAnswers().entrySet()
//                .stream()
//                .filter(entry -> entry.getValue() != null)
//                .forEach(enrty -> {
//                    KeyboardRow row = new KeyboardRow();
//                    row.add(enrty.getValue());
//                    rows.add(row);
//                });
//        keyboardMarkup.setKeyboard(rows);
//        return keyboardMarkup;
//    }

    private static ReplyKeyboardMarkup setupBeginButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(BEGIN);
        keyboardMarkup.setKeyboard(List.of(row));
        return keyboardMarkup;
    }

    private void logUserActivity(Chat contact) {
        System.out.println("Request from: " + contact.getUserName());
    }
    private static boolean isMessagePresent(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

}
