package enums;

public enum CurrencyName {
    GBP ("826"),
    USD ("840"),
    EUR ("978"),
    PLN ("985"),
    JPY ("392"),
    CHF ("756");

    private final String cur;

    CurrencyName(String cur) {
        this.cur = cur;
    }

    public String getCur() {
        return cur;
    }
}
