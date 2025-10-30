package database;

import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import datamodel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SQLDatabaseTests {
    private static SQLDataAccess db;
    private static UserData newUser;

    @BeforeEach
    public void setup() throws Exception {
        db = new SQLDataAccess();
        db.clear();
        newUser = new UserData("newUser", "newPass", "newEmail");
    }

    @Test
    public void addUser() {
        db.createUser(newUser);
    }

    @Test
    public void findUser() {
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

}
