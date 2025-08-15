package org.itk.onlinestore.orderDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.itk.onlinestore.entity.Customer;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotNull(message = "Информация о покупателе обязательна")
    @Valid
    private Customer customer;

    @NotEmpty(message = "Список продуктов не может быть пустым")
    private List<Long> productIds;

    @NotBlank(message = "Адрес доставки обязателен")
    @Size(max = 500, message = "Адрес доставки не может превышать 500 символов")
    private String shippingAddress;
}
