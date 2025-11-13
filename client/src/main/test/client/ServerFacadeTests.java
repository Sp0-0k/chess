package client;

import ResponseException.ResponseException;
import ServerFacade.ServerFacade;
import dataaccess.SQLDataAccess;
import org.junit.jupiter.api.*;
import server.Server;
import service.Service;
import datamodel.*;


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
    public void addUserTest() {
        try {
            AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
            Assertions.assertEquals("TestName", returnedData.username());
        } catch (Exception ex) {
            System.out.println("Failed To Add User");
        }
    }

    @Test
    public void loginUserTest() {
        try {
            facade.addUser("TestName", "TestPass", "TestEmail");
            AuthData returnedData = facade.loginUser("TestName", "TestPass");
            Assertions.assertNotNull(returnedData);
            Assertions.assertEquals("TestName", returnedData.username());
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void logoutUserTest() {
        try {
            AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
            facade.logoutUser(returnedData.authToken());
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void createGameTest() {
        try {
            AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
            var gameID = facade.addGame(returnedData.authToken(), "testGame");
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void listGameTest() {
        try {
            AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
            var game1ID = facade.addGame(returnedData.authToken(), "testGame");
            var game2ID = facade.addGame(returnedData.authToken(), "testGame2");
            var game3ID = facade.addGame(returnedData.authToken(), "testGame3");
            var gameList = facade.listGames(returnedData.authToken());
            for (GameData data : gameList) {
                System.out.println(data.gameName());
            }
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void joinGameTest() {
        try {
            AuthData returnedData = facade.addUser("TestName", "TestPass", "TestEmail");
            var game1ID = facade.addGame(returnedData.authToken(), "testGame");
            facade.joinGame(returnedData.authToken(), "WHITE", game1ID);

        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
