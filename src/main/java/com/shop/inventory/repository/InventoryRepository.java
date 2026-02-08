package com.shop.inventory.repository;

import com.shop.inventory.entity.Part;
import com.shop.inventory.entity.Product;

import java.util.List;

public interface InventoryRepository {

    List<Part> findAllParts();
    List<Product> findAllProducts();
    Part findPartById(long id);
    Product findProductById(long id);
    void savePart(Part part);
    void saveProduct(Product product);
    void deletePart(Part part);
    void deleteProduct(Product product);
    long getNextPartId();
    long getNextProductId();
    void loadInventory();
    void saveInventory();
}
