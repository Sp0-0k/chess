package dataaccess;

import datamodel.*;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccesser {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authTokens = new HashMap<>();
    private final HashMap<Integer, GameData> gameList = new HashMap<>();
    //Might not be correct implementation

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void clear() {
        users.clear();
        authTokens.clear();
        gameList.clear();
    }

    @Override
    public void addAuthData(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public void removeAuthData(AuthData authData) {
        authTokens.remove(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public GameData[] getGameData() {
        return gameList.values().toArray(new GameData[0]);
    }

    @Override
    public void addGameData(GameData newGameData) {
        gameList.put(newGameData.gameID(), newGameData);
    }

    @Override
    public GameData getGame(int gameID) {
        if (gameList.containsKey(gameID)) {
            return gameList.get(gameID);
        }
        return null;
    }

    @Override
    public void removeGame(int gameID) {
        gameList.remove(gameID);
    }
}
