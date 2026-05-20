package repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import domain.Product;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

public class ProductRepository {
    private final MongoCollection<Document> collection;

    public ProductRepository(MongoDatabase database) {
        this.collection = database.getCollection("produtos");
    }

    public void seedDefaults() {
        if (collection.countDocuments() > 0) {
            return;
        }

        collection.insertMany(List.of(
                new Product(null, "Teclado Mecânico", "Teclado gamer com switch azul", 220).toDocument(),
                new Product(null, "Mouse Sem Fio", "Mouse ergonômico para escritório", 120).toDocument(),
                new Product(null, "Monitor 24\"", "Monitor Full HD para setup", 780).toDocument(),
                new Product(null, "Headset", "Headset com microfone", 180).toDocument(),
                new Product(null, "Webcam", "Webcam Full HD", 160).toDocument(),
                new Product(null, "Mousepad XXL", "Mousepad estendido", 90).toDocument(),
                new Product(null, "Notebook", "Notebook de demonstração", 3500).toDocument(),
                new Product(null, "Cadeira Gamer", "Cadeira ergonômica", 1450).toDocument()
        ));
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        FindIterable<Document> documents = collection.find();
        for (Document document : documents) {
            products.add(Product.fromDocument(document));
        }
        return products;
    }

    public Product findById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        Document document = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return document == null ? null : Product.fromDocument(document);
    }
}