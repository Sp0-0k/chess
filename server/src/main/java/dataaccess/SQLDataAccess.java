package dataaccess;

import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

public class SQLDataAccess implements DataAccesser {

    public SQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        setupTables();
    }

    private void resetDatabase() {
        try {
            DatabaseManager.createDatabase();
            setupTables();
        } catch (Exception exception) {
            exception.printStackTrace();
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
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("There was an issue with the database");
        }
    }


    @Override
    public UserData getUser(String username) {
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
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void createUser(UserData user) {
        try (var conn = DatabaseManager.getConnection()) {
            var addUser = conn.prepareStatement("INSERT INTO userData (username, password, email) VALUES (?, ?, ?)");
            addUser.setString(1, user.username());
            addUser.setString(2, user.password());
            addUser.setString(3, user.email());
            addUser.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var clearDatabase = conn.prepareStatement("DROP DATABASE chess");
            clearDatabase.executeUpdate();
            resetDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addAuthData(AuthData authData) {
        try (var conn = DatabaseManager.getConnection()) {
            var addAuthData = conn.prepareStatement("INSERT INTO authData (authToken, username) VALUES (?, ?, ?)");
            addAuthData.setString(1, authData.authToken());
            addAuthData.setString(2, authData.username());
            addAuthData.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeAuthData(AuthData authData) {

    }

    @Override
    public AuthData getAuthData(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var findAuth = conn.prepareStatement("SELECT * FROM authData WHERE username = ?");
            findAuth.setString(1, username);
            try (var response = findAuth.executeQuery()) {
                if (response.next()) {
                    String resToken = response.getString("authToken");
                    String resName = response.getString("username");
                    return new AuthData(resToken, resName);
                }
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public GameData[] getGameData() {
        return new GameData[0];
    }

    @Override
    public void addGameData(GameData newGameData) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void removeGame(int gameID) {

    }
}
