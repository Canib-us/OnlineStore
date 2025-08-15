package org.itk.onlinestore.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.itk.onlinestore.entity.Order;
import org.itk.onlinestore.orderDto.OrderDto;
import org.itk.onlinestore.service.OrderService;
import org.itk.onlinestore.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JsonUtil jsonUtil;

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrderById(@PathVariable("id") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(jsonUtil.toJson(order));
    }

    @GetMapping
    public ResponseEntity<String> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(jsonUtil.toJson(orders));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<String> getOrdersByCustomerId(@PathVariable("customerId") Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(jsonUtil.toJson(orders));
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody String json) {
        OrderDto orderDto = jsonUtil.fromJson(json, OrderDto.class);
        Order createdOrder = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonUtil.toJson(createdOrder));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable("id") Long orderId,
                                                    @RequestBody String json) {
        Map<String, String> map = jsonUtil.fromJson(json, new TypeReference<Map<String, String>>() {});
        Order.OrderStatus status = Order.OrderStatus.valueOf(map.get("status"));
        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(jsonUtil.toJson(updatedOrder));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<String> getOrdersByStatus(@PathVariable("status") Order.OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(jsonUtil.toJson(orders));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(jsonUtil.toJson(Map.of("status", "deleted")));
    }
}
