import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CourseCurrency {
    private static final String BASE_URL_COURSE_NBU = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json&date=";
    private static final String BASE_URL_COURSE_PRIVAT = "https://api.privatbank.ua/p24api/exchange_rates?json";
    public Currency[] getNBU(String currentdateDDpMMpYYYY) throws IOException {
        String urlString = BASE_URL_COURSE_NBU;
        String currentdateYYYYMMDD;
        if(!currentdateDDpMMpYYYY.equals("")) {
            String[] temp = currentdateDDpMMpYYYY.split("\\.");
            currentdateYYYYMMDD = temp[2] + temp[1] + temp[0];
            urlString += "&date=" + currentdateYYYYMMDD;
        }
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        int responseCode = connection.getResponseCode();
        String inputLine;
        StringBuilder response = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } else {
            System.out.println("GET posts request not worked");
        }
        StringReader reader = new StringReader(String.valueOf(response));
        ObjectMapper mapper = new ObjectMapper();
        Currency[] currencies = mapper.readValue(reader, Currency[].class);
        for(Currency currency: currencies){
            currency.setRateBuy(currency.getRateSell());
        }
        return currencies;
    }

    /*
baseCurrency                Базова валюта
currency	                Валюта угоди
saleRateNB/purchaseRateNB	Курс продажу НБУ
saleRate	                Курс продажу ПриватБанку
purchaseRate	            Курс купівлі ПриватБанку
*/
    public Rate[] getPrivat(String currentdateDDpMMpYYYY) throws IOException {
        String urlString = BASE_URL_COURSE_PRIVAT;
        if(currentdateDDpMMpYYYY.equals("")) {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            currentdateDDpMMpYYYY = formatDate.format(today.getTime());
        }
        urlString += "&date=" + currentdateDDpMMpYYYY;
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        int responseCode = connection.getResponseCode();
        String inputLine;
        StringBuilder response = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } else {
            System.out.println("GET posts request not worked");
        }
        StringReader reader = new StringReader(String.valueOf(response));
        ObjectMapper mapper = new ObjectMapper();
        CurrencyPRB currencyPRB = mapper.readValue(reader, CurrencyPRB.class);
        for(Rate a: currencyPRB.exchangeRate){
            a.setDate(currentdateDDpMMpYYYY);
//            System.out.println(a.getCurrency());
        }

        return currencyPRB.exchangeRate;
    }
}
