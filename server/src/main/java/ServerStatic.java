import com.google.gson.Gson;
import io.javalin.Javalin;

import java.util.List;
import java.util.Map;


public class ServerStatic {
    public static void main(String[] args) {
        gsonExample();
        //Javalin.create().get("hello", ctx -> ctx.result("Hello!")).start(8080);
    }

    public static void gsonExample() {
        var serializer = new Gson();

        var obj = Map.of(
                "name", "Kirk",
                "year", 2025,
                "pets", List.of("Stormy", "Hobbes")
        );
        var json = serializer.toJson(obj);
        System.out.println("Json: " + json);

        var objFromJson = serializer.fromJson(json, Map.class);
        System.out.println("Object: " + objFromJson);
    }


}
