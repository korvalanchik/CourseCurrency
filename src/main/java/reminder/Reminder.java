package reminder;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.Objects;

import static keyboard.KeyboadSet.setupBeginButton;

public class Reminder implements Runnable{
    private final String userMessage;
    private final long chatId;

    public Reminder(String userMessage, long chatId) {
        this.userMessage = userMessage;
        this.chatId = chatId;
    }


    @Override
    public void run() {
        SendMessage message = new SendMessage();
        System.out.println(userMessage);
        message.setChatId(chatId);
        message.setText(userMessage);
        message.setReplyMarkup(setupBeginButton());
//        try {
////            System.out.println("Execute");
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder that = (Reminder) o;
        return chatId == that.chatId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }

    public long getChatId() {
        return chatId;
    }
}
