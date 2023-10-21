import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Builder;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CourseCurrency {
    private static final Gson GSON = new Gson();
    private static final String URL_COURSE_NBU = "https://bank.gov.ua/NBU_Exchange/exchange?json";
    public List<Currency> getNBU(String currentdate) throws IOException {
        URL url = new URL(URL_COURSE_NBU);
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
        List<Currency> currencyList = GSON.fromJson(reader, new TypeToken<List<Currency>>(){}.getType());
//        System.out.println(currencyList);
        return currencyList;
    }
}
