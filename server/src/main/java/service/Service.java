package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DataAccesser;
import datamodel.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

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
        try {
            if (dataAccess.getUser(username) == null) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                var userToAdd = new UserData(username, hashedPassword, email);
                createUser(userToAdd);
                var authData = new AuthData(generateAuthToken(), username);
                dataAccess.addAuthData(authData);
                return authData;
            } else {
                throw new ServiceException("Error: username already taken");
            }
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }


    private void createUser(UserData data) throws ServiceException {
        try {
            dataAccess.createUser(data);
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }

    public AuthData login(String username, String password) throws ServiceException {
        try {
            if (dataAccess.getUser(username) != null) {
                UserData existingUser = dataAccess.getUser(username);
                if (BCrypt.checkpw(password, existingUser.password())) {
                    var newAuthData = new AuthData(generateAuthToken(), username);
                    dataAccess.addAuthData(newAuthData);
                    return newAuthData;
                }
            }
            throw new ServiceException("Error: unauthorized");
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }

    public void clear() throws ServiceException {
        try {
            dataAccess.clear();
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }

    public void logout(String authToken) throws ServiceException {
        try {
            if (dataAccess.getAuthData(authToken) != null) {
                var oldAuthData = dataAccess.getAuthData(authToken);
                dataAccess.removeAuthData(oldAuthData);
            } else {
                throw new ServiceException("Error: unauthorized");
            }
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }

    public GameData[] listGames(String authToken) throws ServiceException {
        try {
            if (dataAccess.getAuthData(authToken) != null) {
                return dataAccess.getGameData();
            } else {
                throw new ServiceException("Error: unauthorized");
            }
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }


    public int createGame(String gameName, String authToken) throws ServiceException {
        try {
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
        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }

    private GameData generateGameData(String gameName) throws ServiceException {
        if (gameName == null) {
            throw new ServiceException("Error: bad request");
        }
        int gameID = Math.abs(random.nextInt());
        return new GameData(gameID, null, null, gameName, new ChessGame(), false);
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ServiceException {
        try {
            if (dataAccess.getGame(gameID) == null) {
                throw new ServiceException("Error: bad request");
            }
            if (dataAccess.getAuthData(authToken) == null) {
                throw new ServiceException("Error: unauthorized");
            }
            var usernameToSet = dataAccess.getAuthData(authToken).username();
            var baseGameData = dataAccess.getGame(gameID);
            GameData updatedGame;
            if (Objects.equals(playerColor, "WHITE")) {
                if (baseGameData.whiteUsername() == null) {
                    updatedGame = new GameData(gameID, usernameToSet, baseGameData.blackUsername(), baseGameData.gameName(),
                            baseGameData.game(), baseGameData.gameEnded());
                } else {
                    throw new ServiceException("Error: already taken");
                }
            } else {
                if (baseGameData.blackUsername() == null) {
                    updatedGame = new GameData(gameID, baseGameData.whiteUsername(), usernameToSet, baseGameData.gameName(),
                            baseGameData.game(), baseGameData.gameEnded());
                } else {
                    throw new ServiceException("Error: already taken");
                }
            }
            dataAccess.removeGame(gameID);
            dataAccess.addGameData(updatedGame);

        } catch (DataAccessException ex) {
            throw new ServiceException("Error: Database Error");
        }
    }


}
