package User;

import enums.ConversationState;
import enums.CurrencyName;
import lombok.Data;

import java.util.List;

@Data
public class UserSession {
    private Long chatId;
    private ConversationState state;
    private String bank;
    private List<CurrencyName> currency;

    public UserSession(Long chatId, ConversationState state, String bank, List<CurrencyName> currency) {
        this.chatId = chatId;
        this.state = state;
        this.bank = bank;
        this.currency = currency;
    }


    public ConversationState getState() {
        return state;
    }

    public void setState(ConversationState state) {
        this.state = state;
    }
}
