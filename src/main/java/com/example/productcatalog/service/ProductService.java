package com.example.productcatalog.service;

import com.example.productcatalog.model.Product;
import com.example.productcatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductService {
    public static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_KEY_PREFIX = "product::";
    private static final String FILTERED_LIST_PREFIX = "product::list::";

    public List<Product> getAllProducts() {
        String key = "product::all";
        List<Product> cached = (List<Product>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("Cache HIT for get all product");
            return cached;
        }
        log.info("Cache MISS for get all product");

        List<Product> products = productRepository.findAll();
        redisTemplate.opsForValue().set(key, products, 10, TimeUnit.MINUTES);
        return products;
    }

    public Product getProductById(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        Product cached = (Product) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("Cache HIT for product ID: {}", id);
            return cached;
        }
        log.info("Cache MISS for product ID: {}", id);

        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(p -> redisTemplate.opsForValue().set(key, p));
        return product.orElse(null);
    }

    public List<Product> getFiltered(String category, Double min, Double max) {
        String key = FILTERED_LIST_PREFIX + category + "::" + min + "::" + max;
        List<Product> cached = (List<Product>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("Cache HIT for get all product by filter");
            return cached;
        }
        log.info("Cache MISS for filter");

        List<Product> products = productRepository.findByCategoryAndPriceBetween(category, min, max);
        redisTemplate.opsForValue().set(key, products, 10, TimeUnit.MINUTES);
        return products;
    }

    public Product saveProduct(Product product) {
        Product saved = productRepository.save(product);
        redisTemplate.delete(PRODUCT_KEY_PREFIX + saved.getId());
        clearFilteredCaches();
        redisTemplate.delete("product::all");
        return saved;
    }

    public Product updateProduct(Long id, Product updated) {
        return productRepository.findById(id).map(prod -> {
            prod.setName(updated.getName());
            prod.setCategory(updated.getCategory());
            prod.setPrice(updated.getPrice());
            prod.setStock(updated.getStock());
            redisTemplate.delete(PRODUCT_KEY_PREFIX + id);
            clearFilteredCaches();
            redisTemplate.delete("product::all");
            return productRepository.save(prod);
        }).orElse(null);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        redisTemplate.delete(PRODUCT_KEY_PREFIX + id);
        clearFilteredCaches();
        redisTemplate.delete("product::all");
    }

    public void clearFilteredCaches() {
        Set<String> keys = redisTemplate.keys("product::list::*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
