package com.victorze.stock.controllers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.victorze.stock.dto.CreateProductDTO;
import com.victorze.stock.dto.ProductDTO;
import com.victorze.stock.dto.converter.ProductDTOConverter;
import com.victorze.stock.errors.ApiError;
import com.victorze.stock.errors.ProductNotFoundException;
import com.victorze.stock.models.Category;
import com.victorze.stock.models.Product;
import com.victorze.stock.repositories.CategoryRepository;
import com.victorze.stock.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public Product getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> newProduct(@RequestBody CreateProductDTO createProductDTO) {
        Product product = dtoToProduct(createProductDTO, new Product());
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @PutMapping("/products/{id}")
    public Product editProduct(@RequestBody CreateProductDTO createProductDTO, @PathVariable Long id) {
        return productRepository.findById(id).map(p -> {
            Product product = dtoToProduct(createProductDTO, p);
            return productRepository.save(product);
        }).orElseThrow(() -> new ProductNotFoundException(id));
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex) {
        ApiError apiError = createApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ApiError> handleJsonMappingException(JsonMappingException ex) {
        ApiError apiError = createApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    private ApiError createApiError(HttpStatus httpStatus, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(httpStatus);
        apiError.setDate(LocalDateTime.now());
        apiError.setMessage(message);
        return apiError;
    }

}
