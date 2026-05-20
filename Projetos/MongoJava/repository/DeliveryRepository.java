package repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import domain.Delivery;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class DeliveryRepository {
    private final MongoCollection<Document> collection;

    public DeliveryRepository(MongoDatabase database) {
        this.collection = database.getCollection("deliveries");
    }

    public Delivery save(Delivery delivery) {
        Document document = delivery.toDocument();
        collection.insertOne(document);
        delivery.setId(document.getObjectId("_id").toHexString());
        return delivery;
    }

    public List<Delivery> findAll() {
        List<Delivery> deliveries = new ArrayList<>();
        FindIterable<Document> documents = collection.find();
        for (Document document : documents) {
            deliveries.add(Delivery.fromDocument(document));
        }
        return deliveries;
    }

    public Delivery findById(String id) {
        Document document = collection.find(Filters.eq("_id", new org.bson.types.ObjectId(id))).first();
        return document == null ? null : Delivery.fromDocument(document);
    }

    public boolean update(String id, Delivery delivery) {
        Document update = delivery.toDocument();
        return collection.replaceOne(Filters.eq("_id", new org.bson.types.ObjectId(id)), update).getModifiedCount() > 0;
    }

    public boolean deleteById(String id) {
        return collection.deleteOne(Filters.eq("_id", new org.bson.types.ObjectId(id))).getDeletedCount() > 0;
    }
}