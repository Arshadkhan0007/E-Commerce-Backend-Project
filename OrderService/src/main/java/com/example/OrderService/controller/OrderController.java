package com.example.OrderService.controller;

import com.example.OrderService.dto.OrderRequestDto;
import com.example.OrderService.workflow.OrderWorkflow;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;
import org.springframework.http.HttpStatus;
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
                        .setWorkflowIdReusePolicy(WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_REJECT_DUPLICATE)
                        .build());
        try{
            WorkflowClient.start(orderWorkflow::placeOrder, orderRequestDtoList, orderId);
        } catch (WorkflowExecutionAlreadyStarted e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return ResponseEntity.accepted().body("Your order has been initiated");
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
