package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(Product product);
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    void deleteProduct(Long id);
    Integer getStock(Long id);
    Product updateStock(Long id, Integer newStock);
}
