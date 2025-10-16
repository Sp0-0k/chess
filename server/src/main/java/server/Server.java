package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.ServiceException;
import service.Service;

import java.util.Map;
import java.util.Objects;

public class Server {

    //TODO: Create Service, write two unit tests for every method, one passing on fail

    private final Javalin server;
    private final Service userService;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        userService = new Service(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register); //Can be ctx -> register(ctx)
        server.post("session", this::login);

        //TODO: Register your endpoints and exception handlers here.

    }

    private void login(Context ctx) {
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, Map.class);

            if (req.containsKey("username") && req.containsKey("password")) {

                String username = req.get("username").toString();
                String password = req.get("password").toString();
                var authData = userService.login(username, password);
                ctx.result(serializer.toJson(authData));
            } else {
                var res = Map.of("message", "Error: bad request");
                ctx.status(400);
                ctx.result(serializer.toJson(res));
            }
        } catch (ServiceException ex) {
            var res = Map.of("message", ex.getMessage());
            if (Objects.equals(ex.getMessage(), "Error: bad request")) {
                ctx.status(401);
            } else {
                ctx.status(401);
            }
            var serializer = new Gson();
            ctx.result(serializer.toJson(res));
        }
    }

    //Register Handler
    private void register(Context ctx) {
        try {
            var serializer = new Gson();
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, Map.class);
            if (!req.containsKey("username") || !req.containsKey("password") || !req.containsKey("email")) {
                var res = Map.of("message", "Error: bad request");
                ctx.status(400);
                ctx.result(serializer.toJson(res));
                return;
            }
            String username = req.get("username").toString();
            String password = req.get("password").toString();
            String email = req.get("email").toString();
            var authData = userService.register(username, password, email);
            ctx.status(200).result(serializer.toJson(authData));
        } catch (ServiceException ex) {
            var res = Map.of("message", ex.getMessage());
            ctx.status(403);
            var serializer = new Gson();
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
