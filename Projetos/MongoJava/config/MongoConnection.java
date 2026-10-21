package config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class MongoConnection implements AutoCloseable {
    private static MongoConnection instance;
    // A string de conexão vem da variável de ambiente MONGODB_URI, para nenhuma credencial ficar no código.
    private static final String URI_ENV = "MONGODB_URI";

    private final MongoClient client;
    private final MongoDatabase database;

    private MongoConnection() {
        String uri = System.getenv(URI_ENV);
        if (uri == null || uri.trim().isEmpty()) {
            throw new IllegalStateException("Defina a variável de ambiente " + URI_ENV + " com a string de conexão do MongoDB Atlas.");
        }
        String databaseName = System.getenv().getOrDefault("MONGODB_DB", "teste");
        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(databaseName);
    }

    public static synchronized MongoConnection getInstance() {
        if (instance == null) {
            instance = new MongoConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void close() {
        client.close();
    }
}
