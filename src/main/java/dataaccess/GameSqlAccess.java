package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import data.GameData;
import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.Collection;

public class GameSqlAccess implements GameDAO {
    private final Gson serializer = new Gson();

    public GameSqlAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public String createGame() throws DataAccessException {
        while (true) {
            String room = GameCodeGenerator();
            var gameData = getGame(room);
            if (gameData == null) {
                var statement = "INSERT INTO game (roomCode, game) VALUES(?, ?)";
                var game = serializer.toJson(new ChessGame(), ChessGame.class);
                SqlUtils.executeUpdate(statement, room, game);
                return room;
            }
        }
    }

    @Override
    public GameData getGame(String roomCode) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, game FROM game WHERE roomCode = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, roomCode);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
//                        var gameName = rs.getString("gameName");
                        var game = rs.getString("game");
                        var gameObject = serializer.fromJson(game, ChessGame.class);
                        return new GameData(roomCode, whiteUsername, blackUsername, gameObject);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var allGames = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, roomCode FROM game";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
//                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var roomCode = rs.getString("roomCode");
                        allGames.add(new GameData(roomCode, whiteUsername, blackUsername, null));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return allGames;
    }

    @Override
    public void updateGame(GameData u) throws DataAccessException {
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, roomCode = ?, game = ? WHERE roomCode = ?";
        var game = serializer.toJson(u.game(), ChessGame.class);
        SqlUtils.executeUpdate(statement, u.whiteUsername(), u.blackUsername(), u.roomCode(), game, u.roomCode());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM game";
        SqlUtils.executeUpdate(statement);
    }

    private void configureDatabase() throws DataAccessException {
        var createUserTable = """
            CREATE TABLE  IF NOT EXISTS game (
                roomCode VARCHAR(255) NOT NULL,
                whiteUsername VARCHAR(255) DEFAULT NULL,
                blackUsername VARCHAR(255) DEFAULT NULL,
                game TEXT DEFAULT NULL,
                PRIMARY KEY (roomCode),
                FOREIGN KEY (whiteUsername) REFERENCES user(username) ON DELETE CASCADE,
                FOREIGN KEY (blackUsername) REFERENCES user(username) ON DELETE CASCADE
            )""";

            SqlUtils.configureDatabase(createUserTable);
}


    public String GameCodeGenerator() {
        String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ123456789";
        int CODE_LENGTH = 6;
        SecureRandom random = new SecureRandom();

        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

}
