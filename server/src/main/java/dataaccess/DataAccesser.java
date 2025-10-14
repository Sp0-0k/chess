package dataaccess;

import datamodel.*;

public interface DataAccesser {

    UserData getUser(String username);

    void createUser(UserData user);

    void clear();

    void addAuthData(AuthData authData);
}
