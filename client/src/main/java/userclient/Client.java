package userclient;

import java.util.Scanner;

import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import serverfacade.ServerFacade;
import datamodel.*;
import serverfacade.WebsocketFacade;
import ui.*;

public class Client {

    private boolean loggedIn;
    private ServerFacade facade;
    private WebsocketFacade wsFacade;
    private String authToken;
    private GameData[] lastPulledGameList;
    private BoardCreator viewer;
    private String playerName;

    public void run() {
        facade = new ServerFacade("http://localhost:8080");
        wsFacade = new WebsocketFacade("ws://localhost:8080/ws");
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
        String joinInfo = "join [WHITE|BLACK] <ID> --- join a created game\n";
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
        if (!loggedIn) {
            return;
        }
        try {
            if (tokens.length == 2) {
                int idToCheck = Integer.parseInt(tokens[1]) - 1;
                if (idToCheck >= lastPulledGameList.length || idToCheck < 0) {
                    System.out.println("Sorry that's not a valid game ID");
                    return;
                }
                wsFacade.wsConnect(authToken, lastPulledGameList[idToCheck].gameID(), playerName);
                boolean connected = true;
                Scanner scanner = new Scanner(System.in);
                while (connected) {
                    String line = scanner.nextLine();
                    if (line.equalsIgnoreCase("leave")) {
                        connected = false;
                    } else {
                        System.out.println("Enter 'leave' to stop watching");
                    }
                }
            } else {
                System.out.println("Incorrect number of arguments");
            }
        } catch (RuntimeException ex) {
            System.out.println("There was an error: ");
        }
    }

    private void joinGame(String[] tokens) {
        if (!loggedIn) {
            return;
        }
        try {
            if (tokens.length == 3) {
                tokens[1] = tokens[1].toUpperCase();
                if (!tokens[1].equals("WHITE") && !tokens[1].equals("BLACK")) {
                    System.out.println("Sorry, your team color couldn't be read");
                    return;
                }
                int idToCheck = Integer.parseInt(tokens[2]) - 1;
                if (idToCheck >= lastPulledGameList.length || idToCheck < 0) {
                    System.out.println("Sorry that's not a valid game ID");
                    return;
                }
                int gameID = lastPulledGameList[idToCheck].gameID();
                facade.joinGame(authToken, tokens[1], gameID);
                wsFacade.wsConnect(authToken, gameID, playerName);
                boolean connected = true;
                Scanner scanner = new Scanner(System.in);
                while (connected) {
                    String line = scanner.nextLine();
                    if (line.equalsIgnoreCase("leave")) {
                        leaveGame();
                        connected = false;
                    } else {
                        parseGameCommands(line);
                    }
                }
            } else {
                System.out.println("Incorrect number of arguments");
            }
        } catch (ResponseException ex) {
            if (ex.code() == 401) {
                System.out.println("It seems like you're login is no longer valid, please log in and try again");
                loggedIn = false;
            } else if (ex.code() == 403) {
                System.out.println("The player color you selected is already in use in that game, please try again");
            } else {
                System.out.println("There was an error joining that game, please try again");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid gameID, expecting a number");
        }
    }

    private void parseGameCommands(String line) {
        String[] tokens = line.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        switch (cmd) {
            case "resign" -> resignGame();
            case "move" -> moveGame(tokens);
            case "show" -> showGame(tokens);
            case "redraw" -> redrawGame();
            default -> gameHelp();
        }

    }


    private void leaveGame() {
        wsFacade.wsLeave(authToken, wsFacade.gameState.gameID());
        System.out.println("You've left the game");
    }

    private void resignGame() {
        wsFacade.wsResign(authToken, wsFacade.gameState.gameID());
    }

    private void moveGame(String[] tokens) {
        if (tokens.length != 3) {
            System.out.println("Unexpected number of arguments, please try again");
            return;
        }

        String startPos = tokens[1].toLowerCase();
        String endPos = tokens[2].toLowerCase();
        if (!startPos.matches("^[a-h][1-8]$") || !endPos.matches("^[a-h][1-8]$")) {
            System.out.println("Invalid location, format your location like this: [letter][number]");
            return;
        }
        int startCol = startPos.charAt(0) - 'a' + 1;
        int startRow = startPos.charAt(1) - '0';
        int endCol = endPos.charAt(0) - 'a' + 1;
        int endRow = endPos.charAt(1) - '0';

        var start = new ChessPosition(startRow, startCol);
        var end = new ChessPosition(endRow, endCol);
        ChessMove moveToTry = new ChessMove(start, end, null);
        var game = wsFacade.gameState;
        wsFacade.wsMakeMove(authToken, game.gameID(), moveToTry);
    }

    private void redrawGame() {
        viewer = new BoardCreator(wsFacade.gameState, playerName);
        viewer.drawBoard();
    }

    private void showGame(String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Unexpected number of arguments, please try again");
            return;
        }
        String locationToCheck = tokens[1].toLowerCase();
        if (!locationToCheck.matches("^[a-h][1-8]$")) {
            System.out.println("Invalid location, format your location like this: [letter][number]");
            return;
        }
        int col = locationToCheck.charAt(0) - 'a' + 1;
        int row = locationToCheck.charAt(1) - '0';
        var posToCheck = new ChessPosition(row, col);
        var currGame = wsFacade.gameState.game();
        var listOfMoves = currGame.validMoves(posToCheck);
        viewer = new BoardCreator(wsFacade.gameState, playerName);
        viewer.drawBoard(listOfMoves, posToCheck);
    }

