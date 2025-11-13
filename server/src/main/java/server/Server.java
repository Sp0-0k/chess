package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccesser;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import datamodel.GameData;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.ServiceException;
import service.Service;

import java.util.Map;
import java.util.Objects;

public class Server {

    private final Javalin server;
    private final Service userService;
    private final Gson serializer;

    public Server() {
        DataAccesser dataAccess;
        try {
            dataAccess = new SQLDataAccess();
        } catch (DataAccessException e) {
            dataAccess = new MemoryDataAccess();
        }
        this.serializer = new Gson();
        userService = new Service(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));
        server.delete("db", this::delete);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

    }

    //Logout Handler
    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.result("{ }");
        } catch (ServiceException ex) {
            int errorCode = (Objects.equals(ex.getMessage(), "Error: Database Error")) ? 500 : 401;
            sendError(ex.getMessage(), errorCode, ctx);
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
                int errorCode = (Objects.equals(ex.getMessage(), "Error: Database Error")) ? 500 : 401;
                sendError(ex.getMessage(), errorCode, ctx);
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
            var userData = userService.register(username, password, email);
            ctx.status(200).result(serializer.toJson(userData));
        } catch (ServiceException ex) {
            int errorCode = (Objects.equals(ex.getMessage(), "Error: Database Error")) ? 500 : 403;
            sendError(ex.getMessage(), errorCode, ctx);
        }
    }

    //Delete Handler
    private void delete(Context ctx) {
        try {
            userService.clear();
        } catch (ServiceException ex) {
            sendError(ex.getMessage(), 500, ctx);
        }
    }

    //ListGame Handler
    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            GameData[] gamesToReturn = userService.listGames(authToken);
            Map<String, GameData[]> res = Map.of("games", gamesToReturn);
            ctx.result(serializer.toJson(res));
        } catch (ServiceException ex) {
            int errorCode = (Objects.equals(ex.getMessage(), "Error: Database Error")) ? 500 : 401;
            sendError(ex.getMessage(), errorCode, ctx);
        }
    }

    //CreateGame Handler
    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, Map.class);
            if (!req.containsKey("gameName")) {
                sendError("Error: bad request", 400, ctx);
                return;
            }
            var gameID = userService.createGame(req.get("gameName").toString(), authToken);
            var res = Map.of("gameID", gameID);
            ctx.status(200).result(serializer.toJson(res));
        } catch (ServiceException ex) {
            if (Objects.equals(ex.getMessage(), "Error: Database Error")) {
                sendError(ex.getMessage(), 500, ctx);
            } else {
                int errorCode = (Objects.equals(ex.getMessage(), "Error: bad request")) ? 400 : 401;
                sendError(ex.getMessage(), errorCode, ctx);
            }
        }
    }

    //JoinGame Handler
    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, Map.class);
            if (!req.containsKey("gameID") || !req.containsKey("playerColor")) {
                sendError("Error: bad request", 400, ctx);
                return;
            }
            var playerColor = req.get("playerColor").toString();
            if (!(Objects.equals(playerColor, "WHITE")) && !(Objects.equals(playerColor, "BLACK"))) {
                sendError("Error: bad request", 400, ctx);
                return;
            }
            Number idNumber = (Number) req.get("gameID");
            int gameID = idNumber.intValue();
            userService.joinGame(gameID, req.get("playerColor").toString(), authToken);
            ctx.status(200).result("{ }");

        } catch (ServiceException ex) {
            if (Objects.equals(ex.getMessage(), "Error: Database Error")) {
                sendError(ex.getMessage(), 500, ctx);
            } else {
                int errorCode = (Objects.equals(ex.getMessage(), "Error: already taken")) ? 403 : 401;
                sendError(ex.getMessage(), errorCode, ctx);
            }
        }

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
