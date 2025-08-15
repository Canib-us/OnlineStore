package org.itk.onlinestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.itk.onlinestore.entity.Product;
import org.itk.onlinestore.service.ProductService;
import org.itk.onlinestore.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private JsonUtil jsonUtil;


    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody String json) throws JsonProcessingException {
        Product product = jsonUtil.fromJson(json, Product.class);
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.ok(jsonUtil.toJson(newProduct));
    }

    @GetMapping
    public ResponseEntity<String> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(jsonUtil.toJson(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getProductById(@PathVariable("id") Long productId) {
        Product product = productService.findById(productId);
        return ResponseEntity.ok(jsonUtil.toJson(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable("id") Long productId,
                                                @RequestBody String json) {
        Product productDetails = jsonUtil.fromJson(json, Product.class);
        Product updatedProduct = productService.updateProduct(productId, productDetails);
        return ResponseEntity.ok(jsonUtil.toJson(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(jsonUtil.toJson(Map.of("status", "deleted")));
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchProductsByName(@RequestParam("name") String name) {
        List<Product> products = productService.findProductsByName(name);
        return ResponseEntity.ok(jsonUtil.toJson(products));
    }

    @GetMapping("/available")
    public ResponseEntity<String> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(jsonUtil.toJson(products));
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<String> checkProductAvailability(@PathVariable("id") Long productId) {
        boolean isAvailable = productService.isProductAvailable(productId);
        return ResponseEntity.ok(jsonUtil.toJson(Map.of("available", isAvailable)));
    }
}
