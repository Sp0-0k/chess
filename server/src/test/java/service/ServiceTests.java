package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import datamodel.*;
import org.junit.jupiter.api.*;

public class ServiceTests {
    private static Service testService;
    private static MemoryDataAccess db;

    @BeforeEach
    public void initialize() {
        db = new MemoryDataAccess();
        testService = new Service(db);
    }


    @Test
    public void registerSuccess() throws Exception {
        var authData = testService.register("testUser", "testPass", "testEmail");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("testUser", authData.username());
        Assertions.assertFalse(authData.authToken().isEmpty());
    }

    @Test
    public void registerExistingUsername() throws Exception {
        testService.register("existingUser", "existingUserPass", "existingUserEmail");
        Assertions.assertThrows(ServiceException.class, () -> testService.register("existingUser", "testPassword", "testEmail"));
    }

    @Test
    public void loginSuccess() throws Exception {
        var startAuthData = testService.register("existingUser", "existingUserPass", "existingUserEmail");
        db.removeAuthData(startAuthData);
        var authData = testService.login("existingUser", "existingUserPass");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("existingUser", authData.username());
        Assertions.assertFalse(authData.authToken().isEmpty());
        Assertions.assertNotNull(db.getAuthData(authData.authToken()));
    }

    @Test
    public void invalidLoginAttempts() {
        Assertions.assertThrows(ServiceException.class, () -> testService.login("fakeUser", "fakePass"));
        Assertions.assertThrows(ServiceException.class, () -> testService.login("existingUser", "fakePassword"));
    }

    @Test
    public void logoutSuccess() throws Exception {
        var existingAuthData = testService.register("existingUser", "existingUserPass", "existingUserEmail");
        Assertions.assertDoesNotThrow(() -> testService.logout(existingAuthData.authToken()));
    }

    @Test
    public void logoutBadToken() {
        Assertions.assertThrows(ServiceException.class, () -> testService.logout("BadToken"));
    }

    @Test
    public void createGameSuccess() throws Exception {
        db.addAuthData(new AuthData("abc", "testUser"));
        var gameID = testService.createGame("testGame", "abc");
        Assertions.assertNotNull(db.getGame(gameID));
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameNoName() {
        db.addAuthData(new AuthData("abc", "testUser"));
        Assertions.assertThrows(ServiceException.class, () -> testService.createGame(null, "abc"));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        var gameToAdd1 = new GameData(1, "a", "b", "game1", new ChessGame());
        var gameToAdd2 = new GameData(2, "c", "d", "game2", new ChessGame());
        db.addGameData(gameToAdd1);
        db.addGameData(gameToAdd2);
        db.addAuthData(new AuthData("abc", "testUser"));

        var gameList = testService.listGames("abc");
        Assertions.assertNotNull(gameList);
        Assertions.assertEquals(1, gameList[0].gameID());
        Assertions.assertEquals(2, gameList[1].gameID());
        Assertions.assertEquals("c", gameList[1].whiteUsername());
    }

    @Test
    public void listGamesBadAuth() {
        Assertions.assertThrows(ServiceException.class, () -> testService.listGames("badAuth"));
    }

    @Test
    public void joinGameSuccess() {
        var gameToAdd1 = new GameData(1, null, "b", "game1", new ChessGame());
        db.addGameData(gameToAdd1);
        db.addAuthData(new AuthData("abc", "testUser"));
        Assertions.assertDoesNotThrow(() -> testService.joinGame(1, "WHITE", "abc"));
        Assertions.assertEquals("testUser", db.getGame(1).whiteUsername());
        Assertions.assertEquals(1, db.getGameData().length);
    }

    @Test
    public void joinGameFull() {
        var gameToAdd1 = new GameData(1, "a", "b", "game1", new ChessGame());
        db.addGameData(gameToAdd1);
        db.addAuthData(new AuthData("abc", "testUser"));
        Assertions.assertThrows(ServiceException.class, () -> testService.joinGame(1, "WHITE", "abc"));
        Assertions.assertEquals(1, db.getGameData().length);
        Assertions.assertEquals("a", db.getGame(1).whiteUsername());
    }


}
