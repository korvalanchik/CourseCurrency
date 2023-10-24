import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException {
        CourseCurrency cur = new CourseCurrency();

        for(CurrencyNBU c:cur.getNBU("23.10.2023")) {
            System.out.println(c);
        }
        for(CurrencyPRB cc : cur.getPrivat("23.10.2023")) {
            System.out.println(cc);
        }

    }
}
