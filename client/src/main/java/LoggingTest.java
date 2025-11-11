import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingTest {

    public static void main(String[] args) throws Exception {
        //Args given is = cow rat dog
        Logger logger = Logger.getLogger("myLogger");
        var handler = new FileHandler("example.log", true);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
        logger.info("main: " + String.join(", ", args));
        logger.severe("the bombs are dropping");
    }
}
