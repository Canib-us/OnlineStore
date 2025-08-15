package org.itk.onlinestore.config;

import org.itk.onlinestore.entity.Customer;
import org.itk.onlinestore.entity.Product;
import org.itk.onlinestore.repository.CustomerRepository;
import org.itk.onlinestore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            initializeProducts();
        }

        if (customerRepository.count() == 0) {
            initializeCustomers();
        }
    }

    private void initializeProducts() {
        Product laptop = Product.builder()
                .name("Dell Latitude")
                .description("laptop for everyone")
                .price(new BigDecimal("599.99"))
                .quantityInStock(189)
                .build();

        Product burger = Product.builder()
                .name("Hello Kitty")
                .description("real brutal cat's meat")
                .price(new BigDecimal("1.99"))
                .quantityInStock(1828)
                .build();

        Product car = Product.builder()
                .name("BMW M6 GT coup")
                .description("600 hp")
                .price(new BigDecimal("129000.99"))
                .quantityInStock(3)
                .build();

        Product tv = Product.builder()
                .name("Samsung TV")
                .description("55, 4k")
                .price(new BigDecimal("340.99"))
                .quantityInStock(1828)
                .build();

        Product plumbum = Product.builder()
                .name("Plumbum")
                .description("new version")
                .price(new BigDecimal("0.99"))
                .quantityInStock(824)
                .build();

        productRepository.save(laptop);
        productRepository.save(burger);
        productRepository.save(car);
        productRepository.save(tv);
        productRepository.save(plumbum);

        System.out.println("testProducts has been init");
    }

    private void initializeCustomers() {
        Customer cust1 = Customer.builder()
                .firstName("Vinni")
                .lastName("the Pouh")
                .email("honey@mail.ru")
                .contactNumber("88005553535")
                .build();

        Customer cust2 = Customer.builder()
                .firstName("Mikki")
                .lastName("Mouse")
                .email("disney@gmail.com")
                .contactNumber("85555553535")
                .build();

        Customer cust3 = Customer.builder()
                .firstName("Minni")
                .lastName("Mouse")
                .email("spy@mail.ru")
                .contactNumber("80005553535")
                .build();

        customerRepository.save(cust1);
        customerRepository.save(cust2);
        customerRepository.save(cust3);

        System.out.println("Customers has been init");
    }
}
