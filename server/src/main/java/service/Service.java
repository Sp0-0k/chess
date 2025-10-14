package service;

import dataaccess.DataAccesser;
import datamodel.*;

public class Service {
    private final DataAccesser dataAccess;

    public Service(DataAccesser dataAccess) {

        this.dataAccess = dataAccess;
    }

    public AuthData register(String username, String password, String email) throws AlreadyTakenException {
        if (dataAccess.getUser(username) == null) {
            var userToAdd = new UserData(username, password, email);
            createUser(userToAdd);
            //TODO: Add generateAuthToken();
            var tempAuthData = new AuthData("xyz", username);
            dataAccess.addAuthData(tempAuthData);
            return tempAuthData;
        } else {
            throw new AlreadyTakenException("Error: username already take");
        }
    }

    private void generateAuthToken() {
    }


    public void createUser(UserData data) {
        //dataAccess.addUserData(data);
    }
}
