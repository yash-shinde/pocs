package com.learning.resilientorders.messaging;

import com.learning.resilientorders.service.InventoryService;
import com.learning.resilientorders.service.NotificationService;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "app.messaging.rabbit-enabled", havingValue = "false", matchIfMissing = true)
public class WorkflowEventListener {

    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    public WorkflowEventListener(InventoryService inventoryService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
    }

    @Async("workflowExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        runWithCorrelationId(event.correlationId(), () -> inventoryService.reserveInventory(event.orderId()));
    }

    @Async("workflowExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInventoryReserved(InventoryReservedEvent event) {
        runWithCorrelationId(event.correlationId(), () -> notificationService.sendOrderNotification(event.orderId()));
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
