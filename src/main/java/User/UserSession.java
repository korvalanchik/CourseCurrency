package User;

import enums.BankName;
import enums.ConversationState;
import enums.CurrencyName;
import lombok.Data;

import java.util.List;

@Data
public class UserSession {
    private Long chatId;
    private ConversationState state;
    private List<BankName> bank;
    private CurrencyName currency;
    private int bitDepth;
    private String hour;
    private String minute;

    public UserSession(Long chatId, ConversationState state, List<BankName> bank, CurrencyName currency, int bitDepth, String hour, String minute) {
        this.chatId = chatId;
        this.state = state;
        this.bank = bank;
        this.currency = currency;
        this.bitDepth = bitDepth;
        this.hour = hour;
        this.minute = minute;
    }


    public CurrencyName getCurrency() { return currency; }

    public void setCurrency(CurrencyName currency) { this.currency = currency; }


    public ConversationState getState() {
        return state;
    }

    public void setState(ConversationState state) {
        this.state = state;
    }

    public List<BankName> getBank() {
        return bank;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String  getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }
}
