package com.learning.resilientorders.service;

import com.learning.resilientorders.aspect.MonitoredOperation;
import com.learning.resilientorders.domain.OrderEntity;
import com.learning.resilientorders.domain.OrderStatus;
import com.learning.resilientorders.exception.OrderNotFoundException;
import com.learning.resilientorders.exception.TemporaryDependencyException;
import com.learning.resilientorders.messaging.InventoryReservationFailedEvent;
import com.learning.resilientorders.messaging.InventoryReservedEvent;
import com.learning.resilientorders.messaging.WorkflowEventGateway;
import com.learning.resilientorders.repository.OrderRepository;
import java.util.Random;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.retry.annotation.Retry;

@Service
public class InventoryService {

    private final OrderRepository orderRepository;
    private final WorkflowEventGateway eventGateway;
    private final Random random = new Random();

    @Value("${app.failure-simulation.inventory-failure-rate:0.25}")
    private double failureRate;

    public InventoryService(OrderRepository orderRepository, WorkflowEventGateway eventGateway) {
        this.orderRepository = orderRepository;
        this.eventGateway = eventGateway;
    }

    @Retry(name = "inventory", fallbackMethod = "reserveInventoryFallback")
    @Transactional
    @MonitoredOperation("inventory.reserve")
    public void reserveInventory(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (shouldFail()) {
            throw new TemporaryDependencyException("Inventory service temporarily unavailable");
        }

        order.setStatus(OrderStatus.INVENTORY_RESERVED);
        order.setFailureReason(null);
        orderRepository.save(order);

        eventGateway.publishInventoryReserved(new InventoryReservedEvent(order.getId(), currentCorrelationId()));
    }

    @Transactional
    public void reserveInventoryFallback(UUID orderId, Throwable throwable) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.FAILED);
        order.setFailureReason("Inventory reservation failed: " + throwable.getMessage());
        orderRepository.save(order);

        eventGateway.publishInventoryReservationFailed(
            new InventoryReservationFailedEvent(orderId, order.getFailureReason(), currentCorrelationId())
        );
    }

    private boolean shouldFail() {
        return random.nextDouble() < failureRate;
    }

    private String currentCorrelationId() {
        return MDC.get("correlationId");
    }
}
