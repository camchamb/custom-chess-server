package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final String roomCode;

    private ChessMove move = null;

    public UserGameCommand(CommandType commandType, String authToken, String roomCode) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.roomCode = roomCode;
    }

    public UserGameCommand(CommandType commandType, String authToken, String roomCode, ChessMove move) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.roomCode = roomCode;
        this.move = move;
    }

    public UserGameCommand(CommandType commandType, String authToken, String roomCode, String move) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.roomCode = roomCode;
        this.move = new ChessMove(move);
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand that)) {
            return false;
        }
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getRoomCode(), that.getRoomCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getRoomCode());
    }
}
