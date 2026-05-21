package com.learning.resilientorders.dto;

import com.learning.resilientorders.domain.OrderEntity;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
    UUID orderId,
    String customerEmail,
    String itemSku,
    int quantity,
    String status,
    String failureReason,
    String correlationId,
    Instant createdAt,
    Instant updatedAt
) {
    public static OrderResponse fromEntity(OrderEntity entity) {
        return new OrderResponse(
            entity.getId(),
            entity.getCustomerEmail(),
            entity.getItemSku(),
            entity.getQuantity(),
            entity.getStatus().name(),
            entity.getFailureReason(),
            entity.getCorrelationId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
