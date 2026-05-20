package factory;

public final class DeliveryFactory {
    private DeliveryFactory() {
    }

    public static DeliveryCreator create(String type) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        return switch (normalized) {
            case "1", "CAMINHAO", "TERRESTRE" -> new Caminhao();
            case "2", "NAVIO", "MARITIMA" -> new Navio();
            case "3", "DRONE" -> new Drone();
            case "4", "TREM" -> new Trem();
            case "AEREA" -> new LogisticaAerea();
            default -> new Caminhao();
        };
    }
}