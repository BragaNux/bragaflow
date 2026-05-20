package service;

import cache.FreightCache;
import domain.Delivery;
import domain.Freight;
import domain.Product;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import repository.FreightRepository;
import repository.ProductRepository;
import repository.DeliveryRepository;

public class DeliveryService {
    private final DeliveryRepository repository;
    private final FreightRepository freightRepository;
    private final ProductRepository productRepository;

    public DeliveryService(DeliveryRepository repository, FreightRepository freightRepository, ProductRepository productRepository) {
        this.repository = repository;
        this.freightRepository = freightRepository;
        this.productRepository = productRepository;
    }

    public List<Freight> listFreights() {
        return freightRepository.findAll();
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public Delivery addDelivery(String freightCode, String cargo, String date, List<String> productIds) {
        Delivery delivery = buildDelivery(freightCode, cargo, date, productIds);
        return repository.save(delivery);
    }

    public List<Delivery> listDeliveries() {
        return repository.findAll();
    }

    public Delivery updateDelivery(String id, String freightCode, String cargo, String date, List<String> productIds) {
        Delivery existing = repository.findById(id);
        if (existing == null) {
            return null;
        }

        String effectiveFreightCode = freightCode == null || freightCode.isBlank() ? existing.getFreightCode() : freightCode;
        String effectiveCargo = cargo == null || cargo.isBlank() ? existing.getCargo() : cargo;
        String effectiveDate = date == null || date.isBlank() ? existing.getDate() : date;
        List<String> effectiveProductIds = productIds == null || productIds.isEmpty() ? extractProductIds(existing) : productIds;

        Delivery updated = buildDelivery(effectiveFreightCode, effectiveCargo, effectiveDate, effectiveProductIds);
        updated.setId(id);
        boolean saved = repository.update(id, updated);
        return saved ? updated : null;
    }

    public boolean removeDelivery(String id) {
        return repository.deleteById(id);
    }

    private Delivery buildDelivery(String freightCode, String cargo, String date, List<String> productIds) {
        Freight freight = FreightCache.getInstance().getByCode(freightCode)
                .or(() -> FreightCache.getInstance().getByType(freightCode))
                .orElseGet(() -> freightRepository.findByCode(freightCode));
        if (freight == null) {
            throw new IllegalArgumentException("Frete nao encontrado");
        }

        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Product product = productRepository.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Produto nao encontrado: " + productId);
            }
            products.add(product);
        }

        if (products.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um produto");
        }

        if (products.size() > freight.getMaxProducts()) {
            throw new IllegalArgumentException("O frete selecionado suporta no maximo " + freight.getMaxProducts() + " produtos");
        }

        int totalValue = freight.getValue();
        for (Product product : products) {
            totalValue += product.getPrice();
        }

        return new Delivery(null, freight.getCode(), freight.getDescription(), freight.getValue(), freight.getMaxProducts(), cargo, LocalDate.parse(date).toString(), products, totalValue);
    }

    private List<String> extractProductIds(Delivery delivery) {
        List<String> productIds = new ArrayList<>();
        if (delivery.getProducts() == null) {
            return productIds;
        }

        for (Product product : delivery.getProducts()) {
            if (product.getId() != null) {
                productIds.add(product.getId());
            }
        }
        return productIds;
    }
}