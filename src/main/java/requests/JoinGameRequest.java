package requests;

public record JoinGameRequest(String playerColor, String roomCode, String authToken) {
}
