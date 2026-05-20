package domain;

import org.bson.Document;
import org.bson.types.ObjectId;
import util.JsonUtils;

public class Product {
    private String id;
    private String name;
    private String description;
    private int price;

    public Product() {
    }

    public Product(String id, String name, String description, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Document toDocument() {
        Document document = new Document("name", name)
                .append("description", description)
                .append("price", price);
        if (id != null && !id.isBlank()) {
            document.append("id", id);
        }
        return document;
    }

    public static Product fromDocument(Document document) {
        ObjectId objectId = document.getObjectId("_id");
        return new Product(
                document.getString("id") != null ? document.getString("id") : (objectId == null ? null : objectId.toHexString()),
                document.getString("name"),
                document.getString("description"),
                document.getInteger("price", 0)
        );
    }

    public String toJson() {
        return "{\"id\":\"" + JsonUtils.escape(id) + "\",\"name\":\"" + JsonUtils.escape(name)
                + "\",\"description\":\"" + JsonUtils.escape(description)
                + "\",\"price\":" + price + "}";
    }
}