package com.learning.resilientorders.messaging;

import com.learning.resilientorders.config.RabbitMessagingConfig;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@ConditionalOnProperty(name = "app.messaging.rabbit-enabled", havingValue = "true")
public class RabbitWorkflowEventGateway implements WorkflowEventGateway {

    private final RabbitTemplate rabbitTemplate;

    public RabbitWorkflowEventGateway(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        afterCommit(() -> send(RabbitMessagingConfig.ORDERS_QUEUE, event));
    }

    @Override
    public void publishInventoryReserved(InventoryReservedEvent event) {
        afterCommit(() -> send(RabbitMessagingConfig.INVENTORY_QUEUE, event));
    }

    @Override
    public void publishInventoryReservationFailed(InventoryReservationFailedEvent event) {
        afterCommit(() -> send(RabbitMessagingConfig.NOTIFICATION_QUEUE, event));
    }

    private void afterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
            return;
        }
        action.run();
    }

    private void send(String routingKey, Object event) {
        rabbitTemplate.convertAndSend(RabbitMessagingConfig.ORDERS_EXCHANGE, routingKey, event, message -> {
            String correlationId = MDC.get("correlationId");
            if (correlationId != null && !correlationId.isBlank()) {
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setHeader("X-Correlation-Id", correlationId);
            }
            return message;
        });
    }
}
