package dataaccess;

import datamodel.*;

public interface DataAccesser {

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    void clear() throws DataAccessException;

    void addAuthData(AuthData authData) throws DataAccessException;

    void removeAuthData(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String username) throws DataAccessException;

    GameData[] getGameData() throws DataAccessException;

    void addGameData(GameData newGameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void removeGame(int gameID) throws DataAccessException;
}
