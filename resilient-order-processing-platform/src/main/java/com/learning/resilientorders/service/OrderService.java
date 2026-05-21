package com.learning.resilientorders.service;

import com.learning.resilientorders.aspect.MonitoredOperation;
import com.learning.resilientorders.domain.OrderEntity;
import com.learning.resilientorders.domain.OrderStatus;
import com.learning.resilientorders.dto.CreateOrderRequest;
import com.learning.resilientorders.dto.OrderResponse;
import com.learning.resilientorders.exception.OrderNotFoundException;
import com.learning.resilientorders.messaging.OrderCreatedEvent;
import com.learning.resilientorders.messaging.WorkflowEventGateway;
import com.learning.resilientorders.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WorkflowEventGateway eventGateway;

    public OrderService(OrderRepository orderRepository, WorkflowEventGateway eventGateway) {
        this.orderRepository = orderRepository;
        this.eventGateway = eventGateway;
    }

    @Transactional
    @MonitoredOperation("order.place")
    public OrderResponse placeOrder(CreateOrderRequest request) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerEmail(request.customerEmail());
        entity.setItemSku(request.itemSku());
        entity.setQuantity(request.quantity());
        entity.setStatus(OrderStatus.PENDING_INVENTORY);
        entity.setCorrelationId(currentCorrelationId());

        OrderEntity saved = orderRepository.save(entity);
        eventGateway.publishOrderCreated(new OrderCreatedEvent(saved.getId(), saved.getCorrelationId()));
        return OrderResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    @MonitoredOperation("order.get")
    public OrderResponse getOrder(UUID id) {
        return OrderResponse.fromEntity(findOrder(id));
    }

    @Transactional(readOnly = true)
    @MonitoredOperation("order.list")
    public List<OrderResponse> listOrders() {
        return orderRepository.findAll()
            .stream()
            .map(OrderResponse::fromEntity)
            .toList();
    }

    @Transactional
    public void markFailed(UUID orderId, String reason) {
        OrderEntity order = findOrder(orderId);
        order.setStatus(OrderStatus.FAILED);
        order.setFailureReason(reason);
        orderRepository.save(order);
    }

    private OrderEntity findOrder(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private String currentCorrelationId() {
        String correlationId = MDC.get("correlationId");
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }
}
