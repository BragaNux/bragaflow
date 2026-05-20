package domain;

import org.bson.Document;
import util.JsonUtils;

public class User {
    private String username;
    private String email;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Document toDocument() {
        return new Document("username", username)
                .append("email", email);
    }

    public static User fromDocument(Document document) {
        return new User(document.getString("username"), document.getString("email"));
    }

    public String toJson() {
        return "{\"username\":\"" + JsonUtils.escape(username) + "\",\"email\":\"" + JsonUtils.escape(email) + "\"}";
    }
}