package com.example.lab3.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemDto(
        Long id,
        @NotNull(message = "Product ID is required") Long productId,
        @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be positive") Integer quantity
) {
}
