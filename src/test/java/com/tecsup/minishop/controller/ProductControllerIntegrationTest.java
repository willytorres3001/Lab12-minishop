package com.tecsup.minishop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/products — debe crear un producto y retornar 201")
    void shouldCreateProductAndReturn201() throws Exception {
        Product product = Product.builder()
                .name("Webcam Logitech").price(250.00).stock(20).build();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Webcam Logitech"))
                .andExpect(jsonPath("$.price").value(250.00))
                .andExpect(jsonPath("$.stock").value(20));
    }

    @Test
    @DisplayName("GET /api/products — debe retornar lista de productos")
    void shouldReturnAllProducts() throws Exception {
        productRepository.save(Product.builder().name("Disco SSD").price(180.00).stock(10).build());
        productRepository.save(Product.builder().name("RAM 16GB").price(120.00).stock(8).build());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Disco SSD"))
                .andExpect(jsonPath("$[1].name").value("RAM 16GB"));
    }

    @Test
    @DisplayName("GET /api/products/{id} — debe retornar el producto correcto")
    void shouldReturnProductById() throws Exception {
        Product saved = productRepository.save(
                Product.builder().name("Impresora HP").price(350.00).stock(4).build());

        mockMvc.perform(get("/api/products/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Impresora HP"))
                .andExpect(jsonPath("$.price").value(350.00));
    }

    @Test
    @DisplayName("GET /api/products/{id} — debe retornar 500 cuando el ID no existe")
    void shouldReturn500WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().is5xxServerError());
    }
}