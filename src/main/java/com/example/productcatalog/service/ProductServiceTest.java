package com.example.productcatalog.service;

import com.example.productcatalog.model.Product;
import com.example.productcatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository productRepository;
    private RedisTemplate<String, Object> redisTemplate;
    private ProductService productService;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepository.class);
        redisTemplate = mock(RedisTemplate.class);
        productService = new ProductService();
        productService.productRepository = productRepository;
        productService.redisTemplate = redisTemplate;
    }

    @Test
    public void testGetProductById_WhenCacheMiss_ShouldFetchFromDB() {
        Long productId = 1L;
        Product product = new Product("Test", "electronics", 500.0, 10);
        product.setId(productId);

        when(redisTemplate.opsForValue().get("product::" + productId)).thenReturn(null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(productId);
        assertNotNull(result);
        assertEquals("Test", result.getName());
    }
}
