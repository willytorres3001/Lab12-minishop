package com.tecsup.minishop.service;

import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceIntegrationTest {

        @Autowired
        private ProductService productService;

        @MockitoBean
        private ProductRepository productRepository;

        @Test
        @DisplayName("Debe guardar un producto válido correctamente")
        void shouldSaveValidProduct() {
                Product input = Product.builder()
                                .name("Auriculares Sony").price(320.00).stock(15).build();
                Product expected = Product.builder()
                                .id(1L).name("Auriculares Sony").price(320.00).stock(15).build();

                when(productRepository.save(any(Product.class))).thenReturn(expected);

                Product result = productService.save(input);

                assertThat(result.getId()).isEqualTo(1L);
                assertThat(result.getName()).isEqualTo("Auriculares Sony");
                verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el precio es cero o negativo")
        void shouldThrowExceptionWhenPriceIsInvalid() {
                Product product = Product.builder()
                                .name("Producto inválido").price(0.0).stock(5).build();

                assertThatThrownBy(() -> productService.save(product))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("El precio debe ser mayor a cero");
                verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el stock es negativo")
        void shouldThrowExceptionWhenStockIsNegative() {
                Product product = Product.builder()
                                .name("Producto sin stock").price(100.00).stock(-1).build();

                assertThatThrownBy(() -> productService.save(product))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("El stock no puede ser negativo");
        }

        @Test
        @DisplayName("Debe retornar todos los productos")
        void shouldReturnAllProducts() {
                List<Product> products = List.of(
                                Product.builder().id(1L).name("Producto A").price(100.0).stock(5).build(),
                                Product.builder().id(2L).name("Producto B").price(200.0).stock(3).build());
                when(productRepository.findAll()).thenReturn(products);

                List<Product> result = productService.findAll();

                assertThat(result).hasSize(2);
                verify(productRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el producto no existe por ID")
        void shouldThrowExceptionWhenProductNotFound() {
                when(productRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> productService.findById(99L))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("Producto no encontrado con id: 99");
        }
}