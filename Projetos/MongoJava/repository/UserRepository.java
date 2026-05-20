package repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import domain.User;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class UserRepository {
    private final MongoCollection<Document> collection;

    public UserRepository(MongoDatabase database) {
        this.collection = database.getCollection("users");
        this.collection.createIndex(new Document("username", 1), new IndexOptions().unique(true));
    }

    public User save(User user) {
        collection.replaceOne(Filters.eq("username", user.getUsername()), user.toDocument(), new ReplaceOptions().upsert(true));
        return user;
    }

    public User findByUsername(String username) {
        Document document = collection.find(Filters.eq("username", username)).first();
        return document == null ? null : User.fromDocument(document);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        FindIterable<Document> documents = collection.find();
        for (Document document : documents) {
            users.add(User.fromDocument(document));
        }
        return users;
    }

    public boolean update(String username, User user) {
        Bson filter = Filters.eq("username", username);
        long modified = collection.replaceOne(filter, user.toDocument()).getModifiedCount();
        return modified > 0;
    }

    public boolean deleteByUsername(String username) {
        return collection.deleteOne(Filters.eq("username", username)).getDeletedCount() > 0;
    }
}