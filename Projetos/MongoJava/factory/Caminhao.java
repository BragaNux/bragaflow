package factory;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import java.time.LocalDate;
import java.util.ArrayList;

public class Caminhao implements DeliveryCreator {
    @Override
    public Delivery create(String cargo, LocalDate date) {
        Freight freight = FreightCache.getInstance().getByCode("1")
                .orElseGet(() -> FreightCache.getInstance().getByType("Caminhão").orElse(null));
        int value = freight == null ? 500 : freight.getValue();
        int maxProducts = freight == null ? 15 : freight.getMaxProducts();
        String code = freight == null ? "1" : freight.getCode();
        String description = freight == null ? "Caminhão" : freight.getDescription();
        return new Delivery(null, code, description, value, maxProducts, cargo, date.toString(), new ArrayList<>(), value);
    }

    @Override
    public String getType() {
        return "CAMINHAO";
    }

    @Override
    public int getFreightValue() {
        return FreightCache.getInstance().getByCode("1").map(Freight::getValue).orElse(500);
    }
}