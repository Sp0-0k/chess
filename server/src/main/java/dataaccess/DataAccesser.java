package dataaccess;

import datamodel.*;

public interface DataAccesser {

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    void clear() throws DataAccessException;

    void addAuthData(AuthData authData) throws DataAccessException;

    void removeAuthData(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String username) throws DataAccessException;

    GameData[] getGameData();

    void addGameData(GameData newGameData);

    GameData getGame(int gameID);

    void removeGame(int gameID);
}
