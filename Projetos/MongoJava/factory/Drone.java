package factory;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import java.time.LocalDate;
import java.util.ArrayList;

public class Drone implements DeliveryCreator {
    @Override
    public Delivery create(String cargo, LocalDate date) {
        Freight freight = FreightCache.getInstance().getByCode("3")
                .orElseGet(() -> FreightCache.getInstance().getByType("Drone").orElse(null));
        int value = freight == null ? 100 : freight.getValue();
        int maxProducts = freight == null ? 2 : freight.getMaxProducts();
        String code = freight == null ? "3" : freight.getCode();
        String description = freight == null ? "Drone" : freight.getDescription();
        return new Delivery(null, code, description, value, maxProducts, cargo, date.toString(), new ArrayList<>(), value);
    }

    @Override
    public String getType() {
        return "DRONE";
    }

    @Override
    public int getFreightValue() {
        return FreightCache.getInstance().getByCode("3").map(Freight::getValue).orElse(100);
    }
}