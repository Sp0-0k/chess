package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.ResultSet;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccesser {

    public SQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        setupTables();
    }

    private void resetDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
            setupTables();
        } catch (Exception exception) {
            throw new DataAccessException("There was an issue with the database");
        }
    }

    static String[] tables = {
            """
                        CREATE TABLE  IF NOT EXISTS userData (
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        PRIMARY KEY (username)
                    )""",

            """
                    CREATE TABLE  IF NOT EXISTS gameData (
                        gameID INT NOT NULL AUTO_INCREMENT,
                        whiteUsername VARCHAR(255),
                        blackUsername VARCHAR(255),
                        gameName VARCHAR(255) NOT NULL,
                        game TEXT NOT NULL,
                        PRIMARY KEY (gameID)
                    )""",

            """
                    CREATE TABLE  IF NOT EXISTS authData (
                        authToken VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        PRIMARY KEY (authToken)
                    )"""

    };

    private void setupTables() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            for (var table : tables) {
                try (var createTable = conn.prepareStatement(table)) {
                    createTable.executeUpdate();
                } catch (Exception ex) {
                    throw new DataAccessException("Failed to setup tables");
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Failed to setup tables");

        }
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var findUser = conn.prepareStatement("SELECT * FROM userData WHERE username = ?");
            findUser.setString(1, username);
            try (var response = findUser.executeQuery()) {
                if (response.next()) {
                    String resName = response.getString("username");
                    String resPass = response.getString("password");
                    String resEmail = response.getString("email");
                    return new UserData(resName, resPass, resEmail);
                }
                return null;
            }
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with finding a user in the database");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var addUser = conn.prepareStatement("INSERT INTO userData (username, password, email) VALUES (?, ?, ?)");
            addUser.setString(1, user.username());
            addUser.setString(2, user.password());
            addUser.setString(3, user.email());
            addUser.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with creating a user in the database");
        }

    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var clearDatabase = conn.prepareStatement("DROP DATABASE chess");
            clearDatabase.executeUpdate();
            resetDatabase();
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with clearing the database");
        }
    }

    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var addAuthData = conn.prepareStatement("INSERT INTO authData (authToken, username) VALUES (?, ?)");
            addAuthData.setString(1, authData.authToken());
            addAuthData.setString(2, authData.username());
            addAuthData.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with adding authData to the database");
        }
    }

    @Override
    public void removeAuthData(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var removeAuth = conn.prepareStatement("DELETE FROM authData WHERE authToken = ?");
            removeAuth.setString(1, authData.authToken());
            removeAuth.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with removing authData from the database");
        }

    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var findAuth = conn.prepareStatement("SELECT * FROM authData WHERE authToken = ?");
            findAuth.setString(1, authToken);
            try (var response = findAuth.executeQuery()) {
                if (response.next()) {
                    String resToken = response.getString("authToken");
                    String resName = response.getString("username");
                    return new AuthData(resToken, resName);
                }
                return null;
            }
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with the database");
        }
    }

    @Override
    public GameData[] getGameData() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var findGame = conn.prepareStatement("SELECT * FROM gameData");
            try (var response = findGame.executeQuery()) {
                while (response.next()) {
                    result.add(readGameData(response));
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with the database");
        }
        return result.toArray(new GameData[0]);
    }

    private GameData readGameData(ResultSet rs) throws Exception {
        var gameID = rs.getInt("gameID");
        var whiteUser = rs.getString("whiteUsername");
        var blackUser = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(gameID, whiteUser, blackUser, gameName, game);

    }

    @Override
    public void addGameData(GameData newGameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var addGameData = conn.prepareStatement("INSERT INTO gameData (gameID, whiteUsername, blackUsername," +
                    "gameName, game) VALUES (?, ?, ?, ?, ?)");
            addGameData.setInt(1, newGameData.gameID());
            addGameData.setString(2, newGameData.whiteUsername());
            addGameData.setString(3, newGameData.blackUsername());
            addGameData.setString(4, newGameData.gameName());
            String json = new Gson().toJson(newGameData.game());
            addGameData.setString(5, json);

            addGameData.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with adding gameData to the database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var findGame = conn.prepareStatement("SELECT * FROM gameData WHERE gameID = ?");
            findGame.setInt(1, gameID);
            try (var response = findGame.executeQuery()) {
                if (response.next()) {
                    return readGameData(response);
                }
                return null;
            }
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with the database");
        }
    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var removeAuth = conn.prepareStatement("DELETE FROM gameData WHERE gameID = ?");
            removeAuth.setInt(1, gameID);
            removeAuth.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with removing authData from the database");
        }
    }
}
