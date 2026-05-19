package com.example.OrderService.controller;

import com.example.OrderService.dto.OrderRequestDto;
import com.example.OrderService.workflow.OrderWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final WorkflowClient workflowClient;

    public OrderController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/place-order")
    public ResponseEntity<String> createOrder(@RequestBody List<OrderRequestDto> orderRequestDtoList) {

        String orderId = "ORD-" + UUID.randomUUID();

        OrderWorkflow orderWorkflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue("ORDER_TASK_QUEUE")
                        .setWorkflowId(orderId)
                        .build());
        WorkflowClient.start(orderWorkflow::placeOrder, orderRequestDtoList, orderId);
        return ResponseEntity.ok("Your order has been initiated");
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestParam String orderId) {
        OrderWorkflow orderWorkflow = workflowClient.newWorkflowStub(
                OrderWorkflow.class,
                orderId
        );
        orderWorkflow.cancelOrder();
        return ResponseEntity.ok("Order cancellation has been initiated");
    }

}
