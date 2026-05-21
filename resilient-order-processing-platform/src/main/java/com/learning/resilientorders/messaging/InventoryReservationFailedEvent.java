package com.learning.resilientorders.messaging;

import java.util.UUID;

public record InventoryReservationFailedEvent(UUID orderId, String reason, String correlationId) {
}
