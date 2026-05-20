package factory;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import java.util.ArrayList;
import java.time.LocalDate;

public class LogisticaMaritima implements DeliveryCreator {
    @Override
    public Delivery create(String cargo, LocalDate date) {
        return new Delivery(null, getType(), getType(), 0, 0, cargo, date.toString(), new ArrayList<>(), 0);
    }

    @Override
    public String getType() {
        return "MARITIMA";
    }

    @Override
    public int getFreightValue() {
        return FreightCache.getInstance().getByCode("2").map(Freight::getValue).orElse(1000);
    }
}