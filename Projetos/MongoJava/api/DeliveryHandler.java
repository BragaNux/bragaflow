package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.Delivery;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import service.DeliveryService;
import util.JsonUtils;

public class DeliveryHandler implements HttpHandler {
    private final DeliveryService service;

    public DeliveryHandler(DeliveryService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String base = "/api/deliveries";

        if (path.equals(base) || path.equals(base + "/")) {
            handleCollection(exchange, method);
            return;
        }

        String id = URLDecoder.decode(path.substring(base.length() + 1), StandardCharsets.UTF_8);
        handleItem(exchange, method, id);
    }

    private void handleCollection(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET" -> {
                List<Delivery> deliveries = service.listDeliveries();
                String body = deliveries.stream().map(Delivery::toJson).collect(Collectors.joining(",", "[", "]"));
                HttpUtils.sendText(exchange, 200, body);
            }
            case "POST" -> {
                String body = HttpUtils.readBody(exchange);
                Map<String, String> data = JsonUtils.parseObject(body);
                List<String> productIds = new ArrayList<>(JsonUtils.parseStringArray(body, "productIds"));
                try {
                    Delivery created = service.addDelivery(data.get("freightCode"), data.get("cargo"), data.get("date"), productIds);
                    HttpUtils.sendText(exchange, 201, created.toJson());
                } catch (IllegalArgumentException exception) {
                    HttpUtils.sendText(exchange, 400, "{\"error\":\"" + JsonUtils.escape(exception.getMessage()) + "\"}");
                }
            }
            default -> HttpUtils.sendText(exchange, 405, "{\"error\":\"Metodo nao permitido\"}");
        }
    }

    private void handleItem(HttpExchange exchange, String method, String id) throws IOException {
        switch (method) {
            case "PUT" -> {
                String body = HttpUtils.readBody(exchange);
                Map<String, String> data = JsonUtils.parseObject(body);
                List<String> productIds = new ArrayList<>(JsonUtils.parseStringArray(body, "productIds"));
                try {
                    Delivery updated = service.updateDelivery(id, data.get("freightCode"), data.get("cargo"), data.get("date"), productIds);
                    if (updated == null) {
                        HttpUtils.sendText(exchange, 404, "{\"error\":\"Entrega nao encontrada\"}");
                        return;
                    }
                    HttpUtils.sendText(exchange, 200, updated.toJson());
                } catch (IllegalArgumentException exception) {
                    HttpUtils.sendText(exchange, 400, "{\"error\":\"" + JsonUtils.escape(exception.getMessage()) + "\"}");
                }
            }
            case "DELETE" -> {
                boolean removed = service.removeDelivery(id);
                if (!removed) {
                    HttpUtils.sendText(exchange, 404, "{\"error\":\"Entrega nao encontrada\"}");
                    return;
                }
                HttpUtils.sendText(exchange, 200, "{\"message\":\"Entrega removida\"}");
            }
            default -> HttpUtils.sendText(exchange, 405, "{\"error\":\"Metodo nao permitido\"}");
        }
    }
}