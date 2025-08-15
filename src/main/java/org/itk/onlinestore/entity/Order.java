package org.itk.onlinestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull(message = "Покупатель обязателен")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotEmpty(message = "Список продуктов не может быть пустым")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @NotBlank(message = "Адрес доставки обязателен")
    @Size(max = 500, message = "Адрес доставки не может превышать 500 символов")
    @Column(nullable = false, length = 500)
    private String shippingAddress;

    @NotNull(message = "Общая стоимость заказа обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Общая стоимость должна быть больше нуля")
    @Digits(integer = 12, fraction = 2, message = "Неверный формат общей стоимости")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPrice;

    @NotNull(message = "Статус заказа обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Getter
    public enum OrderStatus {
        CONFIRMED("Подтвержден"),
        SHIPPED("Отправлен"),
        DELIVERED("Доставлен"),
        CANCELLED("Отменен");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

    }
}
