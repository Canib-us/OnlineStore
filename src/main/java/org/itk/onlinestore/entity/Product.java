package org.itk.onlinestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Название продукта не может быть пустым")
    @Size(max = 255, message = "Название продукта не может превышать 255 символов")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Описание продукта не может превышать 1000 символов")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Цена продукта обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше нуля")
    @Digits(integer = 10, fraction = 2, message = "Неверный формат цены")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Количество на складе обязательно")
    @Min(value = 0, message = "Количество на складе не может быть отрицательным")
    @Column(nullable = false)
    private Integer quantityInStock;
}
