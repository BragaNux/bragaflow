package factory;

import domain.Delivery;
import java.time.LocalDate;

public interface DeliveryCreator {
    Delivery create(String cargo, LocalDate date);

    String getType();

    int getFreightValue();
}