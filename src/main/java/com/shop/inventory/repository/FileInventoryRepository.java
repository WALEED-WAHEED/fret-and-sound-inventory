package com.shop.inventory.repository;

import com.shop.inventory.entity.*;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Persistent storage is saved to the file name below (requirement G).
 */
@Repository
public class FileInventoryRepository implements InventoryRepository {

    private static final String STORAGE_FILENAME = "guitar_shop_inventory.dat";

    private final List<Part> parts = new CopyOnWriteArrayList<>();
    private final List<Product> products = new CopyOnWriteArrayList<>();
    private final AtomicLong nextPartId = new AtomicLong(1);
    private final AtomicLong nextProductId = new AtomicLong(1);

    @PostConstruct
    public void init() {
        loadInventory();
        syncIds();
    }

    private void syncIds() {
        long maxPartId = 0;
        for (Part p : parts) if (p.getId() > maxPartId) maxPartId = p.getId();
        nextPartId.set(maxPartId + 1);
        long maxProductId = 0;
        for (Product p : products) if (p.getId() > maxProductId) maxProductId = p.getId();
        nextProductId.set(maxProductId + 1);
    }

    @Override
    public List<Part> findAllParts() {
        return new ArrayList<>(parts);
    }

    @Override
    public List<Product> findAllProducts() {
        return new ArrayList<>(products);
    }

    @Override
    public Part findPartById(long id) {
        return parts.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Product findProductById(long id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void savePart(Part part) {
        if (part.getId() == 0) {
            part.setId(nextPartId.getAndIncrement());
            parts.add(part);
        } else {
            parts.removeIf(p -> p.getId() == part.getId());
            parts.add(part);
        }
        saveInventory();
    }

    @Override
    public void saveProduct(Product product) {
        if (product.getId() == 0) {
            product.setId(nextProductId.getAndIncrement());
            products.add(product);
        } else {
            products.removeIf(p -> p.getId() == product.getId());
            products.add(product);
        }
        saveInventory();
    }

    @Override
    public void deletePart(Part part) {
        parts.removeIf(p -> p.getId() == part.getId());
        saveInventory();
    }

    @Override
    public void deleteProduct(Product product) {
        products.removeIf(p -> p.getId() == product.getId());
        saveInventory();
    }

    @Override
    public long getNextPartId() {
        return nextPartId.get();
    }

    @Override
    public long getNextProductId() {
        return nextProductId.get();
    }

    @Override
    public void loadInventory() {
        File f = new File(STORAGE_FILENAME);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            parts.clear();
            products.clear();
            int partCount = ois.readInt();
            for (int i = 0; i < partCount; i++) {
                Part p = (Part) ois.readObject();
                parts.add(p);
            }
            int productCount = ois.readInt();
            for (int i = 0; i < productCount; i++) {
                Product p = (Product) ois.readObject();
                if (p.getParts() != null) {
                    java.util.Set<Part> resolved = new java.util.HashSet<>();
                    for (Part part : p.getParts()) {
                        Part fromList = parts.stream().filter(x -> x.getId() == part.getId()).findFirst().orElse(part);
                        resolved.add(fromList);
                    }
                    p.setParts(resolved);
                }
                products.add(p);
            }
            syncIds();
        } catch (Exception e) {
            parts.clear();
            products.clear();
        }
    }

    @Override
    public void saveInventory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_FILENAME))) {
            oos.writeInt(parts.size());
            for (Part p : parts) oos.writeObject(p);
            oos.writeInt(products.size());
            for (Product p : products) oos.writeObject(p);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save inventory", e);
        }
    }
}
