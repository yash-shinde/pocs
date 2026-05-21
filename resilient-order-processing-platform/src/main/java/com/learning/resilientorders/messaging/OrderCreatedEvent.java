package com.learning.resilientorders.messaging;

import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, String correlationId) {
}
