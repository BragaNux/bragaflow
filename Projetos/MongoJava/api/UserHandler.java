package api;

import domain.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import service.UserService;
import util.JsonUtils;

public class UserHandler implements HttpHandler {
    private final UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String base = "/api/users";

        if (path.equals(base) || path.equals(base + "/")) {
            handleCollection(exchange, method);
            return;
        }

        String username = URLDecoder.decode(path.substring(base.length() + 1), StandardCharsets.UTF_8);
        handleItem(exchange, method, username);
    }

    private void handleCollection(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET" -> {
                List<User> users = service.listUsers();
                String body = users.stream().map(User::toJson).collect(Collectors.joining(",", "[", "]"));
                HttpUtils.sendText(exchange, 200, body);
            }
            case "POST" -> {
                Map<String, String> data = JsonUtils.parseObject(HttpUtils.readBody(exchange));
                User created = service.addUser(new User(data.get("username"), data.get("email")));
                HttpUtils.sendText(exchange, 201, created.toJson());
            }
            default -> HttpUtils.sendText(exchange, 405, "{\"error\":\"Metodo nao permitido\"}");
        }
    }

    private void handleItem(HttpExchange exchange, String method, String username) throws IOException {
        switch (method) {
            case "GET" -> {
                User user = service.getUserByUsername(username);
                if (user == null) {
                    HttpUtils.sendText(exchange, 404, "{\"error\":\"Usuario nao encontrado\"}");
                    return;
                }
                HttpUtils.sendText(exchange, 200, user.toJson());
            }
            case "PUT" -> {
                Map<String, String> data = JsonUtils.parseObject(HttpUtils.readBody(exchange));
                User updated = service.updateUser(username, new User(username, data.get("email")));
                if (updated == null) {
                    HttpUtils.sendText(exchange, 404, "{\"error\":\"Usuario nao encontrado\"}");
                    return;
                }
                HttpUtils.sendText(exchange, 200, updated.toJson());
            }
            case "DELETE" -> {
                boolean removed = service.removeUser(username);
                if (!removed) {
                    HttpUtils.sendText(exchange, 404, "{\"error\":\"Usuario nao encontrado\"}");
                    return;
                }
                HttpUtils.sendText(exchange, 200, "{\"message\":\"Usuario removido\"}");
            }
            default -> HttpUtils.sendText(exchange, 405, "{\"error\":\"Metodo nao permitido\"}");
        }
    }
}