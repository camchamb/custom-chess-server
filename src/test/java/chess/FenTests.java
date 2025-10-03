package chess;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import requests.RegisterResult;
import chess.*;

public class FenTests {

    @Test
    @Order(1)
    @DisplayName("Fen Test")
    public void Fen() throws DataAccessException {
        var game = new ChessGame();
        System.out.println(game.toFen());
    }
}
