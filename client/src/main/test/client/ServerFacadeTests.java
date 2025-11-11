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
            facade.logoutUser();
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
