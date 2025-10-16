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
    private final Gson serializer;

    public Server() {
        var dataAccess = new MemoryDataAccess();
        this.serializer = new Gson();
        userService = new Service(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::delete);
        server.post("user", this::register); //Can be ctx -> register(ctx)
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);

        //TODO: Register your endpoints and exception handlers here.

    }

    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.result("{ }");
        } catch (ServiceException ex) {
            sendError(ex.getMessage(), 401, ctx);

        }
    }

    //Login Handler
    private void login(Context ctx) {
        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, Map.class);

            if (req.containsKey("username") && req.containsKey("password")) {

                String username = req.get("username").toString();
                String password = req.get("password").toString();
                var authData = userService.login(username, password);
                ctx.result(serializer.toJson(authData));
            } else {
                sendError("Error: bad request", 400, ctx);
            }
        } catch (ServiceException ex) {
            var res = Map.of("message", ex.getMessage());
            if (Objects.equals(ex.getMessage(), "Error: bad request")) {
                sendError("Error: bad request", 400, ctx);
            } else {
                sendError(ex.getMessage(), 401, ctx);
            }
            ctx.result(serializer.toJson(res));
        }
    }

    //Register Handler
    private void register(Context ctx) {
        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, Map.class);
            if (!req.containsKey("username") || !req.containsKey("password") || !req.containsKey("email")) {
                sendError("Error: bad request", 400, ctx);
                return;
            }
            String username = req.get("username").toString();
            String password = req.get("password").toString();
            String email = req.get("email").toString();
            var authData = userService.register(username, password, email);
            ctx.status(200).result(serializer.toJson(authData));
        } catch (ServiceException ex) {
            sendError(ex.getMessage(), 403, ctx);
        }
    }

    private void delete(Context ctx) {
        userService.clear();
    }

    private void listGames(Context ctx) {

    }


    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }


    private void sendError(String message, int errorCode, Context ctx) {
        var res = Map.of("message", message);
        ctx.status(errorCode).result(serializer.toJson(res));
    }
}
