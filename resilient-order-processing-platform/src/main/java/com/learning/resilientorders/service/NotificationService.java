package com.learning.resilientorders.service;

import com.learning.resilientorders.aspect.MonitoredOperation;
import com.learning.resilientorders.domain.OrderEntity;
import com.learning.resilientorders.domain.OrderStatus;
import com.learning.resilientorders.exception.OrderNotFoundException;
import com.learning.resilientorders.exception.TemporaryDependencyException;
import com.learning.resilientorders.repository.OrderRepository;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final OrderRepository orderRepository;
    private final Random random = new Random();

    @Value("${app.failure-simulation.notification-failure-rate:0.15}")
    private double failureRate;

    public NotificationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CircuitBreaker(name = "notification", fallbackMethod = "sendNotificationFallback")
    @Transactional
    @MonitoredOperation("notification.send")
    public void sendOrderNotification(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (shouldFail()) {
            throw new TemporaryDependencyException("Notification provider temporarily unavailable");
        }

        log.info("Simulated notification sent to {} for order {}", order.getCustomerEmail(), orderId);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    @Transactional
    public void sendNotificationFallback(UUID orderId, Throwable throwable) {
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.FAILED);
        order.setFailureReason("Notification failed: " + throwable.getMessage());
        orderRepository.save(order);
        log.warn("Notification fallback applied for order {} due to {}", orderId, throwable.getMessage());
    }

    private boolean shouldFail() {
        return random.nextDouble() < failureRate;
    }
}
