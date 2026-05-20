package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import domain.Product;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import service.DeliveryService;

public class ProductHandler implements HttpHandler {
    private final DeliveryService service;

    public ProductHandler(DeliveryService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            HttpUtils.sendText(exchange, 405, "{\"error\":\"Metodo nao permitido\"}");
            return;
        }

        List<Product> products = service.listProducts();
        String body = products.stream().map(Product::toJson).collect(Collectors.joining(",", "[", "]"));
        HttpUtils.sendText(exchange, 200, body);
    }
}