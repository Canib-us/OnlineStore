package org.itk.onlinestore.repository;

import org.itk.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByQuantityInStockGreaterThan(Integer quantity);
    @Query("SELECT CASE WHEN p.quantityInStock > 0 THEN true ELSE false END FROM Product p WHERE p.productId=:productId")
    boolean isProductAvailable(@Param("productId") Long productId);
}
