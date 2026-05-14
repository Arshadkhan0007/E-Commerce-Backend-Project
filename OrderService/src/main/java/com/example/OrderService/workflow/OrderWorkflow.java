package com.example.OrderService.workflow;

import com.example.OrderService.dto.OrderRequestDto;
import io.temporal.workflow.WorkflowInterface;

import java.util.List;

@WorkflowInterface
public interface OrderWorkflow {
    public void placeOrder(List<OrderRequestDto> orderRequestDtoList);
}
