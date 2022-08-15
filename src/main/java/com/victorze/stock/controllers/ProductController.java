package com.victorze.stock.controllers;

import com.victorze.stock.dto.CreateProductDTO;
import com.victorze.stock.dto.ProductDTO;
import com.victorze.stock.dto.converter.ProductDTOConverter;
import com.victorze.stock.models.Category;
import com.victorze.stock.models.Product;
import com.victorze.stock.repositories.CategoryRepository;
import com.victorze.stock.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDTOConverter productDTOConverter;

    @GetMapping("/products")
    public ResponseEntity<?> getAll() {
        List<Product> result = productRepository.findAll();

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<ProductDTO> dtoList =
                    result.stream()
                            .map(productDTOConverter::convertToDTO)
                            .collect(Collectors.toList());
            return ResponseEntity.ok(dtoList);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Product result = productRepository.findById(id).orElse(null);

        if (result == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> newProduct(@RequestBody CreateProductDTO createProductDTO) {
        Product product = dtoToProduct(createProductDTO, new Product());
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> editProduct(@RequestBody CreateProductDTO createProductDTO, @PathVariable Long id) {
        return productRepository.findById(id).map(p -> {
            Product product = dtoToProduct(createProductDTO, p);
            return ResponseEntity.ok(productRepository.save(product));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Product dtoToProduct(CreateProductDTO createProductDTO, Product product) {
        product.setName(createProductDTO.getName());
        product.setPrice(createProductDTO.getPrice());
        Category category = categoryRepository.findById(createProductDTO.getCategoryId()).orElse(null);
        product.setCategory(category);
        return product;
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productRepository.deleteById(id);
        } finally {
            return ResponseEntity.noContent().build();
        }
    }

}
