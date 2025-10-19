package dataaccess;

import datamodel.*;

public interface DataAccesser {

    UserData getUser(String username);

    void createUser(UserData user);

    void clear();

    void addAuthData(AuthData authData);

    void removeAuthData(AuthData authData);

    AuthData getAuthData(String username);

    GameData[] getGameData();

    void addGameData(GameData newGameData);

    GameData getGame(int gameID);
}
