package factory;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import java.time.LocalDate;
import java.util.ArrayList;

public class Trem implements DeliveryCreator {
    @Override
    public Delivery create(String cargo, LocalDate date) {
        Freight freight = FreightCache.getInstance().getByCode("4")
                .orElseGet(() -> FreightCache.getInstance().getByType("Trem").orElse(null));
        int value = freight == null ? 900 : freight.getValue();
        int maxProducts = freight == null ? 30 : freight.getMaxProducts();
        String code = freight == null ? "4" : freight.getCode();
        String description = freight == null ? "Trem" : freight.getDescription();
        return new Delivery(null, code, description, value, maxProducts, cargo, date.toString(), new ArrayList<>(), value);
    }

    @Override
    public String getType() {
        return "TREM";
    }

    @Override
    public int getFreightValue() {
        return FreightCache.getInstance().getByCode("4").map(Freight::getValue).orElse(900);
    }
}