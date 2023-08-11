package de.firecreeper82.shop.controller;

import de.firecreeper82.shop.exceptions.IdNotFoundException;
import de.firecreeper82.shop.model.ProductCreateRequest;
import de.firecreeper82.shop.model.ProductResponse;
import de.firecreeper82.shop.model.ProductUpdateRequest;
import de.firecreeper82.shop.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProductController {


    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public List<ProductResponse> getAllProducts(@RequestParam(required = false) String tag ) {
        return productRepository.findAll(tag);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        Optional<ProductResponse> product = productRepository.findById(id);

        return product
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable String id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products")
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        return productRepository.save(request);
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable String id) throws IdNotFoundException {
        final ProductResponse product = productRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Product with id " + id + " not found", HttpStatus.BAD_REQUEST));
        final ProductResponse updatedProduct = new ProductResponse(
                product.id(),
                (request.name() == null) ? product.name() : request.name(),
                (request.description() == null) ? product.description() : request.description(),
                (request.priceInCent() == null) ? product.priceInCent() : request.priceInCent(),
                product.tags()
        );

        return productRepository.save(updatedProduct);
    }
}
