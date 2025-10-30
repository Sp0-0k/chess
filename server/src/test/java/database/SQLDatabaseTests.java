package database;

import chess.ChessGame;
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
        Assertions.assertEquals(newAuth, db.getAuthData(newAuth.authToken()));
    }

    @Test
    void findAuthData() throws Exception {
        db.addAuthData(newAuth);
        var returnedAuth = db.getAuthData(newAuth.authToken());
        Assertions.assertNotNull(returnedAuth);
        Assertions.assertEquals(newAuth, returnedAuth);
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
    void findAllGames() {

    }


}
