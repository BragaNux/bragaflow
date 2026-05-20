package factory;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import java.util.ArrayList;
import java.time.LocalDate;

public class LogisticaTerrestre implements DeliveryCreator {
    @Override
    public Delivery create(String cargo, LocalDate date) {
        return new Delivery(null, getType(), getType(), 0, 0, cargo, date.toString(), new ArrayList<>(), 0);
    }

    @Override
    public String getType() {
        return "TERRESTRE";
    }

    @Override
    public int getFreightValue() {
        return FreightCache.getInstance().getByCode("1").map(Freight::getValue).orElse(500);
    }
}