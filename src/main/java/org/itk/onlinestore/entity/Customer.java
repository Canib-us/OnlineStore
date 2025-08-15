package org.itk.onlinestore.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank(message = "Имя покупателя не может быть пустым")
    @Size(max = 100, message = "Имя покупателя не может превышать 100 символов")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Фамилия покупателя не может быть пустой")
    @Size(max = 100, message = "Фамилия покупателя не может превышать 100 символов")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Email адрес обязателен")
    @Email(message = "Неверный формат email адреса")
    @Size(max = 255, message = "Email адрес не может превышать 255 символов")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Контактный номер обязателен")
    @Pattern(regexp = "^8\\d{10}$", message = "Номер должен состоять из 11 цифр и начинаться с 8")
    @Column(nullable = false)
    private String contactNumber;
}
