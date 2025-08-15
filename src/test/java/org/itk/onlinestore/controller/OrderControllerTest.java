package org.itk.onlinestore.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.itk.onlinestore.entity.Customer;
import org.itk.onlinestore.entity.Order;
import org.itk.onlinestore.entity.Product;
import org.itk.onlinestore.exception.InvalidOrderException;
import org.itk.onlinestore.exception.ResourceNotFoundException;
import org.itk.onlinestore.orderDto.OrderDto;
import org.itk.onlinestore.service.OrderService;
import org.itk.onlinestore.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JsonUtil jsonUtil;

    private Customer testCustomer;
    private Product testProduct;
    private Order testOrder;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .customerId(1L)
                .firstName("Yo")
                .lastName("Asakura")
                .email("ostorozhno@modern.ru")
                .contactNumber("88005553535")
                .build();

        testProduct = Product.builder()
                .productId(1L)
                .name("meat")
                .description("hamone")
                .price(new BigDecimal("999.99"))
                .quantityInStock(10)
                .build();

        testOrder = Order.builder()
                .orderId(1L)
                .customer(testCustomer)
                .products(Arrays.asList(testProduct))
                .shippingAddress("Novosibirsk, Red avenue, 73")
                .orderDate(LocalDateTime.now())
                .orderStatus(Order.OrderStatus.CONFIRMED)
                .build();

        orderDto = new OrderDto();
        orderDto.setCustomer(testCustomer);
        orderDto.setProductIds(Arrays.asList(1L));
        orderDto.setShippingAddress("Novosibirsk, Red avenue, 73");
    }

    @Test
    void createOrder_WithValidData_returnCreatedOrder() throws Exception {
        String inputJson = "{\"customer\":{\"firstName\":\"Yo\"},\"productIds\":[1],\"shippingAddress\":\"Novosibirsk, Red avenue, 73\"}";
        String outputJson = "{\"orderId\":1,\"customer\":{\"firstName\":\"Yo\"}}";

        when(jsonUtil.fromJson(inputJson, OrderDto.class)).thenReturn(orderDto);
        when(orderService.createOrder(orderDto)).thenReturn(testOrder);
        when(jsonUtil.toJson(testOrder)).thenReturn(outputJson);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(outputJson));

        verify(jsonUtil).fromJson(inputJson, OrderDto.class);
        verify(orderService).createOrder(orderDto);
        verify(jsonUtil).toJson(testOrder);
    }

    @Test
    void createOrder_WithInvalidJson_throwException() throws Exception {
        String invalidJson = "{invalid json}";

        when(jsonUtil.fromJson(invalidJson, OrderDto.class))
                .thenThrow(new IllegalStateException("JSON deserialization error"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError());

        verify(jsonUtil).fromJson(invalidJson, OrderDto.class);
        verify(orderService, never()).createOrder(any());
    }

    @Test
    void createOrder_WithUnavailableProduct_return400() throws Exception {
        String inputJson = "{\"customer\":{\"firstName\":\"Yo\"},\"productIds\":[999]}";

        when(jsonUtil.fromJson(inputJson, OrderDto.class)).thenReturn(orderDto);
        when(orderService.createOrder(orderDto))
                .thenThrow(new InvalidOrderException("Product not found"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isBadRequest());

        verify(jsonUtil).fromJson(inputJson, OrderDto.class);
        verify(orderService).createOrder(orderDto);
    }

    @Test
    void getOrderById_WhenOrderExists_returnOrder() throws Exception {
        String expectedJson = "{\"orderId\":1,\"customer\":{\"firstName\":\"Yo\"}}";

        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(jsonUtil.toJson(testOrder)).thenReturn(expectedJson);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(orderService).getOrderById(1L);
        verify(jsonUtil).toJson(testOrder);
    }

    @Test
    void getOrderById_WhenOrderNotExists_return404() throws Exception {
        when(orderService.getOrderById(999L))
                .thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999L);
        verify(jsonUtil, never()).toJson(any());
    }

    @Test
    void getAllOrders_returnAllOrders() throws Exception {
        String expectedJson = "[{\"orderId\":1}]";
        List<Order> orders = Arrays.asList(testOrder);

        when(orderService.getAllOrders()).thenReturn(orders);
        when(jsonUtil.toJson(orders)).thenReturn(expectedJson);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(orderService).getAllOrders();
        verify(jsonUtil).toJson(orders);
    }

    @Test
    void getOrdersByCustomerId_returnCustomerOrders() throws Exception {
        String expectedJson = "[{\"orderId\":1,\"customer\":{\"customerId\":1}}]";
        List<Order> orders = Arrays.asList(testOrder);

        when(orderService.getOrdersByCustomerId(1L)).thenReturn(orders);
        when(jsonUtil.toJson(orders)).thenReturn(expectedJson);

        mockMvc.perform(get("/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(orderService).getOrdersByCustomerId(1L);
        verify(jsonUtil).toJson(orders);
    }

    @Test
    void updateOrderStatus_WithValidStatus_returnUpdatedOrder() throws Exception {
        String inputJson = "{\"status\":\"CONFIRMED\"}";
        String outputJson = "{\"orderId\":1,\"orderStatus\":\"CONFIRMED\"}";
        Map<String, String> statusMap = Map.of("status", "CONFIRMED");

        Order updatedOrder = testOrder;
        updatedOrder.setOrderStatus(Order.OrderStatus.CONFIRMED);

        when(jsonUtil.fromJson(eq(inputJson), any(TypeReference.class))).thenReturn(statusMap);
        when(orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED)).thenReturn(updatedOrder);
        when(jsonUtil.toJson(updatedOrder)).thenReturn(outputJson);

        mockMvc.perform(patch("/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().string(outputJson));

        verify(jsonUtil).fromJson(eq(inputJson), any(TypeReference.class));
        verify(orderService).updateOrderStatus(1L, Order.OrderStatus.CONFIRMED);
        verify(jsonUtil).toJson(updatedOrder);
    }

    @Test
    void updateOrderStatus_WithInvalidStatus_throwException() throws Exception {
        String inputJson = "{\"status\":\"INVALID_STATUS\"}";
        Map<String, String> statusMap = Map.of("status", "INVALID_STATUS");

        when(jsonUtil.fromJson(eq(inputJson), any(TypeReference.class))).thenReturn(statusMap);

        mockMvc.perform(patch("/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isInternalServerError());

        verify(jsonUtil).fromJson(eq(inputJson), any(TypeReference.class));
        verify(orderService, never()).updateOrderStatus(anyLong(), any(Order.OrderStatus.class));
    }

    @Test
    void getOrdersByStatus_returnOrdersWithSpecificStatus() throws Exception {
        String expectedJson = "[{\"orderId\":1,\"orderStatus\":\"CONFIRMED\"}]";
        List<Order> orders = Arrays.asList(testOrder);

        when(orderService.getOrdersByStatus(Order.OrderStatus.CONFIRMED)).thenReturn(orders);
        when(jsonUtil.toJson(orders)).thenReturn(expectedJson);

        mockMvc.perform(get("/orders/status/CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(orderService).getOrdersByStatus(Order.OrderStatus.CONFIRMED);
        verify(jsonUtil).toJson(orders);
    }

    @Test
    void deleteOrder_WhenOrderCanBeDeleted_returnSuccessMessage() throws Exception {
        String expectedJson = "{\"status\":\"deleted\"}";
        Map<String, String> statusMap = Map.of("status", "deleted");

        doNothing().when(orderService).deleteOrder(1L);
        when(jsonUtil.toJson(statusMap)).thenReturn(expectedJson);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(orderService).deleteOrder(1L);
        verify(jsonUtil).toJson(statusMap);
    }

    @Test
    void deleteOrder_WhenOrderCannotBeDeleted_return400() throws Exception {
        doThrow(new InvalidOrderException("You can only delete orders with he status confirmed"))
                .when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isBadRequest());

        verify(orderService).deleteOrder(1L);
        verify(jsonUtil, never()).toJson(any());
    }
}
