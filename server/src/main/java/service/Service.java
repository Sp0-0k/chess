package service;

import chess.ChessGame;
import dataaccess.DataAccesser;
import datamodel.*;

import java.util.Objects;
import java.util.Random;

public class Service {
    private final DataAccesser dataAccess;
    private final Random random;

    public Service(DataAccesser dataAccess) {
        random = new Random();
        this.dataAccess = dataAccess;
    }

    public AuthData register(String username, String password, String email) throws ServiceException {
        if (dataAccess.getUser(username) == null) {
            var userToAdd = new UserData(username, password, email);
            createUser(userToAdd);
            var tempAuthData = new AuthData(generateAuthToken(username), username);
            dataAccess.addAuthData(tempAuthData);
            return tempAuthData;
        } else {
            throw new ServiceException("Error: username already taken");
        }
    }

    private String generateAuthToken(String username) {
        return username.concat("AuthToken" + Math.abs(random.nextInt(100000)));
    }


    private void createUser(UserData data) {
        dataAccess.createUser(data);
    }

    public AuthData login(String username, String password) throws ServiceException {
        if (dataAccess.getUser(username) == null) {
            throw new ServiceException("Error: unauthorized");
        } else if (!Objects.equals(dataAccess.getUser(username).password(), password)) {
            throw new ServiceException("Error: unauthorized");
        } else {
            var newAuthData = new AuthData(generateAuthToken(username), username);
            dataAccess.addAuthData(newAuthData);
            return newAuthData;
        }
    }

    public void clear() {
        dataAccess.clear();
    }

    public void logout(String authToken) throws ServiceException {
        if (dataAccess.getAuthData(authToken) != null) {
            var oldAuthData = dataAccess.getAuthData(authToken);
            dataAccess.removeAuthData(oldAuthData);
        } else {
            throw new ServiceException("Error: unauthorized");
        }
    }

    public GameData[] listGames(String authToken) throws ServiceException {
        if (dataAccess.getAuthData(authToken) != null) {
            return dataAccess.getGameData();
        } else {
            throw new ServiceException("Error: unauthorized");
        }
    }


    public int createGame(String gameName, String authToken) throws ServiceException {
        if (dataAccess.getAuthData(authToken) != null) {
            var newGameData = generateGameData(gameName);
            if (dataAccess.getGame(newGameData.gameID()) == null) {
                dataAccess.addGameData(newGameData);
                return newGameData.gameID();
            } else {
                throw new ServiceException("Error: bad request");
            }
        } else {
            throw new ServiceException("Error: unauthorized");
        }
    }

    private GameData generateGameData(String gameName) {
        int gameID = Math.abs(gameName.hashCode() % 9999999);
        return new GameData(gameID, null, null, gameName, new ChessGame());
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ServiceException {
        if (dataAccess.getGame(gameID) == null) {
            throw new ServiceException("Error: bad request");
        }
        if (dataAccess.getAuthData(authToken) == null) {
            throw new ServiceException("Error: unauthorized");
        }
        var usernameToSet = dataAccess.getAuthData(authToken).username();
        var originalGameData = dataAccess.getGame(gameID);
        GameData updatedGame;
        if (Objects.equals(playerColor, "WHITE")) {
            if (originalGameData.whiteUsername() == null) {
                updatedGame = new GameData(gameID, usernameToSet, originalGameData.blackUsername(), originalGameData.gameName(), originalGameData.game());
            } else {
                throw new ServiceException("Error: already taken");
            }
        } else {
            if (originalGameData.blackUsername() == null) {
                updatedGame = new GameData(gameID, originalGameData.whiteUsername(), usernameToSet, originalGameData.gameName(), originalGameData.game());
            } else {
                throw new ServiceException("Error: already taken");
            }
        }
        dataAccess.removeGame(gameID);
        dataAccess.addGameData(updatedGame);

    }
}
