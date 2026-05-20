import api.DeliveryHandler;
import api.FreightHandler;
import api.ProductHandler;
import api.StaticHandler;
import api.UserHandler;
import cache.FreightCache;
import cache.UserCache;
import config.MongoConnection;
import repository.FreightRepository;
import repository.DeliveryRepository;
import repository.ProductRepository;
import repository.UserRepository;
import service.DeliveryService;
import service.UserService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) throws IOException {
        MongoConnection mongoConnection = MongoConnection.getInstance();
        UserRepository userRepository = new UserRepository(mongoConnection.getDatabase());
        DeliveryRepository deliveryRepository = new DeliveryRepository(mongoConnection.getDatabase());
        FreightRepository freightRepository = new FreightRepository(mongoConnection.getDatabase());
        ProductRepository productRepository = new ProductRepository(mongoConnection.getDatabase());

        freightRepository.seedDefaults();
        productRepository.seedDefaults();
        FreightCache.getInstance().loadAll(freightRepository.findAll());

        UserService userService = new UserService(userRepository, UserCache.getInstance());
        DeliveryService deliveryService = new DeliveryService(deliveryRepository, freightRepository, productRepository);

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/users", new UserHandler(userService));
        server.createContext("/api/deliveries", new DeliveryHandler(deliveryService));
        server.createContext("/api/freights", new FreightHandler(deliveryService));
        server.createContext("/api/products", new ProductHandler(deliveryService));
        server.createContext("/", new StaticHandler());
        server.setExecutor(Executors.newFixedThreadPool(8));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            UserCache.getInstance().close();
            mongoConnection.close();
            server.stop(0);
        }));

        server.start();
        System.out.println("Servidor iniciado em http://localhost:" + port);
    }
}