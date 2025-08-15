package org.itk.onlinestore.repository;

import org.itk.onlinestore.entity.Customer;
import org.itk.onlinestore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    List<Order> findByCustomerCustomerId(Long customerId);
}
