package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.BoardCreator;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.net.URI;
import java.util.Map;

public class WebsocketFacade extends Endpoint {
    private BoardCreator viewer;
    private String serverUrl;
    private WebSocketContainer container;
    private Session session;

    public WebsocketFacade(String url) {
        try {
            serverUrl = url;
            URI uri = new URI(serverUrl);
            container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    var decoded = new Gson().fromJson(message, Map.class);
                    String type = decoded.get("serverMessageType").toString();
                    switch (type) {
                        case "LOAD_GAME":
                            var gameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            var gameState = gameMessage.getGame();
                            viewer = new BoardCreator(gameState, " ");
                            viewer.drawBoard();
                            break;
//                        case "ERROR" -> decodeTo = ServerMessage.ServerMessageType.ERROR;
//                        default -> decodeTo = ServerMessage.ServerMessageType.NOTIFICATION;
                    }

                }
            });
        } catch (Exception ex) {
            System.out.println("There was an error connection to the chess ws server");
        }
    }

    public void wsConnect(String authToken, int gameID) {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        var json = new Gson().toJson(command);
        try {
            session.getBasicRemote().sendText(json);
        } catch (Exception ex) {
            System.out.println("Error: error connecting to ws server");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
