package client;

import exception.ResponseException;
import serverfacade.ServerFacade;
import dataaccess.SQLDataAccess;
import org.junit.jupiter.api.*;
import server.Server;
import service.Service;
import datamodel.*;

import java.util.Map;


public class ServerFacadeTests {

    private static Server server;
    public static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setup() {
        try {
            var service = new Service(new SQLDataAccess());
            service.clear();
        } catch (Exception ex) {
            System.out.println("Failed to clear MYSQL");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void addUserTest() throws ResponseException {
        AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
        Assertions.assertEquals("TestName", returnedData.username());
    }

    @Test
    public void addExistingUserTest() throws ResponseException {
        facade.addUser("TestName", "TestPass", "TestEmail");
        Assertions.assertThrows(ResponseException.class, () ->
                facade.addUser("TestName", "TestPass", "TestEmail"));
    }

    @Test
    public void loginUserTest() throws ResponseException {
        facade.addUser("TestName", "TestPass", "TestEmail");
        AuthData returnedData = facade.loginUser("TestName", "TestPass");
        Assertions.assertNotNull(returnedData);
        Assertions.assertEquals("TestName", returnedData.username());
    }

    @Test
    public void loginMissingUser() {
        Assertions.assertThrows(ResponseException.class, () -> facade.loginUser("BadUser", "TestPass"));
    }

    @Test
    public void logoutUserTest() throws ResponseException {
        AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
        Assertions.assertDoesNotThrow(() -> facade.logoutUser(returnedData.authToken()));
    }

    @Test
    public void logoutUserNoAuth() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logoutUser("NotARealToken"));
    }

    @Test
    public void createGameTest() throws ResponseException {
        AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
        var gameID = facade.addGame(returnedData.authToken(), "testGame");
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameWithoutLogin() {
        Assertions.assertThrows(ResponseException.class, () -> facade.addGame("FakeToken", "testGame"));
    }

    @Test
    public void listGameTest() throws ResponseException {
        AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
        var game1ID = facade.addGame(returnedData.authToken(), "testGame");
        var game2ID = facade.addGame(returnedData.authToken(), "testGame2");
        var game3ID = facade.addGame(returnedData.authToken(), "testGame3");
        GameData[] gameList = facade.listGames(returnedData.authToken());
        var expectedGames = Map.of("testGame", game1ID, "testGame2", game2ID, "testGame3", game3ID);
        for (GameData data : gameList) {
            var expectedID = expectedGames.get(data.gameName());
            Assertions.assertEquals(expectedID, data.gameID());
        }
    }

    @Test
    public void listGameWithoutLogin() {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("fakeToken"));
    }

    @Test
    public void joinGameTest() throws ResponseException {
        AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
        var game1ID = facade.addGame(returnedData.authToken(), "testGame");
        facade.joinGame(returnedData.authToken(), "WHITE", game1ID);
        var gamesList = facade.listGames(returnedData.authToken());
        Assertions.assertEquals("TestName", gamesList[0].whiteUsername());
    }

    @Test
    public void joinFullGame() throws ResponseException {
        AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
        var token = returnedData.authToken();
        var game1ID = facade.addGame(returnedData.authToken(), "testGame");
        facade.joinGame(token, "WHITE", game1ID);
        facade.joinGame(token, "BLACK", game1ID);
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(token, "WHITE", game1ID));
    }

}
