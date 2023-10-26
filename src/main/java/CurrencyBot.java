import lombok.Data;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static config.BotConfig.*;

@Data
public class CurrencyBot extends TelegramLongPollingBot {
    private Map<Long, Long> userContext = new ConcurrentHashMap<>();

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
        logUserActivity(update.getMessage().getContact());
        SendMessage message = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        message.setChatId(chatId);
        if (isMessagePresent(update) && update.getMessage().getText().equalsIgnoreCase(START)) {
            List<String> defaultQuiz = List.of(new String[]{"Aaa", "Bbb"});
            userContext.put(chatId, Long.valueOf("55"));
            message.setText(String.format("Quiz is ready and contains %s questions. Press %s to start", defaultQuiz.size(), BEGIN));
            message.setReplyMarkup(setupBeginButton());
        } else if ((isMessagePresent(update) && update.getMessage().getText().equalsIgnoreCase(BEGIN))) {
            sendNextMessage(chatId, message);
        } else {
            String currentAnswer = update.getMessage().getText();
            long quiz = userContext.get(chatId);
            if (currentAnswer.equalsIgnoreCase(String.valueOf(quiz))) {
                sendNextMessage(chatId, message);
            } else {
                message.setText("Incorrect answer. Please try again");
            }
        }

        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendNextMessage(Long chatId, SendMessage message) {
        message.setChatId(chatId);
        long quiz = userContext.get(chatId);
        List<String> quizList = List.of(new String[]{"22", "33"});
        int counter = 1;
        if (quizList.size() > counter.get()) {
            QuizResponseApiDto currentQuestion = quizList.get(counter.addAndGet(1));
            message.setText(currentQuestion.getQuestion());
            message.setReplyMarkup(setupQuizKeyboard(currentQuestion));
            String correctAnswerNumber = findCorrectAnswer(currentQuestion.getCorrectAnswers());
            quiz.setLastCorrectAnswer(currentQuestion.getAnswers().get(correctAnswerNumber));
        } else {
            message.setText("Quiz is completed");
            quiz.setLastCorrectAnswer(null);
        }
    }

    private ReplyKeyboardMarkup setupQuizKeyboard(QuizResponseApiDto currentQuestion) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        currentQuestion.getAnswers().entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(enrty -> {
                    KeyboardRow row = new KeyboardRow();
                    row.add(enrty.getValue());
                    rows.add(row);
                });
        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private static ReplyKeyboardMarkup setupBeginButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(BEGIN);
        keyboardMarkup.setKeyboard(List.of(row));
        return keyboardMarkup;
    }

    private void logUserActivity(Contact contact) {
        System.out.println("Request from: " + contact.getFirstName());
    }
    private static boolean isMessagePresent(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

}
