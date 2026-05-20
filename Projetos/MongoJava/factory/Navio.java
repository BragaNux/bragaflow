package factory;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import java.time.LocalDate;
import java.util.ArrayList;

public class Navio implements DeliveryCreator {
    @Override
    public Delivery create(String cargo, LocalDate date) {
        Freight freight = FreightCache.getInstance().getByCode("2")
                .orElseGet(() -> FreightCache.getInstance().getByType("Navio").orElse(null));
        int value = freight == null ? 1000 : freight.getValue();
        int maxProducts = freight == null ? 80 : freight.getMaxProducts();
        String code = freight == null ? "2" : freight.getCode();
        String description = freight == null ? "Navio" : freight.getDescription();
        return new Delivery(null, code, description, value, maxProducts, cargo, date.toString(), new ArrayList<>(), value);
    }

    @Override
    public String getType() {
        return "NAVIO";
    }

    @Override
    public int getFreightValue() {
        return FreightCache.getInstance().getByCode("2").map(Freight::getValue).orElse(1000);
    }
}