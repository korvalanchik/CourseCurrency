import java.io.IOException;
import java.util.ArrayList;

public class Demo {
    public static void main(String[] args) throws IOException {
        CourseCurrency cur = new CourseCurrency();
        ArrayList<Currency> listCur = cur.getNBU("");

        for(Currency c:listCur) {
            System.out.println(c);
        }
    }
}
