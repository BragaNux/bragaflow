package cache;

import domain.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class UserCache {
    private static UserCache instance;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final ScheduledExecutorService cleaner;

    private UserCache() {
        this.ttlMillis = Long.parseLong(System.getenv().getOrDefault("USER_CACHE_TTL_MS", "300000"));
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::removeExpiredEntries, ttlMillis, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public static synchronized UserCache getInstance() {
        if (instance == null) {
            instance = new UserCache();
        }
        return instance;
    }

    public void put(User user) {
        cache.put(user.getUsername(), new CacheEntry(user, System.currentTimeMillis() + ttlMillis));
    }

    public Optional<User> get(String username) {
        CacheEntry entry = cache.get(username);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired()) {
            cache.remove(username);
            return Optional.empty();
        }
        return Optional.of(entry.user);
    }

    public void remove(String username) {
        cache.remove(username);
    }

    public void close() {
        cleaner.shutdownNow();
    }

    private void removeExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private static final class CacheEntry {
        private final User user;
        private final long expiresAt;

        private CacheEntry(User user, long expiresAt) {
            this.user = user;
            this.expiresAt = expiresAt;
        }

        private boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}