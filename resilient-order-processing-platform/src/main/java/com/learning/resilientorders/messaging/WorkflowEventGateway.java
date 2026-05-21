package com.learning.resilientorders.messaging;

public interface WorkflowEventGateway {
    void publishOrderCreated(OrderCreatedEvent event);
    void publishInventoryReserved(InventoryReservedEvent event);
    void publishInventoryReservationFailed(InventoryReservationFailedEvent event);
}
