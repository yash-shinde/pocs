package com.learning.resilientorders.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
    @Email @NotBlank String customerEmail,
    @NotBlank String itemSku,
    @Min(1) int quantity
) {
}
