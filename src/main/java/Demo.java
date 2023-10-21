import java.io.IOException;
import java.util.List;

public class Demo {
    public static void main(String[] args) throws IOException {
        CourseCurrency cur = new CourseCurrency();
        List<Currency> listCur = cur.getNBU("121212");
        for(Currency c:listCur) {
            System.out.println(c.toString());
        }
    }
}
