package dataaccess;

import data.GameData;

import java.util.Collection;

public interface GameDAO {
    String createGame() throws DataAccessException;

    GameData getGame(String roomCode) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData u) throws DataAccessException;

    void clear() throws DataAccessException;
}
