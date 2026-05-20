package domain;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import util.JsonUtils;

public class Delivery {
    private String id;
    private String freightCode;
    private String freightDescription;
    private int freightValue;
    private int maxProducts;
    private String cargo;
    private String date;
    private List<Product> products;
    private int totalValue;

    public Delivery() {
        this.products = new ArrayList<>();
    }

    public Delivery(String id, String freightCode, String freightDescription, int freightValue, int maxProducts, String cargo, String date, List<Product> products, int totalValue) {
        this.id = id;
        this.freightCode = freightCode;
        this.freightDescription = freightDescription;
        this.freightValue = freightValue;
        this.maxProducts = maxProducts;
        this.cargo = cargo;
        this.date = date;
        this.products = products == null ? new ArrayList<>() : new ArrayList<>(products);
        this.totalValue = totalValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFreightCode() {
        return freightCode;
    }

    public void setFreightCode(String freightCode) {
        this.freightCode = freightCode;
    }

    public String getFreightDescription() {
        return freightDescription;
    }

    public void setFreightDescription(String freightDescription) {
        this.freightDescription = freightDescription;
    }

    public int getFreightValue() {
        return freightValue;
    }

    public void setFreightValue(int freightValue) {
        this.freightValue = freightValue;
    }

    public int getMaxProducts() {
        return maxProducts;
    }

    public void setMaxProducts(int maxProducts) {
        this.maxProducts = maxProducts;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products == null ? new ArrayList<>() : new ArrayList<>(products);
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    public Document toDocument() {
        List<Document> productDocuments = new ArrayList<>();
        for (Product product : products) {
            productDocuments.add(product.toDocument());
        }

        return new Document("freightCode", freightCode)
                .append("freightDescription", freightDescription)
                .append("freightValue", freightValue)
                .append("maxProducts", maxProducts)
                .append("cargo", cargo)
                .append("date", date)
                .append("products", productDocuments)
                .append("totalValue", totalValue);
    }

    public static Delivery fromDocument(Document document) {
        List<Product> products = new ArrayList<>();
        List<Document> productDocuments = document.getList("products", Document.class);
        if (productDocuments != null) {
            for (Document productDocument : productDocuments) {
                products.add(Product.fromDocument(productDocument));
            }
        }

        ObjectId objectId = document.getObjectId("_id");
        return new Delivery(
                objectId == null ? null : objectId.toHexString(),
                document.getString("freightCode"),
                document.getString("freightDescription"),
                document.getInteger("freightValue", 0),
                document.getInteger("maxProducts", 0),
                document.getString("cargo"),
                document.getString("date"),
                products,
                document.getInteger("totalValue", 0)
        );
    }

    public String toJson() {
        List<Product> currentProducts = products == null ? new ArrayList<>() : products;
        StringBuilder builder = new StringBuilder();
        builder.append("{\"id\":\"").append(JsonUtils.escape(id))
                .append("\",\"freightCode\":\"").append(JsonUtils.escape(freightCode))
                .append("\",\"freightDescription\":\"").append(JsonUtils.escape(freightDescription))
                .append("\",\"freightValue\":").append(freightValue)
                .append(",\"maxProducts\":").append(maxProducts)
                .append(",\"cargo\":\"").append(JsonUtils.escape(cargo))
                .append("\",\"date\":\"").append(JsonUtils.escape(date))
                .append("\",\"totalValue\":").append(totalValue)
                .append(",\"productCount\":").append(currentProducts.size())
                .append(",\"products\":[");

        for (int index = 0; index < currentProducts.size(); index++) {
            if (index > 0) {
                builder.append(',');
            }
            builder.append(currentProducts.get(index).toJson());
        }

        builder.append("]}");
        return builder.toString();
    }
}