package com.learning.resilientorders.messaging;

import com.learning.resilientorders.config.RabbitMessagingConfig;
import com.learning.resilientorders.service.InventoryService;
import com.learning.resilientorders.service.NotificationService;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.rabbit-enabled", havingValue = "true")
public class RabbitWorkflowListener {

    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    public RabbitWorkflowListener(InventoryService inventoryService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMessagingConfig.ORDERS_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event, @Header(name = "X-Correlation-Id", required = false) String correlationId) {
        runWithCorrelationId(correlationId, () -> inventoryService.reserveInventory(event.orderId()));
    }

    @RabbitListener(queues = RabbitMessagingConfig.INVENTORY_QUEUE)
    public void onInventoryReserved(InventoryReservedEvent event, @Header(name = "X-Correlation-Id", required = false) String correlationId) {
        runWithCorrelationId(correlationId, () -> notificationService.sendOrderNotification(event.orderId()));
    }

    private void runWithCorrelationId(String correlationId, Runnable action) {
        String previous = MDC.get("correlationId");
        if (correlationId != null && !correlationId.isBlank()) {
            MDC.put("correlationId", correlationId);
        }
        try {
            action.run();
        } finally {
            if (previous == null) {
                MDC.remove("correlationId");
            } else {
                MDC.put("correlationId", previous);
            }
        }
    }
}
