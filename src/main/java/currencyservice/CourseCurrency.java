package currencyservice;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static connection.ConnectionAPI.connectionTo;

public class CourseCurrency {
    private static final String BASE_URL_COURSE_NBU = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json&date=";
    private static final String BASE_URL_COURSE_PRIVAT = "https://api.privatbank.ua/p24api/exchange_rates?json";
    private static final String BASE_URL_COURSE_MONO = "https://api.monobank.ua/bank/currency";
    public static CurrencyNBU[] getNBU(String currentdateDDpMMpYYYY) throws IOException {
        String urlString = BASE_URL_COURSE_NBU;
        String currentdateYYYYMMDD;
        if(!currentdateDDpMMpYYYY.equals("")) {
            String[] temp = currentdateDDpMMpYYYY.split("\\.");
            currentdateYYYYMMDD = temp[2] + temp[1] + temp[0];
            urlString += "&date=" + currentdateYYYYMMDD;
        }
        URL url = new URL(urlString);

        StringReader reader = new StringReader(connectionTo(url));
        ObjectMapper mapper = new ObjectMapper();
        CurrencyNBU[] currencies = mapper.readValue(reader, CurrencyNBU[].class);
        for(CurrencyNBU currency: currencies){
            currency.setRateBuy(currency.getRateSell());
        }
        return currencies;
    }

     public static CurrencyPRB[] getPrivat(String currentdateDDpMMpYYYY) throws IOException {
        String urlString = BASE_URL_COURSE_PRIVAT;
        if(currentdateDDpMMpYYYY.equals("")) {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
            currentdateDDpMMpYYYY = formatDate.format(today.getTime());
        }
        urlString += "&date=" + currentdateDDpMMpYYYY;
        URL url = new URL(urlString);

        StringReader reader = new StringReader(connectionTo(url));
        ObjectMapper mapper = new ObjectMapper();
        RatePRB ratePRB = mapper.readValue(reader, RatePRB.class);
        for(CurrencyPRB a: ratePRB.exchangeCurrencyPRB){
            a.setDate(currentdateDDpMMpYYYY);
        }

        return ratePRB.exchangeCurrencyPRB;
    }

    public static CurrencyMONO[] getMONO(String currentdateDDpMMpYYYY) throws IOException {
        URL url = new URL(BASE_URL_COURSE_MONO);
        String con = connectionTo(url);
        StringReader reader = new StringReader(con);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, CurrencyMONO[].class);
    }
}
