import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws IOException {
        CourseCurrency cur = new CourseCurrency();

        for(Currency c:cur.getNBU("")) {
            System.out.println(c);
        }
    }
}
