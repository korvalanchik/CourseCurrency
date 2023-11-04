package currencyservice;

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
        RatePRB ratePRB = mapper.readValue(reader, RatePRB.class);
        for(CurrencyPRB a: ratePRB.exchangeCurrencyPRB){
            a.setDate(currentdateDDpMMpYYYY);
        }

        return ratePRB.exchangeCurrencyPRB;
    }

    public static CurrencyMONO[] getMONO(String currentdateDDpMMpYYYY) throws IOException {
//        String urlString = BASE_URL_COURSE_MONO;
//        String currentdateYYYYMMDD;
//        if(!currentdateDDpMMpYYYY.equals("")) {
//            String[] temp = currentdateDDpMMpYYYY.split("\\.");
//            currentdateYYYYMMDD = temp[2] + temp[1] + temp[0];
//            urlString += "&date=" + currentdateYYYYMMDD;
//        }
        URL url = new URL(BASE_URL_COURSE_MONO);

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
        System.out.println(response);
        String resp = "[{\"currencyCodeA\":840,\"currencyCodeB\":980,\"date\":1699090806,\"rateBuy\":36.14,\"rateCross\":0,\"rateSell\":37.2995}]";
        StringReader reader = new StringReader(String.valueOf(response));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, CurrencyMONO[].class);

    }
}
