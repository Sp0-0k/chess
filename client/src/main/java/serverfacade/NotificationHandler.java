package serverfacade;

public interface NotificationHandler {
    void notify(websocket.messages.ServerMessage notification);
}