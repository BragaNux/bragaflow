package cache;

import domain.Freight;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FreightCache {
    private static FreightCache instance;

    private final Map<String, Freight> byCode = new HashMap<>();
    private final Map<String, Freight> byType = new HashMap<>();

    private FreightCache() {
    }

    public static synchronized FreightCache getInstance() {
        if (instance == null) {
            instance = new FreightCache();
        }
        return instance;
    }

    public synchronized void loadAll(List<Freight> freights) {
        byCode.clear();
        byType.clear();

        if (freights == null) {
            return;
        }

        for (Freight freight : freights) {
            register(freight);
        }
    }

    public synchronized void register(Freight freight) {
        if (freight == null) {
            return;
        }

        byCode.put(normalize(freight.getCode()), freight);
        byType.put(normalize(freight.getDescription()), freight);
    }

    public Optional<Freight> getByCode(String code) {
        return Optional.ofNullable(byCode.get(normalize(code)));
    }

    public Optional<Freight> getByType(String type) {
        return Optional.ofNullable(byType.get(normalize(type)));
    }

    public Map<String, Freight> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(byCode));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}