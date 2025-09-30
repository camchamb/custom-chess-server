package data;

import chess.ChessGame;

public record GameData(String roomCode, String whiteUsername, String blackUsername, ChessGame game) {

    @Override
    public String toString() {
        String white = (whiteUsername != null) ? whiteUsername : "Open";
        String black = (blackUsername != null) ? blackUsername : "Open";
        return roomCode + "\n" +
                "White Player: " + white + "\n" +
                "Black Player: " + black + "\n";
    }
}
