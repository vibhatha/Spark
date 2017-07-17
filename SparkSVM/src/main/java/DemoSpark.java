/**
 * Created by vibhatha on 7/11/17.
 */
import static spark.Spark.*;
public class DemoSpark {
    public static void main(String[] args) {

       get("/hello", (req, res) -> "Hello World");
    }
}
