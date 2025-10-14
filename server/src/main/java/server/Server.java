package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    //TODO: Create Service, write two unit tests for every method, one passing on fail

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register); //Can be ctx -> register(ctx)
        server.post("session", this::login);

        //TODO: Register your endpoints and exception handlers here.

    }

    private void login(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        //TODO: Calls to the service and get authToken
        if (req.containsValue("username") && req.containsValue("password")) {


            // TODO: Call to the service and register


            //FIXME: Generate authToken from service not hardcoded
            var res = Map.of("username", req.get("username"), "authToken", "yzx");
            ctx.result(serializer.toJson(res));
        } else {
            var res = Map.of("message", "Error: bad request");
            ctx.status(400);
            ctx.result(serializer.toJson(res));
        }
    }

    //Register Handler
    private void register(Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        if (req.containsValue("username") && req.containsValue("password")) {


            // TODO: Call to the service and register


            //FIXME: Generate authToken from service not hardcoded
            var res = Map.of("username", req.get("username"), "authToken", "yzx");
            ;
            ctx.result(serializer.toJson(res));
        } else {
            var res = Map.of("message", "Error: unauthorized");
            ctx.status(400);
            ctx.result(serializer.toJson(res));
        }
    }


    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