    private void gameHelp() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        System.out.println("Here's what you can do in game:");
        System.out.println("leave -- exit the game");
        System.out.println("resign -- resign from the match");
        System.out.println("move [start] [end]-- make a move uses letter then number format");
        System.out.println("show [start] -- shows all valid moves for the piece");
        System.out.println("redraw -- redraws the board for you");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE);
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);

    }

    private void listGames() {
        if (loggedIn) {
            try {
                lastPulledGameList = facade.listGames(authToken);
                for (int i = 0; i < lastPulledGameList.length; ++i) {
                    var curGame = lastPulledGameList[i];
                    System.out.println(curGame.gameName() + "    Game Number: " + (i + 1) + "   WhitePlayer: "
                            + curGame.whiteUsername() + "   BlackPlayer: " + curGame.blackUsername());
                }
                if (lastPulledGameList.length == 0) {
                    System.out.println("Couldn't find any games");
                }
            } catch (ResponseException ex) {
                if (ex.code() == 401) {
                    System.out.println("It seems like you're login is no longer valid, please log in and try again");
                    loggedIn = false;
                } else {
                    System.out.println("There was an error getting the game, please try again");
                }
            }
        }
    }

    private void createGame(String[] tokens) {
        if (loggedIn) {
            try {
                if (tokens.length == 2) {
                    facade.addGame(authToken, tokens[1]);
                    System.out.println("Created the game!");
                } else {
                    System.out.println("Incorrect number of arguments");
                }
            } catch (ResponseException ex) {
                if (ex.code() == 401) {
                    System.out.println("It seems like you're login is no longer valid, please log in and try again");
                    loggedIn = false;
                } else {
                    System.out.println("There was and error creating your game, please try again");
                }
            }
        }
    }

    private void logoutUser() {
        if (loggedIn) {
            try {
                facade.logoutUser(authToken);
                loggedIn = false;
                playerName = "";
                authToken = "";
                System.out.println("Logged out");
            } catch (ResponseException ex) {
                System.out.println("There was an error logging you out, please try again");
            }
        }
    }

    private void registerUser(String[] tokens) {
        if (loggedIn) {
            System.out.println("You're already logged in, try a different command");
            return;
        }
        if (tokens.length == 4) {
            var username = tokens[1];
            var password = tokens[2];
            var email = tokens[3];
            try {
                AuthData auth = facade.addUser(username, password, email);
                authToken = auth.authToken();
                loggedIn = true;
                playerName = username;
                System.out.println("All logged in!");
            } catch (ResponseException ex) {
                if (ex.code() == 403) {
                    System.out.println("Sorry, that username is already taken");
                } else {
                    System.out.println("There was an error logging you in, please try again");
                }
            }
        } else {
            System.out.println("Incorrect number of arguments");
        }
    }

    private void signIn(String[] tokens) {
        if (loggedIn) {
            System.out.println("You're already signed in, try a different command");
            return;
        }
        if (tokens.length == 3) {
            var username = tokens[1];
            var password = tokens[2];
            try {
                AuthData auth = facade.loginUser(username, password);
                authToken = auth.authToken();
                loggedIn = true;
                playerName = username;
                System.out.println("All logged in!");
            } catch (ResponseException ex) {
                if (ex.code() == 401) {
                    System.out.println("There was an error with your credentials");
                } else {
                    System.out.println("There was an error with logging in, please try again");
                }
            }
        }
    }


}
