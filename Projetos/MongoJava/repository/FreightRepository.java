package repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import domain.Freight;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class FreightRepository {
    private final MongoCollection<Document> collection;

    public FreightRepository(MongoDatabase database) {
        this.collection = database.getCollection("fretes");
    }

    public void seedDefaults() {
        if (collection.countDocuments() > 0) {
            return;
        }

        collection.insertMany(List.of(
                new Freight(null, "1", "Caminhão", 500, 15).toDocument(),
                new Freight(null, "2", "Navio", 1000, 80).toDocument(),
                new Freight(null, "3", "Drone", 100, 2).toDocument(),
                new Freight(null, "4", "Trem", 900, 30).toDocument()
        ));
    }

    public List<Freight> findAll() {
        List<Freight> freights = new ArrayList<>();
        FindIterable<Document> documents = collection.find();
        for (Document document : documents) {
            freights.add(Freight.fromDocument(document));
        }
        return freights;
    }

    public Freight findByCode(String code) {
        Document document = collection.find(Filters.eq("code", code)).first();
        return document == null ? null : Freight.fromDocument(document);
    }
}