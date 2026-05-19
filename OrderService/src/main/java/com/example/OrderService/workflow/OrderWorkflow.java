package com.example.OrderService.workflow;

import com.example.OrderService.dto.OrderRequestDto;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface OrderWorkflow {
    @WorkflowMethod
    public void placeOrder(List<OrderRequestDto> orderRequestDtoList, String orderId);

    @SignalMethod
    public void cancelOrder();
}
