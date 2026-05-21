package com.learning.resilientorders.messaging;

import java.util.UUID;

public record InventoryReservedEvent(UUID orderId, String correlationId) {
}
