package org.itk.onlinestore.service;

import org.itk.onlinestore.entity.Customer;
import org.itk.onlinestore.entity.Order;
import org.itk.onlinestore.entity.Product;
import org.itk.onlinestore.exception.InvalidOrderException;
import org.itk.onlinestore.exception.ResourceNotFoundException;
import org.itk.onlinestore.orderDto.OrderDto;
import org.itk.onlinestore.repository.CustomerRepository;
import org.itk.onlinestore.repository.OrderRepository;
import org.itk.onlinestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getOrderStatus() != Order.OrderStatus.CONFIRMED) {
            throw new InvalidOrderException("Можно удалить только заказы в статусе CONFIRMED");
        }
        orderRepository.deleteById(orderId);
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerCustomerId(customerId);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    private Customer findOrCreateCustomer(Customer customerData) {
        return customerRepository.findByEmail(customerData.getEmail())
                .orElseGet(() -> customerRepository.save(customerData));
    }

    private Product validateAndGetProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));

        if (product.getQuantityInStock() <= 0) {
            throw new InvalidOrderException("Product quantity is less than 0");
        }
        return product;
    }

    private BigDecimal calculateTotalPrice(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order createOrder(OrderDto orderDto) {
        Customer customer = findOrCreateCustomer(orderDto.getCustomer());

        List<Product> products = orderDto.getProductIds().stream()
                .map(this::validateAndGetProduct)
                .collect(Collectors.toList());

        BigDecimal totalPrice = calculateTotalPrice(products);

        Order order = Order.builder()
                .customer(customer)
                .products(products)
                .shippingAddress(orderDto.getShippingAddress())
                .totalPrice(totalPrice)
                .orderDate(LocalDateTime.now())
                .orderStatus(Order.OrderStatus.CONFIRMED)
                .build();

        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
