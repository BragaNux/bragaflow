package config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class MongoConnection implements AutoCloseable {
    private static MongoConnection instance;
    private static final String URI = "mongodb+srv://devbrayan:ieNdWcmx9vJMUVgy@clusterteste.0wiph78.mongodb.net/?appName=ClusterTeste";

    private final MongoClient client;
    private final MongoDatabase database;

    private MongoConnection() {
        String databaseName = System.getenv().getOrDefault("MONGODB_DB", "teste");
        this.client = MongoClients.create(URI);
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