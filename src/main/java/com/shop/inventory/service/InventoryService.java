package com.shop.inventory.service;

import com.shop.inventory.entity.*;
import com.shop.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public List<Part> findAllParts() {
        return repository.findAllParts();
    }

    public List<Product> findAllProducts() {
        return repository.findAllProducts();
    }

    public Part findPartById(long id) {
        return repository.findPartById(id);
    }

    public Product findProductById(long id) {
        return repository.findProductById(id);
    }

    public List<Part> searchParts(String name) {
        if (name == null || name.trim().isEmpty()) return findAllParts();
        return findAllParts().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String name) {
        if (name == null || name.trim().isEmpty()) return findAllProducts();
        return findAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    public void savePart(Part part) {
        repository.savePart(part);
    }

    public void saveProduct(Product product) {
        repository.saveProduct(product);
    }

    public void deletePart(Part part) {
        repository.deletePart(part);
    }

    public void deleteProduct(Product product) {
        repository.deleteProduct(product);
    }

    /**
     * Add sample inventory only when both part and product lists are empty (requirement E).
     * Uses Set for products so duplicate items become a "multi-pack" part.
     */
    public boolean addSampleInventoryIfEmpty() {
        List<Part> parts = repository.findAllParts();
        List<Product> products = repository.findAllProducts();
        if (!parts.isEmpty() || !products.isEmpty()) {
            return false;
        }
        addGuitarShopSampleInventory();
        return true;
    }

    private void addGuitarShopSampleInventory() {
        // 5 parts
        InhousePart strings = new InhousePart();
        strings.setName("Acoustic Guitar Strings Set");
        strings.setPrice(12.99);
        strings.setInv(50);
        strings.setMin(10);
        strings.setMax(100);
        strings.setPartId(1001);
        repository.savePart(strings);

        InhousePart tuners = new InhousePart();
        tuners.setName("Machine Head Tuners Set");
        tuners.setPrice(24.99);
        tuners.setInv(30);
        tuners.setMin(5);
        tuners.setMax(50);
        tuners.setPartId(1002);
        repository.savePart(tuners);

        OutsourcedPart pickups = new OutsourcedPart();
        pickups.setName("Humbucker Pickup Set");
        pickups.setCompanyName("Seymour Duncan");
        pickups.setPrice(149.99);
        pickups.setInv(20);
        pickups.setMin(5);
        pickups.setMax(40);
        repository.savePart(pickups);

        OutsourcedPart neck = new OutsourcedPart();
        neck.setName("Maple Guitar Neck");
        neck.setCompanyName("Warmoth");
        neck.setPrice(199.99);
        neck.setInv(15);
        neck.setMin(3);
        neck.setMax(25);
        repository.savePart(neck);

        InhousePart bridge = new InhousePart();
        bridge.setName("Tune-O-Matic Bridge");
        bridge.setPrice(45.00);
        bridge.setInv(25);
        bridge.setMin(5);
        bridge.setMax(50);
        bridge.setPartId(1003);
        repository.savePart(bridge);

        // 5 products (guitars) - use Set for parts so one part per product (duplicate part = multi-pack)
        Product acoustic = new Product();
        acoustic.setName("Classic Acoustic Guitar");
        acoustic.setPrice(299.99);
        acoustic.setInv(8);
        Set<Part> acousticParts = new HashSet<>();
        acousticParts.add(strings);
        acoustic.setParts(acousticParts);
        repository.saveProduct(acoustic);

        Product electric = new Product();
        electric.setName("Standard Electric Guitar");
        electric.setPrice(549.99);
        electric.setInv(5);
        Set<Part> electricParts = new HashSet<>();
        electricParts.add(strings);
        electricParts.add(pickups);
        electricParts.add(neck);
        electricParts.add(bridge);
        electricParts.add(tuners);
        electric.setParts(electricParts);
        repository.saveProduct(electric);

        Product beginnerPack = new Product();
        beginnerPack.setName("Beginner Guitar Pack");
        beginnerPack.setPrice(199.99);
        beginnerPack.setInv(12);
        Set<Part> beginnerParts = new HashSet<>();
        beginnerParts.add(strings);
        beginnerParts.add(tuners);
        beginnerPack.setParts(beginnerParts);
        repository.saveProduct(beginnerPack);

        Product bass = new Product();
        bass.setName("4-String Bass Guitar");
        bass.setPrice(429.99);
        bass.setInv(4);
        Set<Part> bassParts = new HashSet<>();
        bassParts.add(strings);
        bassParts.add(neck);
        bassParts.add(tuners);
        bassParts.add(bridge);
        bass.setParts(bassParts);
        repository.saveProduct(bass);

        Product custom = new Product();
        custom.setName("Custom Electric Guitar");
        custom.setPrice(899.99);
        custom.setInv(2);
        Set<Part> customParts = new HashSet<>();
        customParts.add(strings);
        customParts.add(pickups);
        customParts.add(neck);
        customParts.add(bridge);
        customParts.add(tuners);
        custom.setParts(customParts);
        repository.saveProduct(custom);
    }

    /**
     * Buy Now: decrement product inventory by one; does not affect parts (requirement F).
     */
    public boolean buyNow(long productId) {
        Product product = repository.findProductById(productId);
        if (product == null || product.getInv() <= 0) return false;
        product.setInv(product.getInv() - 1);
        repository.saveProduct(product);
        return true;
    }

    public boolean isPartUsedInProduct(Part part) {
        return findAllProducts().stream()
                .anyMatch(p -> p.getParts() != null && p.getParts().contains(part));
    }
}
