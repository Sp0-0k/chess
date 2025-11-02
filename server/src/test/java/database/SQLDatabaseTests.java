package database;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SQLDatabaseTests {
    private static SQLDataAccess db;
    private static UserData newUser;
    private static AuthData newAuth;
    private static GameData newGame;

    @BeforeEach
    public void setup() throws Exception {
        db = new SQLDataAccess();
        db.clear();
        newUser = new UserData("newUser", "newPass", "newEmail");
        newAuth = new AuthData("ImAToken", "newUser");
        newGame = new GameData(1, "user1", "user2", "testGame", new ChessGame());
    }

    @Test
    public void addUser() throws Exception {
        db.createUser(newUser);
        Assertions.assertEquals(newUser, db.getUser(newUser.username()));
    }

    @Test
    public void addExistingUser() throws Exception {
        db.createUser(newUser);
        Assertions.assertThrows(DataAccessException.class, () -> db.createUser(newUser));
    }

    @Test
    public void findUser() throws Exception {
        db.createUser(newUser);
        var userdata = db.getUser("newUser");
        Assertions.assertEquals(newUser, userdata);
        Assertions.assertNull(db.getUser("fakeUser"));
    }

    @Test
    public void findFakeUser() throws Exception {
        db.createUser(newUser);
        db.createUser(new UserData("test", "pass", "email"));
        var user = db.getUser("*");
        var user2 = db.getUser("notReal");
        Assertions.assertNull(user);
        Assertions.assertNull(user2);
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
        Assertions.assertEquals(newAuth, db.getAuthData(newAuth.authToken()));
    }

    @Test
    void addEmptyAuthData() {
        var nullToken = new AuthData(null, "user1");
        var nullUsername = new AuthData("token", null);
        Assertions.assertThrows(DataAccessException.class, () -> db.addAuthData(null));
        Assertions.assertThrows(DataAccessException.class, () -> db.addAuthData(nullToken));
        Assertions.assertThrows(DataAccessException.class, () -> db.addAuthData(nullUsername));
    }

    @Test
    void findAuthData() throws Exception {
        db.addAuthData(newAuth);
        var returnedAuth = db.getAuthData(newAuth.authToken());
        Assertions.assertNotNull(returnedAuth);
        Assertions.assertEquals(newAuth, returnedAuth);
    }

    @Test
    void findFakeAuthData() throws Exception {
        db.addAuthData(newAuth);
        Assertions.assertNull(db.getAuthData("fakeAuthToken"));
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

    @Test
    void removeEmptyAuthData() {
        Assertions.assertThrows(DataAccessException.class, () -> db.removeAuthData(null));
    }

    @Test
    void addGameData() throws Exception {
        db.addGameData(newGame);
        Assertions.assertEquals(newGame, db.getGame(1));
    }

    @Test
    void findGameData() throws Exception {
        db.addGameData(newGame);
        GameData returnedData = db.getGame(1);
        Assertions.assertEquals(newGame, returnedData);
        Assertions.assertNull(db.getGame(2));
        Assertions.assertEquals(newGame.game(), returnedData.game());
    }

    @Test
    void findAllGames() throws Exception {
        GameData test = new GameData(2, "testW", "testB", "testG", new ChessGame());
        db.addGameData(newGame);
        db.addGameData(test);
        GameData[] returnedData = db.getGameData();
        GameData[] expectedData = new GameData[2];
        expectedData[0] = newGame;
        expectedData[1] = test;
        for (int i = 0; i < returnedData.length; ++i) {
            Assertions.assertEquals(expectedData[i], returnedData[i]);
        }
    }


}
