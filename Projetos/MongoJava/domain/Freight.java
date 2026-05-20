package domain;

import org.bson.Document;
import org.bson.types.ObjectId;
import util.JsonUtils;

public class Freight {
    private String id;
    private String code;
    private String description;
    private int value;
    private int maxProducts;

    public Freight() {
    }

    public Freight(String id, String code, String description, int value, int maxProducts) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.value = value;
        this.maxProducts = maxProducts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxProducts() {
        return maxProducts;
    }

    public void setMaxProducts(int maxProducts) {
        this.maxProducts = maxProducts;
    }

    public Document toDocument() {
        return new Document("code", code)
                .append("description", description)
                .append("value", value)
                .append("maxProducts", maxProducts);
    }

    public static Freight fromDocument(Document document) {
        ObjectId objectId = document.getObjectId("_id");
        return new Freight(
                objectId == null ? null : objectId.toHexString(),
                document.getString("code"),
                document.getString("description"),
                document.getInteger("value", 0),
                document.getInteger("maxProducts", 0)
        );
    }

    public String toJson() {
        return "{\"id\":\"" + JsonUtils.escape(id) + "\",\"code\":\"" + JsonUtils.escape(code)
                + "\",\"description\":\"" + JsonUtils.escape(description)
                + "\",\"value\":" + value + ",\"maxProducts\":" + maxProducts + "}";
    }
}