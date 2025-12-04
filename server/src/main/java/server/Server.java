package server;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccesser;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.websocket.WsMessageContext;
import org.eclipse.jetty.websocket.api.Session;
import service.ServiceException;
import service.Service;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        server.ws("ws", ws -> {
            ws.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket connected");
            });
            ws.onMessage(this::decodeWebsocket);
            ws.onClose(_ -> System.out.println("Websocket closed"));
        });


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


    Map<Integer, Set<Session>> activeGames;

    //WebSocket Handler
    private void decodeWebsocket(WsMessageContext ctx) {
        var userCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (userCommand.getCommandType()) {
            case CONNECT -> wsConnect(userCommand.getGameID(), userCommand.getAuthToken(), ctx.session);
            case MAKE_MOVE -> {
                var moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                wsMove(moveCommand.getGameID(), moveCommand.getAuthToken(), ctx.session, moveCommand.getMove());
            }
            case LEAVE -> wsLeave(userCommand.getGameID(), userCommand.getAuthToken(), ctx.session);
            case RESIGN -> wsResign(userCommand.getGameID(), userCommand.getAuthToken(), ctx.session);
            default -> throw new IllegalStateException("Unexpected value: " + userCommand.getCommandType());
        }
    }

    private GameData getGame(int gameID) {
        try {
            var dataTest = new SQLDataAccess();
            return dataTest.getGame(gameID);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private AuthData getAuthData(String authToken) {
        try {
            var dataTest = new SQLDataAccess();
            return dataTest.getAuthData(authToken);
        } catch (Exception ex) {
            return null;
        }
    }

    private void updateGame(int gameID, GameData data) {
        try {
            var dataTest = new SQLDataAccess();
            dataTest.removeGame(gameID);
            dataTest.addGameData(data);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean validateMove(ChessMove moveToMake, GameData gameData, String playerName) {
        ChessGame currGame = gameData.game();
        var validMoves = currGame.validMoves(moveToMake.getStartPosition());
        var pieceColor = currGame.getBoard().getPiece(moveToMake.getStartPosition()).getTeamColor();
        ChessGame.TeamColor playerColor;
        if (validMoves.contains(moveToMake)) {
            if (Objects.equals(playerName, gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(playerName, gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                return false;
            }
            return pieceColor == playerColor;
        }
        return false;
    }


    private void wsMove(int gameID, String authToken, Session session, ChessMove move) {
        var connections = activeGames.get(gameID);
        if (!connections.contains(session)) {
            return;
        }
        var authData = getAuthData(authToken);
        if (authData != null) {
            var gameData = getGame(gameID);
            if (validateMove(move, gameData, authData.username())) {
                try {
                    gameData.game().makeMove(move);
                    updateGame(gameID, gameData);
                    var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
                    var loadGameJson = new Gson().toJson(loadGame);
                    for (Session connection : connections) {
                        connection.getRemote().sendString(loadGameJson);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

            }

        }
    }

    private void wsConnect(int gameID, String authToken, Session session) {
        try {
            var gameData = getGame(gameID);
            getAuthData(authToken);
            var connectedMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "Test");
            var gameLoad = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
            var notifJson = new Gson().toJson(connectedMessage);
            var gameJson = new Gson().toJson(gameLoad);
            session.getRemote().sendString(gameJson);
            var otherUsers = activeGames.get(gameID);
            for (Session connection : otherUsers) {
                connection.getRemote().sendString(notifJson);
            }
            otherUsers.add(session);
            activeGames.replace(gameID, otherUsers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void wsResign(int gameID, String authToken, Session session) {
    }

    private void wsLeave(int gameID, String authToken, Session session) {
        try {
            var connections = activeGames.get(gameID);
            var playerName = Objects.requireNonNull(getAuthData(authToken)).username();
            var leftMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, playerName + " left");
            var messageJson = new Gson().toJson(leftMessage);
            connections.remove(session);
            activeGames.replace(gameID, connections);
            for (Session connection : connections) {
                connection.getRemote().sendString(messageJson);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
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
