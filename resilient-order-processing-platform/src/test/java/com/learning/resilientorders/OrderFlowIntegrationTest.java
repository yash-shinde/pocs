package com.learning.resilientorders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.resilientorders.domain.OrderEntity;
import com.learning.resilientorders.domain.OrderStatus;
import com.learning.resilientorders.dto.CreateOrderRequest;
import com.learning.resilientorders.repository.OrderRepository;
import java.util.UUID;
import static java.lang.Thread.sleep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:orders;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.failure-simulation.inventory-failure-rate=0.0",
    "app.failure-simulation.notification-failure-rate=0.0",
    "app.messaging.rabbit-enabled=false"
})
@AutoConfigureMockMvc
class OrderFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void placingAnOrderCompletesTheAsyncWorkflow() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("customer@example.com", "SKU-123", 2);

        String responseJson = mockMvc.perform(
                post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isAccepted())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String orderIdText = objectMapper.readTree(responseJson).get("orderId").asText();
        UUID orderId = UUID.fromString(orderIdText);

        OrderEntity order = waitForOrder(orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.getFailureReason()).isNull();
    }

    @Test
    void createdOrderIsReadableThroughTheApi() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("reader@example.com", "SKU-321", 1);

        String responseJson = mockMvc.perform(
                post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isAccepted())
            .andReturn()
            .getResponse()
            .getContentAsString();

        UUID orderId = UUID.fromString(objectMapper.readTree(responseJson).get("orderId").asText());

        mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isOk());
    }

    private OrderEntity waitForOrder(UUID orderId) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        OrderEntity order = null;
        while (System.currentTimeMillis() < deadline) {
            order = orderRepository.findById(orderId).orElseThrow();
            if (order.getStatus() == OrderStatus.COMPLETED) {
                return order;
            }
            sleep(100);
        }
        return orderRepository.findById(orderId).orElseThrow();
    }
}
