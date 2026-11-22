package com.example.lab3.service;

import com.example.lab3.dto.ProductDto;
import com.example.lab3.entity.Category;
import com.example.lab3.entity.Product;
import com.example.lab3.repository.CategoryRepository;
import com.example.lab3.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    @Transactional
    public ProductDto create(ProductDto dto) {
        Category category = findCategory(dto.categoryId());

        Product product = new Product();
        product.setName(dto.name());
        product.setPrice(toBigDecimal(dto.price()));
        product.setCategory(category);

        return toDto(productRepo.save(product));
    }

    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        return toDto(productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found")));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAll() {
        return productRepo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Pageable pageable) {
        return productRepo.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findByCategoryId(Long categoryId) {
        return productRepo.findByCategoryId(categoryId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchByName(String keyword) {
        return productRepo.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Category category = findCategory(dto.categoryId());

        product.setName(dto.name());
        product.setPrice(toBigDecimal(dto.price()));
        product.setCategory(category);

        return toDto(productRepo.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepo.existsById(id)) {
            throw new EntityNotFoundException("Product not found");
        }
        productRepo.deleteById(id);
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            throw new EntityNotFoundException("Category not found");
        }
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    private BigDecimal toBigDecimal(Double price) {
        return price == null ? null : BigDecimal.valueOf(price);
    }

    private ProductDto toDto(Product product) {
        Double price = product.getPrice() == null ? null : product.getPrice().doubleValue();
        Long categoryId = product.getCategory() == null ? null : product.getCategory().getId();
        return new ProductDto(product.getId(), product.getName(), price, categoryId);
    }
}
