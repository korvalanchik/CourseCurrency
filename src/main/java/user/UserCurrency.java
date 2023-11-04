package user;


import currencyservice.CurrencyPRB;

public class UserCurrency {
    private String currency;
    private String date;
    private float rateSell;
    private float rateBuy;
    public void getCoursPRB(CurrencyPRB[] currencyPRB, String currency) {
        for(int i=0;i<currencyPRB.length; i++) {
            if(currencyPRB[i].getCurrency().equals(currency)) {
                this.currency = currency;
                this.date = currencyPRB[i].getDate();
                this.rateSell = currencyPRB[i].getRateSell();
                this.rateBuy = currencyPRB[i].getRateBuy();
                return;
            }
        }
    }


    public float getRateSell() {
        return rateSell;
    }

    public float getRateBuy() {
        return rateBuy;
    }

    @Override
    public String toString() {
        return "UserCurrency{" +
                "currency='" + currency + '\'' +
                ", date='" + date + '\'' +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                '}';
    }
}
