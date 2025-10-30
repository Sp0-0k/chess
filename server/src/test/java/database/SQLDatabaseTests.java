package database;

import dataaccess.SQLDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SQLDatabaseTests {
    private static SQLDataAccess db;
    private static UserData newUser;
    private static AuthData newAuth;

    @BeforeEach
    public void setup() throws Exception {
        db = new SQLDataAccess();
        db.clear();
        newUser = new UserData("newUser", "newPass", "newEmail");
        newAuth = new AuthData("ImAToken", "newUser");
    }

    @Test
    public void addUser() throws Exception {
        db.createUser(newUser);
    }

    @Test
    public void findUser() throws Exception {
        db.createUser(newUser);
        var userdata = db.getUser("newUser");
        Assertions.assertEquals(newUser, userdata);
        Assertions.assertNull(db.getUser("fakeUser"));
    }

    @Test
    public void clear() throws Exception {
        db.createUser(newUser);
        db.createUser(new UserData("testCase", "testPass", "testEmail"));
        db.clear();
        Assertions.assertNull(db.getUser("testCase"));
        Assertions.assertNull(db.getUser("newUser"));
    }

    @Test
    void addAuthData() throws Exception {
        db.addAuthData(newAuth);
    }

    @Test
    void findAuthData() throws Exception {
        db.addAuthData(newAuth);
        var returnedAuth = db.getAuthData(newAuth.authToken());
        Assertions.assertNotNull(returnedAuth);
        Assertions.assertEquals(newAuth, returnedAuth);
    }

    @Test
    void removeAuthData() throws Exception {
        var testAuth = new AuthData("testToken", "testUser");
        db.addAuthData(newAuth);
        db.addAuthData(testAuth);
        db.removeAuthData(newAuth);
        Assertions.assertNull(db.getAuthData(newAuth.authToken()));
        Assertions.assertNotNull(db.getAuthData(testAuth.authToken()));
        db.removeAuthData(testAuth);
        Assertions.assertNull(db.getAuthData(testAuth.authToken()));
    }
}
