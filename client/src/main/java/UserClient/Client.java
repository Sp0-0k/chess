package UserClient;

import java.util.Scanner;

import ResponseException.ResponseException;
import ServerFacade.ServerFacade;
import datamodel.AuthData;
import ui.*;

public class Client {

    private boolean loggedIn;
    private ServerFacade facade;
    private String authToken;

    public void run() {
        facade = new ServerFacade("http://localhost:8080");
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
        System.out.println("Welcome to Kirk's Chess App. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equalsIgnoreCase("quit")) {
            String line = scanner.nextLine();
            try {
                result = parseLine(line);
            } catch (Exception ex) {
                var msg = ex.toString();
                System.out.println(msg);
            }
        }

    }

    private String parseLine(String line) {
        String[] tokens = line.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        switch (cmd) {
            case "login" -> signIn(tokens);
            case "register" -> registerUser(tokens);
            case "logout" -> logoutUser();
            case "create" -> createGame(tokens);
            case "list" -> listGames();
            case "join" -> joinGame(tokens);
            case "observe" -> observeGame(tokens);
            case "quit" -> loggedIn = false;
            default -> help();
        }
        return cmd;


    }

    private void help() {
        String registerInfo = "register <USERNAME> <PASSWORD> <EMAIL> --- create an account \n";
        String loginInfo = "login <USERNAME> <PASSWORD> --- login to you chess account\n";
        String quitInfo = "quit --- stop playing chess\n";
        String helpInfo = "help --- view possible commands\n";
        String createInfo = "create <NAME> --- create a new game\n";
        String listInfo = "list --- list all games\n";
        String joinInfo = "join <ID> [WHITE|BLACK] --- join a created game\n";
        String observeInfo = "observe <ID> --- watch a game\n";
        String logoutInfo = "logout --- logout of your account\n";

        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        if (!loggedIn) {
            System.out.print(registerInfo);
            System.out.print(loginInfo);
        } else {
            System.out.print(createInfo);
            System.out.print(listInfo);
            System.out.print(joinInfo);
            System.out.print(observeInfo);
            System.out.print(logoutInfo);

        }
        System.out.print(quitInfo);
        System.out.print(helpInfo);
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);

    }

    private void observeGame(String[] tokens) {
    }

    private void joinGame(String[] tokens) {
    }

    private void listGames() {
    }

    private void createGame(String[] tokens) {
    }

    private void logoutUser() {
        try {
            facade.logoutUser(authToken);
            loggedIn = false;
            authToken = "";
        } catch (ResponseException ex) {
            System.out.println("There was an error: \n" + ex.getMessage());
        }
    }

    private void registerUser(String[] tokens) {
        if (tokens.length == 4) {
            var username = tokens[1];
            var password = tokens[2];
            var email = tokens[3];
            try {
                AuthData auth = facade.addUser(username, password, email);
                authToken = auth.authToken();
            } catch (ResponseException ex) {
                System.out.println("There was an error: \n" + ex.getMessage());
            }
        } else {
            System.out.println("Incorrect number of arguments");
        }
    }

    private void signIn(String[] tokens) {
        if (tokens.length == 3) {
            var username = tokens[1];
            var password = tokens[2];
            try {
                AuthData auth = facade.loginUser(username, password);
                authToken = auth.authToken();
                loggedIn = true;
            } catch (ResponseException ex) {
                if (ex.code() == 401) {
                    System.out.println("There was an error with your credentials \n");
                }
                System.out.println("There was an error with logging in " + ex.getMessage());
            }
        }
    }


}
