package server.websocket;

import com.fasterxml.jackson.databind.DatabindException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson serializer = new Gson();

    public void add(String authToken, String roomCode, Session session) throws DataAccessException {
        if (authToken == null || roomCode == null || session == null) {
            throw new DataAccessException(500, "No roomCode");
        }
        var connection = new Connection(authToken, roomCode, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, String roomCode, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeAuthToken)) {
                    if (c.roomCode.equals(roomCode)) {
                        c.send(serializer.toJson(notification));
                        System.out.println("Broadcasted: " + serializer.toJson(notification));
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void messageRoot(Session session, ServerMessage msg) throws IOException {
        if (session == null) {
            throw new RuntimeException("Root ws session closed");
        }
        session.getRemote().sendString(serializer.toJson(msg));
        System.out.println("Sent to Root: " + serializer.toJson(msg));
    }
}