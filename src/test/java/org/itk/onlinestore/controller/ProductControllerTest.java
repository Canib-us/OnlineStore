package org.itk.onlinestore.controller;

import org.itk.onlinestore.entity.Product;
import org.itk.onlinestore.exception.ResourceNotFoundException;
import org.itk.onlinestore.service.ProductService;
import org.itk.onlinestore.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JsonUtil jsonUtil;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .productId(1L)
                .name("arasuka")
                .description("something")
                .price(new BigDecimal("1.99"))
                .quantityInStock(999)
                .build();

        Product anotherProduct = Product.builder()
                .productId(2L)
                .name("monopoly")
                .description("the game")
                .price(new BigDecimal("2.99"))
                .quantityInStock(250)
                .build();

        testProducts = Arrays.asList(testProduct, anotherProduct);
    }

    @Test
    void getAllProducts_returnJsonString() throws Exception {
        String expectedJson = "[{\"productId\":1,\"name\":\"arasuka\"}]";

        when(productService.findAll()).thenReturn(testProducts);
        when(jsonUtil.toJson(testProducts)).thenReturn(expectedJson);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(expectedJson));

        verify(productService).findAll();
        verify(jsonUtil).toJson(testProducts);
    }

    @Test
    void getProductById_WhenProductExists_returnJsonString() throws Exception {
        String expectedJson = "{\"productId\":1,\"name\":\"arasuka\"}";

        when(productService.findById(1L)).thenReturn(testProduct);
        when(jsonUtil.toJson(testProduct)).thenReturn(expectedJson);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(productService).findById(1L);
        verify(jsonUtil).toJson(testProduct);
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldReturn404() throws Exception {
        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Product Not Found"));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService).findById(999L);
        verify(jsonUtil, never()).toJson(any());
    }

    @Test
    void createProduct_WithValidData_returnCreatedProduct() throws Exception {
        String inputJson = "{\"name\":\"sword\",\"description\":\"ichigo`s sword\",\"price\":1499.99,\"quantityInStock\":1}";
        String outputJson = "{\"productId\":3,\"name\":\"sword\"}";

        Product newProduct = Product.builder()
                .name("sword")
                .description("ichigo`s sword")
                .price(new BigDecimal("1499.99"))
                .quantityInStock(1)
                .build();

        Product savedProduct = Product.builder()
                .productId(3L)
                .name("sword")
                .description("ichigo`s sword")
                .price(new BigDecimal("1499.99"))
                .quantityInStock(1)
                .build();

//        Product newProduct = new Product("Новый продукт", "Описание",
//                new BigDecimal("1499.99"), 15);
//        Product savedProduct = new Product("Новый продукт", "Описание",
//                new BigDecimal("1499.99"), 15);
//        savedProduct.setProductId(3L);

        when(jsonUtil.fromJson(inputJson, Product.class)).thenReturn(newProduct);
        when(productService.createProduct(newProduct)).thenReturn(savedProduct);
        when(jsonUtil.toJson(savedProduct)).thenReturn(outputJson);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().string(outputJson));

        verify(jsonUtil).fromJson(inputJson, Product.class);
        verify(productService).createProduct(newProduct);
        verify(jsonUtil).toJson(savedProduct);
    }

    @Test
    void createProduct_WithInvalidJson_throwException() throws Exception {
        String invalidJson = "{invalid json}";

        when(jsonUtil.fromJson(invalidJson, Product.class))
                .thenThrow(new IllegalStateException("JSON deserialization error"));

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isInternalServerError());

        verify(jsonUtil).fromJson(invalidJson, Product.class);
        verify(productService, never()).createProduct(any());
    }

    @Test
    void updateProduct_WithValidData_returnUpdatedProduct() throws Exception {
        String inputJson = "{\"name\":\"UpdProduct\",\"price\":99.99}";
        String outputJson = "{\"productId\":1,\"name\":\"UpdProduct\"}";

        Product updProduct = Product.builder()
                .name("UpdProduct")
                .description("UpdDescription")
                .price(new BigDecimal("99.99"))
                .quantityInStock(8)
                .build();

        Product savedProduct = Product.builder()
                .productId(1L)
                .name("UpdProduct")
                .description("UpdDescription")
                .price(new BigDecimal("99.99"))
                .quantityInStock(8)
                .build();


//        Product updateData = new Product("Обновленный продукт", "Новое описание",
//                new BigDecimal("1299.99"), 8);
//        Product updatedProduct = new Product("Обновленный продукт", "Новое описание",
//                new BigDecimal("1299.99"), 8);
//        updatedProduct.setProductId(1L);

        when(jsonUtil.fromJson(inputJson, Product.class)).thenReturn(updProduct);
        when(productService.updateProduct(eq(1L), eq(updProduct))).thenReturn(savedProduct);
        when(jsonUtil.toJson(savedProduct)).thenReturn(outputJson);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().string(outputJson));

        verify(jsonUtil).fromJson(inputJson, Product.class);
        verify(productService).updateProduct(1L, updProduct);
        verify(jsonUtil).toJson(savedProduct);
    }

    @Test
    void deleteProduct_WhenProductExists_returnSuccessMessage() throws Exception {
        String expectedJson = "{\"status\":\"deleted\"}";
        Map<String, String> statusMap = Map.of("status", "deleted");

        doNothing().when(productService).deleteProduct(1L);
        when(jsonUtil.toJson(statusMap)).thenReturn(expectedJson);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(productService).deleteProduct(1L);
        verify(jsonUtil).toJson(statusMap);
    }

    @Test
    void searchProductsByName_returnMatchingProducts() throws Exception {
        String expectedJson = "[{\"productId\":1,\"name\":\"arasuka\"}]";
        List<Product> searchResults = Arrays.asList(testProduct);

        when(productService.findProductsByName("arasuka")).thenReturn(searchResults);
        when(jsonUtil.toJson(searchResults)).thenReturn(expectedJson);

        mockMvc.perform(get("/products/search")
                        .param("name", "arasuka"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(productService).findProductsByName("arasuka");
        verify(jsonUtil).toJson(searchResults);
    }

    @Test
    void getAvailableProducts_returnAvailableProducts() throws Exception {
        String expectedJson = "[{\"productId\":1},{\"productId\":2}]";

        when(productService.getAvailableProducts()).thenReturn(testProducts);
        when(jsonUtil.toJson(testProducts)).thenReturn(expectedJson);

        mockMvc.perform(get("/products/available"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(productService).getAvailableProducts();
        verify(jsonUtil).toJson(testProducts);
    }

    @Test
    void checkProductAvailability_WhenAvailable_returnTrue() throws Exception {
        String expectedJson = "{\"available\":true}";
        Map<String, Boolean> availabilityMap = Map.of("available", true);

        when(productService.isProductAvailable(1L)).thenReturn(true);
        when(jsonUtil.toJson(availabilityMap)).thenReturn(expectedJson);

        mockMvc.perform(get("/products/1/available"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(productService).isProductAvailable(1L);
        verify(jsonUtil).toJson(availabilityMap);
    }

    @Test
    void checkProductAvailability_WhenNotAvailable_returnFalse() throws Exception {
        String expectedJson = "{\"available\":false}";
        Map<String, Boolean> availabilityMap = Map.of("available", false);

        when(productService.isProductAvailable(1L)).thenReturn(false);
        when(jsonUtil.toJson(availabilityMap)).thenReturn(expectedJson);

        mockMvc.perform(get("/products/1/available"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJson));

        verify(productService).isProductAvailable(1L);
        verify(jsonUtil).toJson(availabilityMap);
    }
}
