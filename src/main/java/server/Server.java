package server;

import dataaccess.*;
import io.javalin.*;
import com.google.gson.Gson;
import io.javalin.http.Context;
import jakarta.servlet.http.Cookie;

import requests.*;
import server.websocket.WebSocketHandler;
import service.*;

import java.util.Map;


public class Server {

    private final Javalin javalin;

    UserDAO userAccess;
    GameDAO gameAccess;
    AuthDAO authAccess;
    UserService userService;
    GameService gameService;
    Gson serializer = new Gson();
    WebSocketHandler webSocketHandler;

    public Server() {
        try {
            userAccess = new UserSqlAccess();
            gameAccess = new GameSqlAccess();
            authAccess = new AuthSqlAccess();
            webSocketHandler = new WebSocketHandler(userAccess, gameAccess, authAccess);
        }  catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }


        this.userService = new UserService(userAccess, authAccess);
        this.gameService = new GameService(gameAccess, authAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register)
                .delete("/db", this::db)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame)
                .exception(Exception.class, this::exceptionHandler)
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context context) {
        try {
            var regReq = serializer.fromJson(context.body(), RegisterRequest.class);
            RegisterResult regRes = userService.register(regReq);

            Cookie cookie = new Cookie("authToken", regRes.authToken());
            cookie.setMaxAge(3600);       // 1 hour
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);       // HTTPS only
            context.res().addCookie(cookie);
            context.json(new LoginResult(regRes.username(), null));

            System.out.println(serializer.toJson(regRes));
        }
        catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void db(Context context) {
        try {
            userService.clear();
            gameService.clear();
            context.json("{ }");
        } catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void login(Context context) {
        try {
            var loginRequest = serializer.fromJson(context.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);

            Cookie cookie = new Cookie("authToken", loginResult.authToken());
            cookie.setMaxAge(3600);       // 1 hour
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);       // HTTPS only
            context.res().addCookie(cookie);
            context.json(new LoginResult(loginRequest.email(), null));
//            context.json(serializer.toJson(loginResult));
            System.out.println(serializer.toJson(loginResult));
        }
        catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void logout(Context context) {
        try {
            String authToken = context.cookie("authToken");
            userService.logout(new LogoutRequest(authToken));
            context.json("{ }");
        }
        catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void listGames(Context context) {
        try {
            String authToken = context.cookie("authToken");
            var listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            context.json(serializer.toJson(listGamesResult));
            System.out.println(serializer.toJson(listGamesResult));
        }
        catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void createGame(Context context) {
        try {
            var createGameRequest = new CreateGameRequest(context.cookie("authToken"));
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            context.json(serializer.toJson(createGameResult));
            System.out.println(serializer.toJson(createGameResult));
        }
        catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void joinGame(Context context) {
        try {
            var tempRequest = serializer.fromJson(context.body(), JoinGameRequest.class);
            var joinGameRequest = new JoinGameRequest(tempRequest.playerColor(),
                    tempRequest.roomCode(), context.cookie("authToken"));
            gameService.joinGame(joinGameRequest);
            context.json("{ }");
        }
        catch (DataAccessException ex) {
            errorHandling(ex, context);
        }
    }

    private void exceptionHandler(Exception e, Context context) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        context.status(500);
        context.json(body);
    }


    public void errorHandling(DataAccessException ex, Context context) {
        context.status(ex.getStatus());
        String message = ex.getMessage();
        String json = "{\"message\": \"" + message + "\" }";
        System.out.println(json);
        context.json(json);
    }
}
