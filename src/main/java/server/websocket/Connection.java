package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public String roomCode;
    public Session session;

    public Connection(String authToken, String RoomCode, Session session) {
        this.authToken = authToken;
        this.roomCode = RoomCode;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}