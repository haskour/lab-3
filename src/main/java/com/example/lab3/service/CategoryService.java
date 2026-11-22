package com.example.lab3.service;

import com.example.lab3.dto.CategoryDto;
import com.example.lab3.dto.ProductDto;
import com.example.lab3.entity.Category;
import com.example.lab3.entity.Product;
import com.example.lab3.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.name());
        return toDto(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        return toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found")));
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findProductsByCategoryId(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return category.getProducts().stream()
                .map(this::toProductDto)
                .toList();
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    private ProductDto toProductDto(Product product) {
        Double price = product.getPrice() == null ? null : product.getPrice().doubleValue();
        Long categoryId = product.getCategory() == null ? null : product.getCategory().getId();
        return new ProductDto(product.getId(), product.getName(), price, categoryId);
    }
}
