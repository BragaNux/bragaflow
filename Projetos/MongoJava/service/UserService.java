package service;

import cache.UserCache;
import domain.User;
import java.util.List;
import repository.UserRepository;

public class UserService {
    private final UserRepository repository;
    private final UserCache cache;

    public UserService(UserRepository repository, UserCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    public User addUser(User user) {
        User saved = repository.save(user);
        cache.put(saved);
        return saved;
    }

    public User getUserByUsername(String username) {
        return cache.get(username).orElseGet(() -> {
            User found = repository.findByUsername(username);
            if (found != null) {
                cache.put(found);
            }
            return found;
        });
    }

    public List<User> listUsers() {
        return repository.findAll();
    }

    public User updateUser(String username, User user) {
        user.setUsername(username);
        boolean updated = repository.update(username, user);
        if (updated) {
            cache.put(user);
            return user;
        }
        return null;
    }

    public boolean removeUser(String username) {
        cache.remove(username);
        return repository.deleteByUsername(username);
    }
}