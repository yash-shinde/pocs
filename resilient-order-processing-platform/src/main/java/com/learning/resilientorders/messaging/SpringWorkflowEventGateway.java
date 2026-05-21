package com.learning.resilientorders.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.rabbit-enabled", havingValue = "false", matchIfMissing = true)
public class SpringWorkflowEventGateway implements WorkflowEventGateway {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringWorkflowEventGateway(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishInventoryReserved(InventoryReservedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishInventoryReservationFailed(InventoryReservationFailedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
