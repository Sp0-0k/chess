package dataaccess;

import datamodel.*;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccesser {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> authTokens = new HashMap<>();
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
    }

    @Override
    public void addAuthData(AuthData authData) {
        authTokens.put(authData.username(), authData);
    }
}
