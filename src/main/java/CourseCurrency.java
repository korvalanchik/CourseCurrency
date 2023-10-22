import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CourseCurrency {
    private static final String BASE_URL_COURSE_NBU = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json&date=";
    private static final String BASE_URL_COURSE_PRIVAT = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    public Currency[] getNBU(String currentdateDDpMMpYYYY) throws IOException {
        String urlString, currentdateYYYYMMDD;
        if(currentdateDDpMMpYYYY != null) {
            // convert DDMMYYYY to YYYYMMDD
            //....
            //....
            urlString = BASE_URL_COURSE_NBU + "&date=" + "20231023";
        } else {
            urlString = BASE_URL_COURSE_NBU;
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
        return mapper.readValue(reader, Currency[].class);
    }

    /*
baseCurrency                Базова валюта
currency	                Валюта угоди
saleRateNB/purchaseRateNB	Курс продажу НБУ
saleRate	                Курс продажу ПриватБанку
purchaseRate	            Курс купівлі ПриватБанку
*/
    public Currency[] getPrivat(String currentdateDDpMMpYYYY) {
        return new Currency[]{new Currency(840, "02.10.2023", 33, 32)};
    }
}
