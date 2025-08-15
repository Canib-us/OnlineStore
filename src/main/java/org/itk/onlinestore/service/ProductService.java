package org.itk.onlinestore.service;

import org.itk.onlinestore.entity.Product;
import org.itk.onlinestore.exception.ResourceNotFoundException;
import org.itk.onlinestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
    }

    public Product createProduct(Product product){
        product.setProductId(null);
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, Product product){
        Product oldProduct = findById(productId);

        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setQuantityInStock(product.getQuantityInStock());

        return productRepository.save(oldProduct);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id " + productId);
        }
        productRepository.deleteById(productId);
    }

    public List<Product> findProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public boolean isProductAvailable(Long productId) {
        return productRepository.isProductAvailable(productId);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByQuantityInStockGreaterThan(0);
    }
}
