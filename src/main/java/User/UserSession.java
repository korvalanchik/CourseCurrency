package User;

import enums.ConversationState;
import lombok.Data;

@Data
public class UserSession {
    private Long chatId;
    private ConversationState state;
    private String bank;
    private String currency;

    public UserSession(Long chatId, ConversationState state, String bank, String currency) {
        this.chatId = chatId;
        this.state = state;
        this.bank = bank;
        this.currency = currency;
    }
}
